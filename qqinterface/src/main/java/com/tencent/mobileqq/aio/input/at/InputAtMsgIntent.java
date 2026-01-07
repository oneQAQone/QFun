package com.tencent.mobileqq.aio.input.at;

import com.tencent.mvi.base.route.MsgIntent;

public abstract class InputAtMsgIntent implements MsgIntent {
    public final class InsertAtMemberSpan extends InputAtMsgIntent {
    }

    public boolean isSticky() {
        throw new RuntimeException("Stub!");
    }
}
