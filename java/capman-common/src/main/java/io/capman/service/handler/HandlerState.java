package io.capman.service.handler;

import io.netty.util.AttributeKey;

/**
 * Created by flying on 7/7/16.
 * For Monitoring and Statistics
 */
public class HandlerState {

    public static final AttributeKey<HandlerState> KEY = AttributeKey.newInstance("Capman-HandlerState");

    private long incomeTime;
    private long bizLogicTime;
    private long writeStartTime;
    private long writeEndTime;

    public long getIncomeTime() {
        return incomeTime;
    }

    public static AttributeKey<HandlerState> getKEY() {
        return KEY;
    }

    public long getBizLogicTime() {
        return bizLogicTime;
    }

    public void setBizLogicTime(long bizLogicTime) {
        this.bizLogicTime = bizLogicTime;
    }

    public long getWriteStartTime() {
        return writeStartTime;
    }

    public void setWriteStartTime(long writeStartTime) {
        this.writeStartTime = writeStartTime;
    }

    public long getWriteEndTime() {
        return writeEndTime;
    }

    public void setWriteEndTime(long writeEndTime) {
        this.writeEndTime = writeEndTime;
    }

    public void setIncomeTime(long incomeTime) {
        this.incomeTime = incomeTime;
    }
}
