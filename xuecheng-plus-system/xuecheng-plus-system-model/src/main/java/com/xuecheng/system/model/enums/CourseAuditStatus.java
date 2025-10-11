package com.xuecheng.system.model.enums;

public enum CourseAuditStatus implements BaseEnum {
    REJECTED("202001", "审核未通过"),
    NOT_SUBMITTED("202002", "未提交"),
    SUBMITTED("202003", "已提交"),
    APPROVED("202004", "审核通过");

    private final String code;
    private final String desc;

    CourseAuditStatus(String code, String desc) {
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
