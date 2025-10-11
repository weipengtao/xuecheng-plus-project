package com.xuecheng.content.service.jobhandler;

import com.xuecheng.content.model.po.MqMessage;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.MqMessageService;
import com.xuecheng.system.model.enums.MessageType;
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
public class CoursePublishJob {

    private final CoursePublishService coursePublishService;
    private final MqMessageService mqMessageService;

    @XxlJob("coursePublishJob")
    public void coursePublishJob() {
        log.info("课程发布任务开始执行...");

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        List<MqMessage> mqMessages = mqMessageService.getPendingMessages(
                MessageType.COURSE_PUBLISH.getCode(), shardIndex, shardTotal, Integer.MAX_VALUE);


        if (mqMessages == null || mqMessages.isEmpty()) {
            log.info("没有待处理的课程发布任务.");
            return;
        }

        // ✅ 使用虚拟线程执行器 - 可以处理成千上万的任务
        try (ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<Void>> futures = mqMessages.stream()
                    .map(mqMessage -> CompletableFuture.runAsync(() ->
                            coursePublishService.processCoursePublishTask(mqMessage), virtualThreadExecutor))
                    .toList();

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(30, TimeUnit.MINUTES)  // 30分钟超时
                    .join();

        } catch (Exception e) {
            log.error("任务执行异常: {}", e.getMessage(), e);
        }

        log.info("课程发布任务执行完成.");
    }
}
