package oicq.wlogin_sdk.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Ticket implements Parcelable {
    public static final Creator<Ticket> CREATOR = new Creator<>() {
        public Ticket createFromParcel(Parcel parcel) {
            return new Ticket(parcel);
        }

        public Ticket[] newArray(int i) {
            return new Ticket[i];
        }
    };
    private static final int EXPIRE_FIELD = 65535;
    private static final int MAX_PSKEY_SIZE = 200;
    public long _create_time;
    public long _expire_time;
    public Map<String, Long> _pskey_expire;
    public Map<String, byte[]> _pskey_map;
    public Map<String, Long> _pt4token_expire;
    public Map<String, byte[]> _pt4token_map;
    public byte[] _sig;
    public byte[] _sig_key;
    public int _type;

    public Ticket() {
        this._pskey_map = new HashMap();
        this._pskey_expire = new HashMap();
        this._pt4token_map = new HashMap();
        this._pt4token_expire = new HashMap();
    }

    public Ticket(int i, byte[] bArr, byte[] bArr2, long j, long j2) {
        this._pskey_map = new HashMap();
        this._pskey_expire = new HashMap();
        this._pt4token_map = new HashMap();
        this._pt4token_expire = new HashMap();
        this._type = i;
        this._sig = bArr == null ? new byte[0] : bArr.clone();
        this._sig_key = bArr2 == null ? new byte[0] : bArr2.clone();
        this._create_time = j;
        this._expire_time = j2;
    }

    public Ticket(int i, byte[] bArr, byte[] bArr2, long j, byte[] bArr3) {
        this._pskey_map = new HashMap();
        this._pskey_expire = new HashMap();
        this._pt4token_map = new HashMap();
        this._pt4token_expire = new HashMap();
        this._type = i;
        this._sig = bArr == null ? new byte[0] : bArr.clone();
        this._sig_key = bArr2 == null ? new byte[0] : bArr2.clone();
        this._create_time = j;
        this._expire_time = 86400 + j;
        parsePsBuf(bArr3, j, this._pskey_map, this._pskey_expire, true);
    }

    public Ticket(int i, byte[] bArr, byte[] bArr2, long j, byte[] bArr3, byte[] bArr4) {
        this._pskey_map = new HashMap();
        this._pskey_expire = new HashMap();
        this._pt4token_map = new HashMap();
        this._pt4token_expire = new HashMap();
        this._type = i;
        this._sig = bArr == null ? new byte[0] : bArr.clone();
        this._sig_key = bArr2 == null ? new byte[0] : bArr2.clone();
        this._create_time = j;
        this._expire_time = 86400 + j;
        parsePsBuf(bArr3, j, this._pskey_map, this._pskey_expire, true);
        parsePsBuf(bArr4, this._create_time, this._pt4token_map, this._pt4token_expire, false);
    }

    private Ticket(Parcel parcel) {
        this._pskey_map = new HashMap();
        this._pskey_expire = new HashMap();
        this._pt4token_map = new HashMap();
        this._pt4token_expire = new HashMap();
        readFromParcel(parcel);
    }

    public static void parsePsBuf(byte[] bArr, long j, Map<String, byte[]> map, Map<String, Long> map2, boolean z) {
        throw new RuntimeException("Stub!");
    }

    public void readFromParcel(Parcel parcel) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._type);
        parcel.writeByteArray(this._sig);
        parcel.writeByteArray(this._sig_key);
        parcel.writeLong(this._create_time);
        parcel.writeLong(this._expire_time);
        parcel.writeMap(this._pskey_map);
        parcel.writeMap(this._pt4token_map);
    }
}
