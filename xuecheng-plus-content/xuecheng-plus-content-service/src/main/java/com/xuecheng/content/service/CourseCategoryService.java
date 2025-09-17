package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;

import java.util.List;

public interface CourseCategoryService {
    List<CourseCategoryTreeDTO> getTreeNodes(String number);
}
