package com.tencent.qqnt.kernel.nativeinterface;

public final class MemoryStateMsgInfo {
    public int memoryStateMsgType;

    public MemoryStateMsgInfo() {
    }

    public int getMemoryStateMsgType() {
        return this.memoryStateMsgType;
    }

    public MemoryStateMsgInfo(int i) {
        this.memoryStateMsgType = i;
    }
}
