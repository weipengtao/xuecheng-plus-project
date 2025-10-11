package com.xuecheng.content.service.impl;

import com.alibaba.fastjson2.JSON;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.system.model.enums.CourseAuditStatus;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
