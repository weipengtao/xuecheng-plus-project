package com.xuecheng.gateway.service;

import java.util.Map;

public interface LoginService {
    Map<String, Object> applyToken(String code);
}
