package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaProcessService;
import com.xuecheng.media.service.MinioService;
import com.xuecheng.utils.VideoTranscoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    private final MinioService minioService;

    private final MediaFilesMapper mediaFilesMapper;
    private final MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public List<MediaProcess> getPendingVideoTasks(int shardIndex, int shardTotal, int processorCount) {
        return this.lambdaQuery()
                .apply("id % {0} = {1}", shardTotal, shardIndex)
                .in(MediaProcess::getStatus, "1", "3")
                .lt(MediaProcess::getFailCount, 3)
                .orderByAsc(MediaProcess::getCreateDate)
                .last("limit " + processorCount)
                .list();
    }

    @Override
    public void processMediaTask(MediaProcess mediaProcess) {
        boolean updated = this.lambdaUpdate()
                .set(MediaProcess::getStatus, "4")
                .eq(MediaProcess::getId, mediaProcess.getId())
                .in(MediaProcess::getStatus, "1", "3")
                .update();
        if (!updated) {
            log.debug("任务已被其他节点处理, 任务ID: {}", mediaProcess.getId());
            return;
        }

        File originFile;
        try {
            originFile = minioService.downloadTempFile(mediaProcess.getBucket(), mediaProcess.getFilePath());
        } catch (Exception e) {
            this.markTaskFailed(mediaProcess.getId(), "下载源文件失败", e);
            throw new RuntimeException(e);
        }

        File mp4File;
        try {
            mp4File = File.createTempFile(mediaProcess.getFileId(), ".mp4");
        } catch (IOException e) {
            this.markTaskFailed(mediaProcess.getId(), "创建临时文件失败", e);
            throw new RuntimeException(e);
        }

        VideoTranscoderUtil.TranscodeResult transcodeResult = VideoTranscoderUtil.transcode(originFile, mp4File);
        if (!transcodeResult.isSuccess()) {
            this.markTaskFailed(mediaProcess.getId(), "视频转码失败: " + transcodeResult.getMessage());
            return;
        }

        String originObjectName = mediaProcess.getFilePath();
        String mp4ObjectName = originObjectName.substring(0, mediaProcess.getFilePath().lastIndexOf(".")) + ".mp4";
        try {
            minioService.uploadFile(mediaProcess.getBucket(), mp4ObjectName, mp4File);
        } catch (Exception e) {
            this.markTaskFailed(mediaProcess.getId(), "上传转码后文件失败", e);
            throw new RuntimeException(e);
        }

        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaProcess.getFileId());
        if (mediaFiles == null) {
            this.markTaskFailed(mediaProcess.getId(), "找不到对应的MediaFiles记录");
            return;
        }
        String url = "/" + mediaProcess.getBucket() + "/" + mp4ObjectName;
        mediaFiles.setUrl(url);
        mediaFiles.setFilePath(mp4ObjectName);
        mediaFilesMapper.updateById(mediaFiles);

        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistory.setId(null);
        mediaProcessHistory.setUrl(url);
        mediaProcessHistory.setFilePath(mp4ObjectName);
        mediaProcessHistory.setFinishDate(LocalDateTime.now());
        mediaProcessHistory.setStatus("2");
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        this.removeById(mediaProcess.getId());
        try {
            minioService.removeObject(mediaProcess.getBucket(), originObjectName);
        } catch (Exception ignored) {
            // 处理已完成, 删除不了 minio 的原始文件不影响业务
        }
    }

    /**
     * 统一处理任务失败
     */
    private void markTaskFailed(Long taskId, String reason) {
        markTaskFailed(taskId, reason, null);
    }

    private void markTaskFailed(Long taskId, String reason, Exception e) {
        if (e != null) {
            log.error("{}，任务ID: {}", reason, taskId, e);
        } else {
            log.error("{}，任务ID: {}", reason, taskId);
        }
        try {
            this.lambdaUpdate()
                    .set(MediaProcess::getStatus, "3")
                    .setSql("fail_count = fail_count + 1")
                    .set(MediaProcess::getErrormsg, reason)
                    .eq(MediaProcess::getId, taskId)
                    .update();
        } catch (Exception ex) {
            log.error("标记任务失败异常, 任务ID: {}", taskId, ex);
        }
    }
}
