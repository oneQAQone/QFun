package com.tencent.widget;

public class PopupMenuDialog {
    public static class MenuItem {

        public int id;
        public MenuItem(int i, String str, String str2, int i2) {

        }
    }

    public interface OnClickActionListener {
        void onClickAction(MenuItem menuItem);
    }
    public interface OnDismissListener {}

}
