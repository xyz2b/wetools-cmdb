package com.webank.wetoolscmdb.constant.enums;

public enum ImsAlertLevel {
    CRITICAL(1), MAJOR(2), MINOR(3), WARNING(4), INFO(5);

    private final int value;

    ImsAlertLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsAlertLevel intToImsAlertLevel(int value) {    //将数值转换成枚举值
        switch (value) {
            case 1:
                return CRITICAL;
            case 2:
                return MAJOR;
            case 3:
                return MINOR;
            case 4:
                return WARNING;
            case 5:
                return INFO;
            default:
                return null;
        }
    }
}
