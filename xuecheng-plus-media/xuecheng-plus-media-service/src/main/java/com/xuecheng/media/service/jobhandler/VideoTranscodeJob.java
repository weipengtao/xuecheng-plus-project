package com.xuecheng.media.service.jobhandler;

import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoTranscodeJob {

    private final MediaProcessService mediaProcessService;

    @XxlJob("videoTranscodeJob")
    public void videoTranscodeJob() {
        log.info("视频转码任务开始执行...");

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        List<MediaProcess> mediaProcesses = mediaProcessService.getPendingVideoTasks(
                shardIndex, shardTotal, Integer.MAX_VALUE); // 可以处理任意数量的任务

        if (mediaProcesses == null || mediaProcesses.isEmpty()) {
            log.info("没有待处理的视频转码任务.");
            return;
        }

        // ✅ 使用虚拟线程执行器 - 可以处理成千上万的任务
        try (ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<Void>> futures = mediaProcesses.stream()
                    .map(mediaProcess -> CompletableFuture.runAsync(() ->
                            mediaProcessService.processMediaTask(mediaProcess), virtualThreadExecutor))
                    .toList();

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(30, TimeUnit.MINUTES)  // 30分钟超时
                    .join();

        } catch (Exception e) {
            log.error("任务执行异常: {}", e.getMessage());
        }

        log.info("视频转码任务执行完成.");
    }
}
