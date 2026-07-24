package com.example.lrhdrunlock;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final float HEADROOM = 5.0f; // 对齐 LR 给窗口的激发倍率

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.adobe.lrmobile".equals(lpparam.packageName)) return;
        ClassLoader cl = lpparam.classLoader;

        // 第一道门：HDR 显示总开关（原有）
        XposedHelpers.findAndHookMethod("com.adobe.lrmobile.utils.l",
                cl, "d", XC_MethodReplacement.returnConstant(true));

        // 第二道门：照片 HDR 支路开关
        /*XposedHelpers.findAndHookMethod("com.adobe.lrmobile.utils.l",
                cl, "c", XC_MethodReplacement.returnConstant(true));*/

        // 保险：loupe 照片 BLAST 层 headroom=0 改写成 5
        try {
            XposedHelpers.findAndHookMethod("android.view.SurfaceControl$Transaction",
                    cl, "setDesiredHdrHeadroom",
                    "android.view.SurfaceControl", float.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Object sc = param.args[0];
                            float hr = (Float) param.args[1];
                            if (hr == 0f && sc != null && sc.toString().contains("LoupeActivity")) {
                                param.args[1] = HEADROOM;
                                XposedBridge.log("lrhdrunlock: headroom 0 -> " + HEADROOM);
                            }
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("lrhdrunlock: headroom hook 失败 " + t);
        }
    }
}
