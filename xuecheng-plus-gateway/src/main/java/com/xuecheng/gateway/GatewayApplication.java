package com.xuecheng.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        // TODO: 未实现用服务名称访问其他服务
        SpringApplication.run(GatewayApplication.class, args);
    }
}
