package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.EditTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;

import java.util.List;

public interface TeachplanService {
    List<TeachplanTreeNodeDTO> getTeachplanTreeNodesByCourseId(String courseId);

    void addOrUpdateTeachplan(EditTeachplanDTO editTeachplanDTO);

    void deleteTeachplanById(Long id);

    void moveDown(Long id);

    void moveUp(Long id);
}
