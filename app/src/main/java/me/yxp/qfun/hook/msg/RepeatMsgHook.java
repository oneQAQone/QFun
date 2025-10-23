package me.yxp.qfun.hook.msg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import me.yxp.qfun.R;
import me.yxp.qfun.activity.InjectSettings;
import me.yxp.qfun.hook.base.BaseWithDataHookItem;
import me.yxp.qfun.hook.base.HookItemAnnotation;
import me.yxp.qfun.javaplugin.api.MsgData;
import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.hook.HookUtils;
import me.yxp.qfun.utils.qq.MsgTool;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

@HookItemAnnotation(TAG = "消息复读", desc = "部分特殊消息无法复读，点击可自定义加一图标")
public final class RepeatMsgHook extends BaseWithDataHookItem {
    public static Bitmap sBitmap;
    private static Method sGetViewMethod;
    private static Method sGetMsgMethod;
    private ImageView mImageView;

    @Override
    protected boolean initMethod() throws Throwable {
        sGetViewMethod = MethodUtils.create(ClassUtils._AIOMsgFollowComponent())
                .withReturnType(ImageView.class)
                .withParamTypes()
                .findOne();

        sGetMsgMethod = MethodUtils.create(ClassUtils._AIOMsgFollowComponent())
                .withReturnType(void.class)
                .withParamTypes(int.class, ClassUtils._AIOMsgItem(), List.class)
                .findOne();

        return true;
    }

    @Override
    protected void initCallback() {
        HookUtils.hookIfEnable(this, sGetViewMethod, null, param -> {
            Object result = param.getResult();
            if (result instanceof ImageView) {
                mImageView = (ImageView) result;
                if (sBitmap != null) {
                    mImageView.setImageDrawable(new BitmapDrawable(sBitmap));
                } else {
                    mImageView.setImageResource(R.drawable.repeat);
                }
            }
        });

        HookUtils.hookIfEnable(this, sGetMsgMethod, null, param -> {
            if (mImageView.getContext().getClass().getName().contains("MultiForwardActivity")) {
                return;
            }
            mImageView.setOnClickListener(v -> sendMsg(param.args[1]));
            mImageView.setVisibility(View.VISIBLE);
        });
    }

    private void sendMsg(Object aioMsgItem) {
        try {
            Object msgRecord = FieldUtils.create(aioMsgItem)
                    .ofType(ClassUtils._MsgRecord())
                    .inParent(ClassUtils._AIOMsgItem())
                    .getValue();
            List<Object> msgElements = (List<Object>) FieldUtils.create(msgRecord)
                    .withName("elements").getValue();
            MsgData msgData = new MsgData(msgRecord);
            MsgTool.sendMsg(msgData.contact, msgElements);
        } catch (Exception e) {
            ErrorOutput.itemHookError(this, e);
        }
    }

    @Override
    public void initData() {
        File file = DataUtils.createFile("data", "repeat.png");
        sBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    public void savaData() {
        File file = DataUtils.createFile("data", "repeat.png");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            if (sBitmap == null) {
                return;
            }
            sBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            ErrorOutput.itemHookError(this, e);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((Activity) v.getContext()).startActivityForResult(intent, InjectSettings.PICK_IMAGE_REQUEST);
    }
}