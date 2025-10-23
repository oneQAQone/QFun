package me.yxp.qfun.utils.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TernaryDataList<T> implements Serializable {
    public List<TernaryData> list = new ArrayList<>();

    public void add(String key, T value, boolean isAvailable) {
        if (!contain(key)) {
            list.add(new TernaryData(key, value, isAvailable));
        }
    }

    public boolean contain(String key) {
        for (TernaryData data : list) {
            if (data.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public TernaryData getData(String key) {
        for (TernaryData data : list) {
            if (data.key.equals(key)) {
                return data;
            }
        }
        return null;
    }

    public T getValue(String key) {
        if (contain(key)) {
            return getData(key).value;
        }
        return null;
    }

    public void setValue(String key, T value) {
        if (contain(key)) {
            getData(key).value = value;
        }
    }

    public boolean getIsAvailable(String key) {
        if (contain(key)) {
            return getData(key).isAvailable;
        }
        return false;
    }

    public void setIsAvailable(String key, boolean isAvailable) {
        if (contain(key)) {
            getData(key).isAvailable = isAvailable;
        }
    }

    public String[] getKeyArray() {
        int size = list.size();
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            result[i] = list.get(i).key;
        }
        return result;
    }

    public class TernaryData implements Serializable {
        public String key;
        public T value;
        public boolean isAvailable;

        private TernaryData(String key, T value, boolean isAvailable) {
            this.key = key;
            this.value = value;
            this.isAvailable = isAvailable;
        }

        @NonNull
        @Override
        public String toString() {
            return "[key=" + key + ", value=" + value + ", isAvailable=" + isAvailable + "]";
        }
    }
}
