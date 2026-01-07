package com.tencent.qqnt.kernel.nativeinterface;

public interface IKernelTicketService {
    long addKernelTicketListener(IKernelTicketListener iKernelTicketListener);

    void forceFetchClientKey(String str, IClientKeyCallback iClientKeyCallback);

    void removeKernelTicketListener(long j);
}
