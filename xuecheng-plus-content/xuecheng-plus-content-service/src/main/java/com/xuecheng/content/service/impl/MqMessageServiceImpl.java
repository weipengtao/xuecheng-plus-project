package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.MqMessageMapper;
import com.xuecheng.content.model.po.MqMessage;
import com.xuecheng.content.service.MqMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {
    @Override
    public List<MqMessage> getPendingMessages(String type, int shardIndex, int shardTotal, int maxValue) {
        return this.lambdaQuery()
                .apply("id % {0} = {1}", shardTotal, shardIndex)
                .eq(MqMessage::getMessageType, type)
                .last("limit " + maxValue)
                .list();
    }
}
