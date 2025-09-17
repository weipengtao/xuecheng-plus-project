package com.xuecheng.system.sevice;

import com.xuecheng.system.model.po.Dictionary;

import java.util.List;

public interface DictionaryService {
    List<Dictionary> getAll();

    Dictionary getByCode(String code);
}
