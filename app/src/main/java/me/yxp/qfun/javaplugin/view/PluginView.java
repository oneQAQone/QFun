package me.yxp.qfun.javaplugin.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Map;

import bsh.EvalError;
import me.yxp.qfun.R;
import me.yxp.qfun.javaplugin.loader.PluginInfo;
import me.yxp.qfun.javaplugin.loader.PluginManager;
import me.yxp.qfun.utils.error.PluginError;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.thread.SyncUtils;

public class PluginView {
    private PopupWindow mPopupWindow;
    private LinearLayout mFloatingButton;
    private int mCurrentX = 0;
    private int mCurrentY = 0;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private int mButtonWidth;
    private int mButtonHeight;
    private boolean mIsDragging = false;
    private int mChatType;
    private String mPeerUin;
    private String mPeerName;
    private Object mContact;

    public void initPopupWindow() {
        Activity activity = QQCurrentEnv.getActivity();
        if (activity == null) return;

        extractIntentData(activity);
        notifyPluginsChatInterface();
        initButtonDimensions(activity);
        createPopupWindow(activity);
        setupTouchListener(activity);
    }

    private void extractIntentData(Activity activity) {
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        mChatType = bundle.getInt("key_chat_type");
        mPeerUin = (mChatType == 2) ? bundle.getString("key_peerId") :
                String.valueOf(bundle.getLong("key_peerUin"));
        mPeerName = bundle.getString("key_chat_name");

        try {
            mContact = ClassUtils.makeDefaultObject(ClassUtils._Contact(), mChatType,
                    bundle.getString("key_peerId"), "");
        } catch (Exception e) {
            // 忽略创建Contact对象的异常
        }
    }

    private void notifyPluginsChatInterface() {
        for (PluginInfo pluginInfo : PluginManager.pluginInfos) {
            if (pluginInfo.isRunning) {
                pluginInfo.pluginCompiler.pluginCallback.chatInterface(mChatType, mPeerUin, mPeerName);
            }
        }
    }

