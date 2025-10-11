package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    private final CourseBaseMapper courseBaseMapper;
    private final CourseMarketMapper courseMarketMapper;
    private final CourseCategoryMapper courseCategoryMapper;

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

    @Override
    @Transactional
    public CourseBaseInfoDTO createCourseBase(Long companyId, AddCourseDTO addCourseDTO) {
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDTO, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setAuditStatus("202002"); // TODO 暂时写成魔法值
        courseBase.setStatus("203001");
        courseBase.setCreateDate(LocalDateTime.now());  // TODO 待实现自动填充
        courseBaseMapper.insert(courseBase);

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDTO, courseMarket);
        courseMarket.setId(courseBase.getId());
        courseMarketMapper.insertOrUpdate(courseMarket);

        return getCourseBaseInfoById(courseBase.getId());
    }

    @Override
    public CourseBaseInfoDTO getCourseBaseInfoById(Long id) {
        CourseBaseInfoDTO courseBaseInfoDTO = new CourseBaseInfoDTO();

        CourseBase courseBase = courseBaseMapper.selectById(id);
        BeanUtils.copyProperties(courseBase, courseBaseInfoDTO);
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDTO);
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDTO.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDTO.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDTO;
    }

    @Override
    public CourseBaseInfoDTO updateCourseBase(Long companyId, EditCourseDTO editCourseDTO) {
        Long courseId = editCourseDTO.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!courseBase.getCompanyId().equals(companyId)) {
            throw new RuntimeException("不允许修改其他机构的课程");
        }

        BeanUtils.copyProperties(editCourseDTO, courseBase);
        courseBase.setChangeDate(LocalDateTime.now()); // TODO 待实现自动填充
        courseBaseMapper.updateById(courseBase);

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDTO, courseMarket);
        courseMarket.setId(courseId);
        courseMarketMapper.insertOrUpdate(courseMarket);

        return getCourseBaseInfoById(courseId);
    }

    @Override
    public void deleteById(Long id) {
        courseBaseMapper.deleteById(id);
    }
}
