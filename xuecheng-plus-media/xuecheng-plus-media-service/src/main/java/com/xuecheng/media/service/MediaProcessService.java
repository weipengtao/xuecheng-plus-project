package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

public interface MediaProcessService extends IService<MediaProcess> {
    List<MediaProcess> getPendingVideoTasks(int shardIndex, int shardTotal, int processorCount);

    void processMediaTask(MediaProcess mediaProcess);
}
