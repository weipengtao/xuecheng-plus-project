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
            log.error("下载源文件失败, 任务ID: {}, 错误信息: {}", mediaProcess.getId(), e.getMessage());
            throw new RuntimeException(e);
        }

        File mp4File;
        try {
            mp4File = File.createTempFile(mediaProcess.getFileId(), ".mp4");
        } catch (IOException e) {
            log.error("创建临时文件失败, 任务ID: {}, 错误信息: {}", mediaProcess.getId(), e.getMessage());
            throw new RuntimeException(e);
        }

        VideoTranscoderUtil.TranscodeResult transcodeResult = VideoTranscoderUtil.transcode(originFile, mp4File);
        if (!transcodeResult.isSuccess()) {
            log.error("视频转码失败, 任务ID: {}, 错误信息: {}", mediaProcess.getId(), transcodeResult.getMessage());
            this.lambdaUpdate()
                    .set(MediaProcess::getStatus, "3")
                    .setSql("fail_count = fail_count + 1")
                    .eq(MediaProcess::getId, mediaProcess.getId())
                    .update();
            return;
        }

        String mp4ObjectName = mediaProcess.getFilePath().substring(0, mediaProcess.getFilePath().lastIndexOf(".")) + ".mp4";
        try {
            minioService.uploadFile(mediaProcess.getBucket(), mp4ObjectName, mp4File);
        } catch (Exception e) {
            log.error("上传转码后文件失败, 任务ID: {}, 错误信息: {}", mediaProcess.getId(), e.getMessage());
            this.lambdaUpdate()
                    .set(MediaProcess::getStatus, "3")
                    .setSql("fail_count = fail_count + 1")
                    .eq(MediaProcess::getId, mediaProcess.getId())
                    .update();
            throw new RuntimeException(e);
        }

        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaProcess.getFileId());
        if (mediaFiles == null) {
            log.error("找不到对应的MediaFiles记录, 任务ID: {}, 文件ID: {}", mediaProcess.getId(), mediaProcess.getFileId());
            this.lambdaUpdate()
                    .set(MediaProcess::getStatus, "3")
                    .setSql("fail_count = fail_count + 1")
                    .eq(MediaProcess::getId, mediaProcess.getId())
                    .update();
            return;
        }
        String url = "/" + mediaProcess.getBucket() + "/" + mp4ObjectName;
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);

        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistory.setId(null);
        mediaProcessHistory.setUrl(url);
        mediaProcessHistory.setFinishDate(LocalDateTime.now());
        mediaProcessHistory.setStatus("2");
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        this.removeById(mediaProcess.getId());
    }
}
