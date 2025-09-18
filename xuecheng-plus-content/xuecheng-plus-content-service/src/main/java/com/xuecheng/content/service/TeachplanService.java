package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;

import java.util.List;

public interface TeachplanService {
    List<TeachplanTreeNodeDTO> getTeachplanTreeNodesByCourseId(String courseId);
}
