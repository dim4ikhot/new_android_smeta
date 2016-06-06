package ua.com.expertsoft.android_smeta.static_data;

/**
 * If you want to build different versions of app, ypu need to change package name:
 * - for rus version package name should be "ru.expersoft.android_smeta" (without quotes)
 * - for ukr version package name should be "ua.com.expertsoft.android_smeta"
 * - for eng version package name should be "net.cableproject.online.android_smeta"
 * Then you need change appLanguage variable to need language(see below)
 * Created by mityai on 06.06.2016.
 */
public class CompilerParams {

    //version can be "true" = full or "false" - demo
    //This field responses for ads showing.
    static boolean isFullVersion = true;
    //appLanguage can be past at short International language name: "ru", "uk", "en"
    static String appLanguage = "ru";


    public static boolean isFullVersion(){
        return isFullVersion;
    }

    public static String getAppLanguage(){
        return appLanguage;
    }
}
