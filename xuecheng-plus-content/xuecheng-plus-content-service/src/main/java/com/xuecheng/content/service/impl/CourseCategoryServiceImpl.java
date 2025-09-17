package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {

    private final CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDTO> getTreeNodes(String rootId) {
        List<CourseCategoryTreeDTO> allNodes = courseCategoryMapper.selectTreeNodes(rootId);

        // 排除掉根节点
        allNodes = allNodes.stream()
                .filter(node -> !rootId.equals(node.getId()))
                .toList();

        // List 转 Map，key是id，value是节点对象
        Map<String, CourseCategoryTreeDTO> nodeMap = allNodes.stream()
                .collect(Collectors.toMap(CourseCategoryTreeDTO::getId, node -> node));

        List<CourseCategoryTreeDTO> tree = new ArrayList<>();

        for (CourseCategoryTreeDTO node : allNodes) {
            if (rootId.equals(node.getParentid())) {
                tree.add(node);
                continue;
            }
            CourseCategoryTreeDTO parentNode = nodeMap.get(node.getParentid());
            if (parentNode == null) {
                throw new RuntimeException("数据异常，找不到父节点，父节点id=" + node.getParentid());
            }
            if (parentNode.getChildrenTreeNodes() == null) {
                parentNode.setChildrenTreeNodes(new ArrayList<>());
            }
            parentNode.getChildrenTreeNodes().add(node);
        }

        return tree;
    }
}
