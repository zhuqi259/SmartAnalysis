package cn.edu.jlu.mina.model;

import cn.edu.jlu.mina.util.ByteUtils;

public class MyMessage {
    private byte begin;
    private byte[] length;
    private byte[] data;
    private byte cs;
    private byte end;

    public MyMessage() {
        this(ByteUtils.getMyByteLength(0), ByteUtils.ZERO_DATA);
    }

    public MyMessage(byte[] length, byte[] data) {
        this.begin = ByteUtils.BEGIN;
        this.length = length;
        this.data = data;
        this.cs = ByteUtils.CS(data);
        this.end = ByteUtils.END;
    }

    public byte[] getMessage() {
        byte[] total = new byte[4 + length.length * 2 + data.length];
        int index = 0;
        total[index] = begin;
        index += 1;
        System.arraycopy(length, 0, total, index, length.length);
        index += length.length;
        System.arraycopy(length, 0, total, index, length.length);
        index += length.length;
        total[index] = begin;
        index += 1;
        System.arraycopy(data, 0, total, index, data.length);
        index += data.length;
        total[index] = cs;
        index += 1;
        total[index] = end;
        return total;
    }

    public byte getBegin() {
        return begin;
    }

    public void setBegin(byte begin) {
        this.begin = begin;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getCs() {
        return cs;
    }

    public void setCs(byte cs) {
        this.cs = cs;
    }

    public byte getEnd() {
        return end;
    }

    public void setEnd(byte end) {
        this.end = end;
    }
}