package com.webank.wetoolscmdb.constant.enums;

public enum ImsAlertHandleState {
    NOT_PROCESSED(0), PROCESSED(1);

    private final int value;

    ImsAlertHandleState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsAlertHandleState intToImsAlertHandleState(int value) {    //将数值转换成枚举值
        switch (value) {
            case 0:
                return NOT_PROCESSED;
            case 1:
                return PROCESSED;
            default:
                return null;
        }
    }
}
