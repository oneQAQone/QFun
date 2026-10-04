#!/system/bin/sh

TARGET_PKGS="com.tencent.mobileqq com.tencent.tim"

for pkg in $TARGET_PKGS; do
    rm -rf /data/data/$pkg/qfun
    rm -rf /data/user/*/$pkg/qfun
done

rm -f /data/local/tmp/*qfun*