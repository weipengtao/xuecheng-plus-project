package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author weipengtao
 * @since 2025-09-16
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<Teachplan> selectByCourseId(String courseId);
}
