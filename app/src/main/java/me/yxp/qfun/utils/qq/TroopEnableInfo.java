package me.yxp.qfun.utils.qq;

import java.util.Map;

import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.data.TernaryDataList;

public class TroopEnableInfo {

    private final String fileName;

    public TernaryDataList<String> dataList = new TernaryDataList<>();

    public TroopEnableInfo(String fileName) {

        this.fileName = fileName;

    }

    public void initInfo() {
        dataList.list.clear();
        Object obj = DataUtils.deserialize("data", fileName);
        if (obj != null) {
            dataList = (TernaryDataList<String>) obj;
        }
    }

    public void updateInfo() {

        TernaryDataList<String> newDataList = new TernaryDataList<>();
        try {
            for (Map<String, Object> map : TroopTool.getGroupList()) {
                String troopUin = (String) map.get("group");
                String troopName = (String) map.get("groupName");
                newDataList.add(troopUin, troopName, dataList.getIsAvailable(troopUin));
            }
        } catch (Exception e) {
        }

        dataList = newDataList;

    }

    public void savaInfo() {
        DataUtils.serialize("data", fileName, dataList);
    }

    public String[] getValueArray() {

        int size = dataList.list.size();
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            String key = dataList.getKeyArray()[i];
            result[i] = dataList.getData(key).value;
        }

        return result;

    }

    public boolean[] getBoolArray() {

        int size = dataList.list.size();
        boolean[] result = new boolean[size];

        for (int i = 0; i < size; i++) {
            String key = dataList.getKeyArray()[i];
            result[i] = dataList.getData(key).isAvailable;
        }

        return result;

    }

}

