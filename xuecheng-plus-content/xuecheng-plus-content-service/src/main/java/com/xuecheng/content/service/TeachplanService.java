package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.AssociateMediaDTO;
import com.xuecheng.content.model.dto.EditTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService extends IService<Teachplan> {
    List<TeachplanTreeNodeDTO> getTeachplanTreeNodesByCourseId(Long courseId);

    void addOrUpdateTeachplan(EditTeachplanDTO editTeachplanDTO);

    void deleteTeachplanById(Long id);

    void moveDown(Long id);

    void moveUp(Long id);

    TeachplanMedia associateMedia(AssociateMediaDTO associateMediaDTO);

    void unassociateMedia(Long teachPlanId, String mediaId);
}
