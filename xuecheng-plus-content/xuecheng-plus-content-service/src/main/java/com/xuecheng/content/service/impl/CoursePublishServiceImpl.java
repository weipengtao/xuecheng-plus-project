package com.xuecheng.content.service.impl;

import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoursePublishServiceImpl implements CoursePublishService {

    private final CourseBaseService courseBaseService;
    private final TeachplanService teachplanService;

    @Override
    public CoursePreviewDTO getCoursePreview(Long courseId) {
        CourseBaseInfoDTO courseBaseInfoDTO = courseBaseService.getCourseBaseInfoById(courseId);
        List<TeachplanTreeNodeDTO> tree = teachplanService.getTeachplanTreeNodesByCourseId(courseId);
        return CoursePreviewDTO.builder()
                .courseBase(courseBaseInfoDTO)
                .teachplans(tree)
                .build();
    }
}
