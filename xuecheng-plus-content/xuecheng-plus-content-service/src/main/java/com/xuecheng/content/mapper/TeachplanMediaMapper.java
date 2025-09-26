package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author weipengtao
 * @since 2025-09-16
 */
public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {

    @Delete("delete from teachplan_media where teachplan_id = #{teachplanId}")
    void deleteByTeachplanId(Long teachplanId);
}
