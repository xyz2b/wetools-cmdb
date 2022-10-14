package com.webank.wetoolscmdb.constant.enums;

public enum ImsCiType {
    SUBSYSTEM(2), DCN(3), HOST(4),
    NETWORK_EQUIPMENT(5), SPECIAL_LINE(6),
    APP_INSTANCE(7), SELF_MONITOR(8),
    DATABASE_INFORMATION_COLLECTION_TEMPLATE(10),
    DATA_AGGREGATION_TASK(11), KAFKA_INSTANCE(12);

    private final int value;

    ImsCiType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ImsCiType intToImsCiType(int value) {    //将数值转换成枚举值
        switch (value) {
            case 2:
                return SUBSYSTEM;
            case 3:
                return DCN;
            case 4:
                return HOST;
            case 5:
                return NETWORK_EQUIPMENT;
            case 6:
                return SPECIAL_LINE;
            case 7:
                return APP_INSTANCE;
            case 8:
                return SELF_MONITOR;
            case 10:
                return DATABASE_INFORMATION_COLLECTION_TEMPLATE;
            case 11:
                return DATA_AGGREGATION_TASK;
            case 12:
                return KAFKA_INSTANCE;
            default:
                return null;
        }
    }
}
