package com.xuecheng.system.sevice.impl;

import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.sevice.DictionaryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DictionaryServiceTest {

    @Autowired
    private DictionaryService dictionaryService;

    @Test
    void testGetAll() {
        List<Dictionary> all = dictionaryService.getAll();
        all.forEach(System.out::println);
    }

    @Test
    void testGetByCode() {
        Dictionary dictionary = dictionaryService.getByCode("001");
        Assertions.assertNotNull(dictionary);
    }
}