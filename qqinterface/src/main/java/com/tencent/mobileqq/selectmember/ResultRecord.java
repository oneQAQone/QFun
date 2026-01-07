package com.tencent.mobileqq.selectmember;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.tencent.mobileqq.data.troop.TroopMemberNickInfo;

public class ResultRecord implements Parcelable {
    public String gameLevelIcon;
    public String groupUin;
    public String guildAvatarUrl;
    public String guildId;
    public boolean isNewTroop;
    public long lastChooseTime;
    public String matchFriendAvatarUrl;
    public TroopMemberNickInfo memberNickInfo;
    public String name;
    public boolean needCheckBoxCheckAnimation;
    public String phone;
    public int source;
    public int type;
    public String uid;
    public String uin;
    public int uinType;

    public ResultRecord() {
        this.uinType = -1;
        this.needCheckBoxCheckAnimation = false;
        this.source = 2;
    }

    public static final Creator<ResultRecord> CREATOR = new Creator<>() {
        @Override
        public ResultRecord createFromParcel(Parcel in) {
            return new ResultRecord(in);
        }

        @Override
        public ResultRecord[] newArray(int size) {
            return new ResultRecord[size];
        }
    };

    public static ResultRecord copyResultRecord(ResultRecord resultRecord) {
        if (resultRecord != null) {
            ResultRecord resultRecord2 = new ResultRecord();
            resultRecord2.uin = resultRecord.uin;
            resultRecord2.uid = resultRecord.uid;
            resultRecord2.name = resultRecord.name;
            resultRecord2.type = resultRecord.type;
            resultRecord2.groupUin = resultRecord.groupUin;
            resultRecord2.phone = resultRecord.phone;
            resultRecord2.uinType = resultRecord.uinType;
            resultRecord2.isNewTroop = resultRecord.isNewTroop;
            resultRecord2.gameLevelIcon = resultRecord.gameLevelIcon;
            resultRecord2.guildId = resultRecord.guildId;
            resultRecord2.guildAvatarUrl = resultRecord.guildAvatarUrl;
            resultRecord2.matchFriendAvatarUrl = resultRecord.matchFriendAvatarUrl;
            resultRecord2.source = resultRecord.source;
            return resultRecord2;
        }
        return null;
    }

    public static ResultRecord readFromParcel(Parcel parcel) {
        return CREATOR.createFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ResultRecord resultRecord) {
            if (resultRecord.uin.equals(this.uin) && resultRecord.type == this.type) {
                if (TextUtils.isEmpty(this.phone) || TextUtils.isEmpty(resultRecord.phone) || !this.phone.equals(resultRecord.phone)) {
                    return TextUtils.isEmpty(this.phone) && TextUtils.isEmpty(resultRecord.phone);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int getUinType() {
        int i = this.uinType;
        if (i == -1 && this.type == 4) {
            return 1006;
        }
        return i;
    }

    public void init(String str, String str2, String str3, int i, String str4) {
        this.uin = str;
        this.name = str4;
        this.uinType = i;
        this.guildId = str2;
        this.guildAvatarUrl = str3;
        this.groupUin = "";
        this.phone = "";
        this.lastChooseTime = 0L;
        this.isNewTroop = false;
        this.gameLevelIcon = "";
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.uin);
        parcel.writeString(this.uid);
        parcel.writeString(this.name);
        parcel.writeInt(this.type);
        parcel.writeString(this.groupUin);
        parcel.writeParcelable((Parcelable) this.memberNickInfo, i);
        parcel.writeString(this.phone);
        parcel.writeLong(this.lastChooseTime);
        parcel.writeInt(this.uinType);
        parcel.writeInt(this.isNewTroop ? 1 : 0);
        parcel.writeString(this.gameLevelIcon);
        parcel.writeString(this.guildId);
        parcel.writeString(this.guildAvatarUrl);
        parcel.writeString(this.matchFriendAvatarUrl);
        parcel.writeInt(this.source);
    }

    public ResultRecord(String str, String str2, int i, int i2, String str3, String str4, long j, boolean z, String str5) {
        this.needCheckBoxCheckAnimation = false;
        this.source = 2;
        this.uin = str;
        this.name = str2;
        this.type = i;
        this.uinType = i2;
        this.groupUin = str3;
        this.phone = str4;
        this.lastChooseTime = j;
        this.isNewTroop = z;
        this.gameLevelIcon = str5;
    }

    public void init(String str, String str2, int i, String str3, String str4) {
        this.uin = str;
        this.name = str2;
        this.uinType = i;
        this.groupUin = str3;
        this.phone = str4;
        this.lastChooseTime = 0L;
        this.isNewTroop = false;
        this.gameLevelIcon = "";
    }

    public ResultRecord(String str, String str2, int i, String str3, String str4) {
        this.uinType = -1;
        this.needCheckBoxCheckAnimation = false;
        this.source = 2;
        init(str, str2, i, str3, str4);
    }

    public ResultRecord(String str, String str2, int i, String str3) {
        this.needCheckBoxCheckAnimation = false;
        this.source = 2;
        this.uin = str;
        this.name = str2;
        this.uinType = i;
        this.guildId = str3;
        this.groupUin = "";
        this.phone = "";
        this.lastChooseTime = 0L;
        this.isNewTroop = false;
        this.gameLevelIcon = "";
        this.guildAvatarUrl = "";
    }

    ResultRecord(Parcel parcel) {
        this.uinType = -1;
        this.needCheckBoxCheckAnimation = false;
        this.source = 2;
        this.uin = parcel.readString();
        this.uid = parcel.readString();
        this.name = parcel.readString();
        this.type = parcel.readInt();
        this.groupUin = parcel.readString();
        this.memberNickInfo = parcel.readParcelable(TroopMemberNickInfo.class.getClassLoader());
        this.phone = parcel.readString();
        this.lastChooseTime = parcel.readLong();
        this.uinType = parcel.readInt();
        this.isNewTroop = parcel.readInt() == 1;
        this.gameLevelIcon = parcel.readString();
        this.guildId = parcel.readString();
        this.guildAvatarUrl = parcel.readString();
        this.matchFriendAvatarUrl = parcel.readString();
        this.source = parcel.readInt();
    }
}
