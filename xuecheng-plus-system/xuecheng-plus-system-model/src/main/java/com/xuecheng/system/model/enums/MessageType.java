package com.xuecheng.system.model.enums;

public enum MessageType implements  BaseEnum {
    COURSE_PUBLISH("course_publish","课程发布");

    private final String code;
    private final String desc;

    MessageType(String code, String desc) {
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
