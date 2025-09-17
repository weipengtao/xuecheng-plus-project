package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseBaseServiceImpl implements CourseBaseService {

    private final CourseBaseMapper courseBaseMapper;

    @Override
    public PageResult<CourseBase> pageList(PageParams pageParams, QueryCourseParamsDTO queryCourseParamsDTO) {
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        String courseName = queryCourseParamsDTO.getCourseName();
        String auditStatus = queryCourseParamsDTO.getAuditStatus();
        String publishStatus = queryCourseParamsDTO.getPublishStatus();

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(courseName != null, CourseBase::getName, courseName);
        queryWrapper.eq(auditStatus != null && !auditStatus.isEmpty(), CourseBase::getAuditStatus, auditStatus);
        queryWrapper.eq(publishStatus != null && !publishStatus.isEmpty(), CourseBase::getStatus, publishStatus);

        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);

        return PageResult.<CourseBase>builder()
                .counts(courseBasePage.getTotal())
                .page(pageParams.getPageNo())
                .pageSize(pageParams.getPageSize())
                .items(courseBasePage.getRecords())
                .build();
    }
}
