package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeachplanTreeNodeDTO extends Teachplan {

    private TeachplanMedia teachplanMedia;

    private List<TeachplanTreeNodeDTO> teachPlanTreeNodes;
}
