package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EditCourseDTO extends AddCourseDTO{
    private Long id;
}
