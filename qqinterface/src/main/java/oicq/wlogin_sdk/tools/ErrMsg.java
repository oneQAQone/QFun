package oicq.wlogin_sdk.tools;

import android.os.Parcel;
import android.os.Parcelable;

public class ErrMsg implements Cloneable, Parcelable {
    public static final Creator<ErrMsg> CREATOR = new Creator<>() {
        public ErrMsg createFromParcel(Parcel parcel) {
            return new ErrMsg(parcel);
        }

        public ErrMsg[] newArray(int i) {
            return new ErrMsg[i];
        }
    };
    private String message;
    private String otherinfo;
    private String title;
    private int type;
    private int version;

    public ErrMsg() {
        this.version = 0;
        this.type = 0;
        this.title = InternationMsg.a(InternationMsg.MSG_TYPE.MSG_0);
        this.message = InternationMsg.a(InternationMsg.MSG_TYPE.MSG_1);
        this.otherinfo = "";
    }

    public ErrMsg(int i, int i2, String str, String str2, String str3) {
        this.version = i;
        this.type = i2;
        this.title = str;
        this.message = str2;
        this.otherinfo = str3;
    }

    private ErrMsg(Parcel parcel) {
        readFromParcel(parcel);
    }

    public String getMessage() {
        return this.message;
    }

    public String getOtherinfo() {
        return this.otherinfo;
    }

    public String getTitle() {
        return this.title;
    }

    public int getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public void readFromParcel(Parcel parcel) {
        throw new RuntimeException("Stub!");
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public void setOtherinfo(String str) {
        this.otherinfo = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setVersion(int i) {
        this.version = i;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.version);
        parcel.writeInt(this.type);
        parcel.writeString(this.title);
        parcel.writeString(this.message);
        parcel.writeString(this.otherinfo);
    }
}
