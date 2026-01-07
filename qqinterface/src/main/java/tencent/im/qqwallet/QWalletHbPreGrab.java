package tencent.im.qqwallet;

import com.tencent.mobileqq.pb.ByteStringMicro;
import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBBytesField;
import com.tencent.mobileqq.pb.PBEnumField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBStringField;

public class QWalletHbPreGrab {
    public static class QQHBRequest extends MessageMicro<QQHBRequest> {
        public final PBStringField cgiName = PBField.initString("");
        public final PBStringField reqText = PBField.initString("");
        public final PBStringField random = PBField.initString("");
        public final PBBytesField reqBody = PBField.initBytes(ByteStringMicro.EMPTY);
        public final PBEnumField enType = PBField.initEnum(0);
    }
    public static class QQHBReply extends MessageMicro<QQHBReply>{
        public final PBStringField retCode = PBField.initString("");
        public final PBStringField retMsg = PBField.initString("");
        public final PBStringField rspText = PBField.initString("");
        public final PBBytesField rspBody = PBField.initBytes(ByteStringMicro.EMPTY);
    }
}
