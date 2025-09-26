package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.AssociateMediaDTO;
import com.xuecheng.content.model.dto.EditTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    private final TeachplanMapper teachplanMapper;
    private final TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanTreeNodeDTO> getTeachplanTreeNodesByCourseId(String courseId) {
        List<Teachplan> teachplans = teachplanMapper.selectByCourseId(courseId);

        List<TeachplanTreeNodeDTO> treeNodeDTOS = teachplans.stream().map(teachplan -> {
            TeachplanTreeNodeDTO teachplanTreeNodeDTO = new TeachplanTreeNodeDTO();
            BeanUtils.copyProperties(teachplan, teachplanTreeNodeDTO);
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
            TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(queryWrapper);
            teachplanTreeNodeDTO.setTeachplanMedia(teachplanMedia);
            return teachplanTreeNodeDTO;
        }).toList();

        Map<Long, TeachplanTreeNodeDTO> map = treeNodeDTOS.stream().collect(Collectors.toMap(TeachplanTreeNodeDTO::getId, teachplanTreeNodeDTO -> teachplanTreeNodeDTO));

        List<TeachplanTreeNodeDTO> tree = new ArrayList<>();

        for (TeachplanTreeNodeDTO treeNodeDTO : treeNodeDTOS) {
            if (treeNodeDTO.getParentid() == 0) {
                tree.add(treeNodeDTO);
                continue;
            }
            TeachplanTreeNodeDTO parent = map.get(treeNodeDTO.getParentid());
            if (parent.getTeachPlanTreeNodes() == null) {
                parent.setTeachPlanTreeNodes(new ArrayList<>());
            }
            parent.getTeachPlanTreeNodes().add(treeNodeDTO);
        }

        return tree;
    }

    @Override
    public void addOrUpdateTeachplan(EditTeachplanDTO editTeachplanDTO) {
        Teachplan teachplan = new Teachplan();
        BeanUtils.copyProperties(editTeachplanDTO, teachplan);
        teachplanMapper.insertOrUpdate(teachplan);
    }

    @Override
    @Transactional
    public void deleteTeachplanById(Long id) {
        Long count = teachplanMapper.selectCount(new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getParentid, id));
        if (count > 0) {
            throw new RuntimeException("Cannot delete teachplan with child nodes");
        }
        teachplanMapper.deleteById(id);
        teachplanMediaMapper.deleteByTeachplanId(id);
    }

    @Override
    @Transactional
    public void moveDown(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .gt(Teachplan::getOrderby, teachplan.getOrderby())
                .orderByAsc(Teachplan::getOrderby);
        Teachplan teachplanDown = teachplanMapper.selectList(queryWrapper).getFirst();
        if (teachplanDown == null) {
            return;
        }
        int tempOrder = teachplan.getOrderby();
        teachplan.setOrderby(teachplanDown.getOrderby());
        teachplanDown.setOrderby(tempOrder);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanDown);
    }

    @Override
    @Transactional
    public void moveUp(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .lt(Teachplan::getOrderby, teachplan.getOrderby())
                .orderByDesc(Teachplan::getOrderby);
        Teachplan teachplanUp = teachplanMapper.selectList(queryWrapper).getFirst();
        if (teachplanUp == null) {
            return;
        }
        int tempOrder = teachplan.getOrderby();
        teachplan.setOrderby(teachplanUp.getOrderby());
        teachplanUp.setOrderby(tempOrder);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanUp);
    }

    @Override
    @Transactional
    public TeachplanMedia associateMedia(AssociateMediaDTO associateMediaDTO) {
        Long teachplanId = associateMediaDTO.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            throw new RuntimeException("未找到课程计划, ID: " + teachplanId);
        }
        if (teachplan.getGrade() != 2) {
            throw new RuntimeException("课程计划不是二级, 不能绑定媒资");
        }

        teachplanMediaMapper.deleteByTeachplanId(teachplanId);

        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(associateMediaDTO.getFileName());
        teachplanMedia.setMediaId(associateMediaDTO.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void unassociateMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId)
                .eq(TeachplanMedia::getMediaId, mediaId);
        teachplanMediaMapper.delete(queryWrapper);
    }
}
