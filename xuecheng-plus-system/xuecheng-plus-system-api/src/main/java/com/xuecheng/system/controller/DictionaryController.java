package com.xuecheng.system.controller;

import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.sevice.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system/dictionary")
@Tag(name = "数据字典接口", description = "数据字典相关操作")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping("/all")
    @Operation(summary = "查询所有数据字典", description = "获取系统中所有的数据字典")
    public List<Dictionary> getAll() {
        return dictionaryService.getAll();
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据字典代码查询数据字典", description = "通过字典代码获取对应的数据字典")
    public Dictionary getByCode(@PathVariable @Parameter(description = "数据字典代码", example = "001") String code) {
        return dictionaryService.getByCode(code);
    }
}
