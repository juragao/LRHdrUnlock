package com.example.lrhdrunlock;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.adobe.lrmobile".equals(lpparam.packageName)) return;
        XposedHelpers.findAndHookMethod("com.adobe.lrmobile.utils.l",
                lpparam.classLoader, "d", XC_MethodReplacement.returnConstant(true));
    }
}