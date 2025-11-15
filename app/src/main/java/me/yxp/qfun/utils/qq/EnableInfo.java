package me.yxp.qfun.utils.qq;

import java.util.Map;

import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.data.TernaryDataList;

public abstract class EnableInfo {

    private final String fileName;

    public TernaryDataList<String> dataList = new TernaryDataList<>();

    protected EnableInfo(String fileName) {

        this.fileName = fileName;

    }

    public final void initInfo() {
        dataList.list.clear();
        Object obj = DataUtils.deserialize("data", fileName);
        if (obj != null) {
            dataList = (TernaryDataList<String>) obj;
        }
    }

    public abstract void updateInfo();

    public final void savaInfo() {
        DataUtils.serialize("data", fileName, dataList);
    }

    public final String[] getValueArray() {

        int size = dataList.list.size();
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            String key = dataList.getKeyArray()[i];
            result[i] = dataList.getData(key).value;
        }

        return result;

    }

    public static final class TroopEnableInfo extends EnableInfo {

        public TroopEnableInfo (String fileName) {
            super(fileName);
        }

        @Override
        public void updateInfo() {
            TernaryDataList<String> newDataList = new TernaryDataList<>();
            try {
                for (Map<String, Object> map : TroopTool.getGroupList()) {
                    String troopUin = (String) map.get("group");
                    String troopName = (String) map.get("groupName");
                    newDataList.add(troopUin, troopName, dataList.getIsAvailable(troopUin));
                }
            } catch (Exception ignored) {
            }

            super.dataList = newDataList;
        }
    }

    public static final class FriendEnableInfo extends EnableInfo {

        public FriendEnableInfo(String fileName) {
            super(fileName);
        }

        @Override
        public void updateInfo() {
            TernaryDataList<String> newDataList = new TernaryDataList<>();
            try {
                for (Map<String, Object> map : FriendTool.getAllFriend()) {
                    String uin = (String) map.get("uin");
                    String name = (String) map.get("name");
                    newDataList.add(uin, name, dataList.getIsAvailable(uin));
                }
            } catch (Exception ignored) {
            }

            super.dataList = newDataList;
        }
    }

}

