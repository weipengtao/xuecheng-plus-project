package com.xuecheng.system.model.enums;

public enum CoursePublishStatus implements BaseEnum {
    UNPUBLISHED("203001", "未发布"),
    PUBLISHED("203002", "已发布"),
    OFFLINE("203003", "下线");

    private final String code;
    private final String desc;

    CoursePublishStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
