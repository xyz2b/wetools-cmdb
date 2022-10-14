package com.webank.wetoolscmdb.constant.enums;

public enum ImsAlertOrigin {
    BUSINESS_REPORT(0), IMS_SELF(1);

    private final int value;

    ImsAlertOrigin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsAlertOrigin intToImsAlertOrigin(int value) {    //将数值转换成枚举值
        switch (value) {
            case 0:
                return BUSINESS_REPORT;
            case 1:
                return IMS_SELF;
            default:
                return null;
        }
    }
}