    private void initButtonDimensions(Activity activity) {
        mButtonWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                activity.getResources().getDisplayMetrics());
        mButtonHeight = mButtonWidth;
    }

    private void createPopupWindow(Activity activity) {
        View popupView = LayoutInflater.from(activity).inflate(R.layout.pluginfloatview, null);
        mFloatingButton = popupView.findViewById(R.id.float_btn);

        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setWidth(mButtonWidth);
        mPopupWindow.setHeight(mButtonHeight);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mButtonWidth = popupView.getMeasuredWidth();
        mButtonHeight = popupView.getMeasuredHeight();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListener(Activity activity) {
        mFloatingButton.setOnTouchListener(new View.OnTouchListener() {
            private long mTouchStartTime;
            private float mTouchStartX, mTouchStartY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View parentView = activity.findViewById(android.R.id.content);
                int parentWidth = parentView.getWidth();
                int parentHeight = parentView.getHeight();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handleActionDown(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        handleActionMove(event, parentWidth, parentHeight);
                        break;
                    case MotionEvent.ACTION_UP:
                        handleActionUp();
                        break;
                }
                return true;
            }

            private void handleActionDown(MotionEvent event) {
                mTouchStartTime = System.currentTimeMillis();
                mTouchStartX = event.getRawX();
                mTouchStartY = event.getRawY();
                mInitialTouchX = event.getRawX();
                mInitialTouchY = event.getRawY();
            }

            private void handleActionMove(MotionEvent event, int parentWidth, int parentHeight) {
                float deltaX = Math.abs(event.getRawX() - mTouchStartX);
                float deltaY = Math.abs(event.getRawY() - mTouchStartY);

                // 点击阈值，单位像素
                int CLICK_THRESHOLD = 10;
                if (deltaX > CLICK_THRESHOLD || deltaY > CLICK_THRESHOLD) {
                    mIsDragging = true;
                }

                if (mIsDragging) {
                    float moveDeltaX = event.getRawX() - mInitialTouchX;
                    float moveDeltaY = event.getRawY() - mInitialTouchY;

                    float newX = mCurrentX + moveDeltaX;
                    float newY = mCurrentY + moveDeltaY;

                    newX = Math.max(0, Math.min(newX, parentWidth));
                    newY = Math.max(0, Math.min(newY, parentHeight));

                    mPopupWindow.update((int) newX, (int) newY, -1, -1);

                    mCurrentX = (int) newX;
                    mCurrentY = (int) newY;

                    mInitialTouchX = event.getRawX();
                    mInitialTouchY = event.getRawY();
                }
            }

            private void handleActionUp() {
                if (!mIsDragging && (System.currentTimeMillis() - mTouchStartTime) < 500) {
                    showMenu();
                }
                mIsDragging = false;
            }
        });
    }

    private void showMenu() {
        Activity activity = QQCurrentEnv.getActivity();
        if (activity == null) return;

        View root = activity.findViewById(android.R.id.content);
        LinearLayout menu = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.pluginpopupview, null);
        LinearLayout pluginItemHolder = menu.findViewById(R.id.pluginitemholder);

        for (PluginInfo pluginInfo : PluginManager.pluginInfos) {
            addPluginToMenu(activity, pluginInfo, pluginItemHolder);
        }

        PopupWindow popupWindow = new PopupWindow(menu, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setHeight(root.getHeight() / 2);
        popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0);
    }

    private void addPluginToMenu(Activity activity, PluginInfo pluginInfo, LinearLayout pluginItemHolder) {
        Map<String, String> itemMap = pluginInfo.pluginCompiler.itemMap;
        if (itemMap.isEmpty()) return;

        LinearLayout menuItem = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.pluginmenu, null);
        TextView pluginNameTextView = menuItem.findViewById(R.id.pluginNameTextView);
        pluginNameTextView.setText(pluginInfo.pluginName);

        pluginNameTextView.setOnClickListener(v -> new Thread(() -> {
            try {
                SyncUtils.runOnUiThread(() ->
                        pluginNameTextView.setText(pluginInfo.pluginName + "(加载中...)"));

                pluginInfo.pluginCompiler.startPlugin();
            } catch (EvalError e) {
                PluginError.evalError(e, pluginInfo);
            } finally {
                SyncUtils.postDelayed(() ->
                        pluginNameTextView.setText(pluginInfo.pluginName), 100);
            }
        }).start());

        for (Map.Entry<String, String> entry : itemMap.entrySet()) {
            addMenuItemToPlugin(activity, pluginInfo, menuItem, entry.getKey(), entry.getValue());
        }

        pluginItemHolder.addView(menuItem);
    }

    private void addMenuItemToPlugin(Activity activity, PluginInfo pluginInfo,
                                     LinearLayout menuItem, String name, String callback) {
        LinearLayout pluginMenuItem = (LinearLayout) LayoutInflater.from(activity)
                .inflate(R.layout.pluginmenuitem, null);
        TextView menuItemButton = pluginMenuItem.findViewById(R.id.pluginmenuitemButton);
        menuItemButton.setText(name);

        menuItemButton.setOnClickListener(v -> pluginInfo.pluginCompiler.pluginCallback.invokeItem(callback, mChatType,
                mPeerUin, mPeerName, mContact));

        menuItem.addView(pluginMenuItem);
    }

    public void showFloatingButton() {
        if (mPopupWindow == null || mPopupWindow.isShowing()) return;

        Activity activity = QQCurrentEnv.getActivity();
        if (activity == null) return;

        View parentView = activity.findViewById(android.R.id.content);
        parentView.post(() -> {
            if (mCurrentX == 0 && mCurrentY == 0) {
                mCurrentX = parentView.getWidth() / 2;
                mCurrentY = parentView.getHeight() / 2;
            }
            mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
        });
    }

    public void dismissFloatButton() {
        if (mPopupWindow == null) return;
        mPopupWindow.dismiss();
        mPopupWindow = null;
    }
}