package com.xuecheng.system.mapper;

import com.xuecheng.system.model.po.Dictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DictionaryMapperTest {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Test
    void testSelectById() {
        Dictionary dictionary = dictionaryMapper.selectById(12);
        Assertions.assertNotNull(dictionary);
    }
}