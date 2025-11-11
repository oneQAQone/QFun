package me.yxp.qfun.utils.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yxp.qfun.R;
import me.yxp.qfun.utils.qq.TroopEnableInfo;

public class TroopEnableDialog {
    private final TroopEnableInfo mTroopEnableInfo;
    private final Context mContext;
    private AlertDialog mAlertDialog;
    private ArrayAdapter<String> mAdapter;

    private TextView statusTextView;
    private final List<String> mTroopList = new ArrayList<>();

    public TroopEnableDialog(Context context, TroopEnableInfo troopEnableInfo) {

        mTroopEnableInfo = troopEnableInfo;
        mContext = context;

        initView();

    }

    private void initView() {

        mAlertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        mAlertDialog.setTitle("选择群聊");
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_troop_enable, null);
        mAlertDialog.setView(view);

        ListView listView = view.findViewById(R.id.lv_troops);
        mTroopList.addAll(Arrays.asList(mTroopEnableInfo.dataList.getKeyArray()));
        mAdapter = new TroopAdapter(mTroopList);
        listView.setAdapter(mAdapter);

        EditText editText = view.findViewById(R.id.et_search);
        editText.addTextChangedListener(new SearchWatch());

        statusTextView = view.findViewById(R.id.tv_selection_status);
        setStatus();

    }

    private void search(String text) {
        mTroopList.clear();
        List<String> keyList = Arrays.asList(mTroopEnableInfo.dataList.getKeyArray());
        List<String> valueList = Arrays.asList(mTroopEnableInfo.getValueArray());
        if (text.isEmpty()) {
            mTroopList.addAll(keyList);
        } else {
            for (int i = 0; i < keyList.size(); i++) {
                if ((valueList.get(i) + "（" + keyList.get(i) + "）").contains(text)) {
                    mTroopList.add(keyList.get(i));
                }
            }
        }
        mAdapter.notifyDataSetChanged();

    }
    private void setStatus() {
        int i = 0;
        String[] keyArray = mTroopEnableInfo.dataList.getKeyArray();
        for (String key : keyArray) {
            if (mTroopEnableInfo.dataList.getIsAvailable(key)) {
                i++;
            }
        }
        if (i == 0) {
            statusTextView.setText("未选择任何群组");
            statusTextView.setTextColor(Color.GRAY);
        } else if (i == keyArray.length) {
            statusTextView.setText("已选择所有群组");
            statusTextView.setTextColor(Color.BLUE);
        } else {
            statusTextView.setText("已选择" + i + "/" + keyArray.length + "个群组");
            statusTextView.setTextColor(Color.BLUE);
        }
    }
    public void show() {
        if (mAlertDialog != null) {
            mAlertDialog.show();
        }
    }

    private class TroopAdapter extends ArrayAdapter<String> {

        private TroopAdapter(List<String> troopList) {
            super(mContext, 0, troopList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            TextView textView = new TextView(mContext);
            String uin = getItem(position);
            boolean enable = mTroopEnableInfo.dataList.getIsAvailable(uin);
            textView.setTextColor(enable ? Color.GREEN : Color.BLACK);
            textView.setText(mTroopEnableInfo.dataList.getValue(uin) + "（" + uin +"）");
            textView.setHeight(200);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(v -> {
                boolean b = mTroopEnableInfo.dataList.getIsAvailable(uin);
                mTroopEnableInfo.dataList.setIsAvailable(uin, !b);
                textView.setTextColor(!b ? Color.GREEN : Color.BLACK);
                setStatus();
            });

            return textView;
        }
    }

    private class SearchWatch implements TextWatcher {

        @Override
        public void afterTextChanged(Editable editable) {}

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            search(charSequence.toString());
        }
    }


}