package com.xuecheng.content.service.impl;

import com.alibaba.fastjson2.JSON;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.mapper.MqMessageMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.system.model.enums.CourseAuditStatus;
import com.xuecheng.system.model.enums.CoursePublishStatus;
import com.xuecheng.system.model.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CoursePublishServiceImpl implements CoursePublishService {

    private final CourseBaseService courseBaseService;
    private final TeachplanService teachplanService;

    private final CourseMarketMapper courseMarketMapper;
    private final CoursePublishPreMapper coursePublishPreMapper;
    private final CoursePublishMapper coursePublishMapper;
    private final MqMessageMapper mqMessageMapper;

    @Override
    public CoursePreviewDTO getCoursePreview(Long courseId) {
        CourseBaseInfoDTO courseBaseInfoDTO = courseBaseService.getCourseBaseInfoById(courseId);
        List<TeachplanTreeNodeDTO> tree = teachplanService.getTeachplanTreeNodesByCourseId(courseId);
        return CoursePreviewDTO.builder()
                .courseBase(courseBaseInfoDTO)
                .teachplans(tree)
                .build();
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseService.getById(courseId);

        if (courseBase == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        if (!courseBase.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("只能提交本机构的课程");
        }

        if (Objects.equals(courseBase.getAuditStatus(), CourseAuditStatus.SUBMITTED.getCode())) {
            throw new IllegalArgumentException("课程已提交，请勿重复提交");
        }

        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseBaseInfoDTO courseBaseInfoDTO = courseBaseService.getCourseBaseInfoById(courseId);
        BeanUtils.copyProperties(courseBaseInfoDTO, coursePublishPre);

        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));

        List<TeachplanTreeNodeDTO> tree = teachplanService.getTeachplanTreeNodesByCourseId(courseId);
        if (tree == null || tree.isEmpty()) {
            throw new IllegalArgumentException("课程计划为空，无法提交");
        }
        coursePublishPre.setTeachplan(JSON.toJSONString(tree));

        coursePublishPre.setStatus(CourseAuditStatus.SUBMITTED.getCode());
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPreMapper.insertOrUpdate(coursePublishPre);

        courseBase.setAuditStatus(CourseAuditStatus.SUBMITTED.getCode());
        courseBaseService.updateById(courseBase);
    }

    @Transactional
    @Override
    public void coursePublish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            throw new IllegalArgumentException("请先提交审核");
        }
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("只能发布本机构的课程");
        }
        if (!Objects.equals(coursePublishPre.getStatus(), CourseAuditStatus.APPROVED.getCode())) {
            throw new IllegalArgumentException("课程未审核通过，无法发布");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus(CoursePublishStatus.PUBLISHED.getCode());
        coursePublishMapper.insertOrUpdate(coursePublish);

        CourseBase courseBase = courseBaseService.getById(courseId);
        courseBase.setStatus(CoursePublishStatus.PUBLISHED.getCode());
        courseBaseService.updateById(courseBase);

        MqMessage mqMessage = MqMessage.builder()
                .messageType(MessageType.COURSE_PUBLISH.getCode())
                .businessKey1(courseId.toString())
                .build();
        mqMessageMapper.insert(mqMessage);

        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public void processCoursePublishTask(MqMessage mqMessage) {
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        // TODO 生成静态化页面并上传至文件系统

        // TODO 课程索引

        // TODO 课程缓存
    }
}
