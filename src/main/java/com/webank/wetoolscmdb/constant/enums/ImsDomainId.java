package com.webank.wetoolscmdb.constant.enums;

public enum ImsDomainId {
    OTPD(125615189);

    private final int value;

    ImsDomainId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsDomainId intToImsDomainId(int value) {    //将数值转换成枚举值
        switch (value) {
            case 125615189:
                return OTPD;
            default:
                return null;
        }
    }
}
