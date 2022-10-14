package com.webank.wetoolscmdb.constant.enums;

public enum ImsAlertState {
    ALARMING(0), BLOCKED(1),
    RESTORED_BEFORE_WITHOUT_BLOCKED(2),
    RESTORED_BEFORE_WITH_BLOCKED(3),
    RESTORED(4);

    private final int value;

    ImsAlertState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsAlertState intToImsAlertState(int value) {    //将数值转换成枚举值
        switch (value) {
            case 0:
                return ALARMING;
            case 1:
                return BLOCKED;
            case 2:
                return RESTORED_BEFORE_WITHOUT_BLOCKED;
            case 3:
                return RESTORED_BEFORE_WITH_BLOCKED;
            case 4:
                return RESTORED;
            default:
                return null;
        }
    }
}
