package com.xuecheng.system.model.enums;

public enum TaskStatus implements BaseEnum {
    INIT("0", "初始"),
    SUCCESS("1", "成功");

    private final String code;
    private final String desc;

    TaskStatus(String code, String desc) {
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
