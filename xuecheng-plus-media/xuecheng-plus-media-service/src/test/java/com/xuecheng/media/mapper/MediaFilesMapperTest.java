package com.xuecheng.media.mapper;

import com.xuecheng.media.model.po.MediaFiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MediaFilesMapperTest {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Test
    void testSelect() {
        List<MediaFiles> mediaFiles = mediaFilesMapper.selectList(null);
        System.out.println(mediaFiles);
    }
}