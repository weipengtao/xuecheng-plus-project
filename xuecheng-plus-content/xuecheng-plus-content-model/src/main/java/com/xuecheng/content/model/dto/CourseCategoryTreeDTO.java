package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCategoryTreeDTO extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDTO> childrenTreeNodes;
}
