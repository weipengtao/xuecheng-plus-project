package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.MqMessage;

import java.util.List;

public interface MqMessageService extends IService<MqMessage> {
    List<MqMessage> getPendingMessages(String type, int shardIndex, int shardTotal, int maxValue);
}
