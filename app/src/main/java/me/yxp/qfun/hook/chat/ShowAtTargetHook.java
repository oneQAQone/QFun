package me.yxp.qfun.hook.chat;

import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.yxp.qfun.hook.api.OnAIOViewUpdate;
import me.yxp.qfun.hook.base.BaseSwitchHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.qq.QQCurrentEnv;
import me.yxp.qfun.utils.reflect.ClassUtils;

@HookItemAnnotation(TAG = "显示艾特对象", desc = "点击消息中的艾特部分可直接跳转其主页（此功能可能导致滑动掉帧）")
public class ShowAtTargetHook extends BaseSwitchHookItem {
    private static Class<?> SelectableLinearLayout;

    private OnAIOViewUpdate.AIOViewUpdateListener onGetView;

    @Override
    protected boolean initMethod() throws Throwable {
        SelectableLinearLayout = ClassUtils._SelectableLinearLayout();
        return true;
    }

    @Override
    protected void initCallback() {

        onGetView = (frameLayout, msgRecord) -> {

            MsgData msgData = new MsgData(msgRecord);

            if (msgData.type != 2 || (msgData.msgType != 2 && msgData.msgType != 9) || msgData.atMap.isEmpty()) return;

            ViewGroup selectableLinearLayout = findSelectableLinearLayout(frameLayout);
            if (selectableLinearLayout == null) return;

            List<TextView> textViewList = getTextViewList(selectableLinearLayout);

            setClickableSpan(msgData.atMap, textViewList);

        };


    }

    @Override
    public void startHook() {
        OnAIOViewUpdate.INSTANCE.addListener(onGetView);
    }

    @Override
    public void stopHook() {
        OnAIOViewUpdate.INSTANCE.removeListener(onGetView);
    }

    private ViewGroup findSelectableLinearLayout(FrameLayout frameLayout) {

        LinearLayout linearLayout = (LinearLayout) frameLayout.getChildAt(0);
        ViewGroup originalView = (ViewGroup) linearLayout.getChildAt(0);
        View childFirst = originalView.getChildAt(0);
        if (SelectableLinearLayout.isInstance(childFirst)) {
            return (ViewGroup) childFirst;
        } else {
            return null;
        }
    }

    private List<TextView> getTextViewList(ViewGroup selectableLinearLayout) {
        List<TextView> textList = new ArrayList<>();
        if (selectableLinearLayout.getChildCount() == 1) {
            View childFirst = selectableLinearLayout.getChildAt(0);
            ViewGroup textExtLinerLayout = (ViewGroup) childFirst;
            if (textExtLinerLayout.getChildAt(0) instanceof TextView) {
                TextView textView = (TextView) textExtLinerLayout.getChildAt(0);
                textList.add(textView);
            }
        } else {
            for (int i = 0; i < selectableLinearLayout.getChildCount(); i++) {
                View childAt = selectableLinearLayout.getChildAt(i);
                if (childAt instanceof TextView) {
                    TextView textView = (TextView) childAt;
                    textList.add(textView);
                }
            }

        }
        return textList;
    }

    private int findFirstName(CharSequence text, String name) {
        String s = text.toString();
        return s.indexOf(name);
    }

    private void setClickableSpan(Map<String, String> atMap, List<TextView> textViewList) {
        for (String uin : atMap.keySet()) {
            String name = atMap.get(uin);
            for (TextView textView : textViewList) {
                SpannableString text = (SpannableString) textView.getText();
                int startIndex = findFirstName(text, name);
                if (startIndex != -1) {

                    int endIndex = startIndex + name.length();
                    text.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            openUserProfileCard(uin);
                        }
                    }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    break;
                }
            }
        }

    }

    private void openUserProfileCard(String uin) {
        String url = "mqqapi://userprofile/friend_profile_card?src_type=web&version=1.0&source=2&uin=" + uin;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        QQCurrentEnv.getActivity().startActivity(intent);
    }


}
