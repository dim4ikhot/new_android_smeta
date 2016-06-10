package ua.com.expertsoft.android_smeta.static_data;

/**
 * !!!!!!!!NOTE: IT'S VERY IMPORTANT
 * When you will build next version of app, you need change "VERSION CODE" and "VERSION NAME"
 * at all of app module flavors(select app module -> right click -> Open Module Settings-> Flavors)
 *
 *
 * If you want to build different versions of app, ypu need:
 * <p/>
 * 1) Change product version (isFullVersion) to build app you need
 * <p/>
 * 2) Change app language (appLanguage) to build app on language you need
 * <p/>
 * 3) Go to AndroidManifest and change by hands Application label and MainActivity label
 *    Note that the labels for full and free version must be different.
 *    Because the shortcut would not create.
 * <p/>
 * //You can skip steps 4 and 5. Now it makes programmatically, depends from isDebuggable and appLanguage flags
 * 4) Open content_main.xml and change 'ads:adUnitId=' of adView you need(depends from App language)
 * <p/>
 * 5) Go to MainActivity onCreate method and change getString bannerId(depends from app language)
 * <p/>
 * 6) Open /app folder and replace google-service.json with
 * google-service.json in /app/google_services/full folder
 * <p/>
 * 7) Go to Build menu and select 'Generate Signed APK...'
 * 7.1) In opened window select an existing keystore(C:\Users\mityai.EXPERTSO\.AndroidStudio1.5\keystore)
 * by click 'Choose Existing...'
 * 7.2) Open this folder in Windows manger. and open ReadMe.txt. You find there all passwords and
 * aliases.
 * 7.2) Select need alias by clicking 3point button on Key alias field.
 * 7.2.1) Select 'use an existing key:' and select nedd alias
 * 7.3) Enter need passwords and press next button.
 * 7.4) On opened window choose Build type: 'release' and select need flavor(you'll see 6 different flavors.)
 * 7.5) Click 'Finish button'.
 * <p/>
 * 8) At the end you must get 6 different versions of app. They are:
 * FULL VERSION:
 * - for rus version package name should be "ru.expertsoft.android_smeta"
 * - for ukr version package name should be "ua.com.expertsoft.android_smeta"
 * - for eng version package name should be "com.cableproject.online.android_estimate"
 * <p/>
 * FREE VERSIONS:
 * - for rus version package name should be "ru.expertsoft.android_smeta.free"
 * - for ukr version package name should be "ua.com.expertsoft.android_smeta.free"
 * - for eng version package name should be "com.cableproject.online.android_estimate.free"
 * <p/>
 * 9) After success built you need return back all changes:
 * first of all replace google-service.json with google-service.json in /app/google_services/test folder
 * set appLanguage to 'uk'
 * set isFullVersion to true
 * press 'Sync project'
 * <p/>
 *
 * FINALLY do not forget set connection with app on Play Store on AdMob web site...
 * <p/>
 * <p/>
 * Created by mityai on 06.06.2016.
 */
public class CompilerParams {

    //version can be "true" = full or "false" - free
    //This field responses for ads showing.
    static boolean isFullVersion = false;

    //appLanguage can be past at short International language name: "ru", "uk", "en"
    static String appLanguage = "uk";

    //this variable service for automatic set params to AdView
    //If app is debuggable - adView.adUnitId would be ..._Test
    // and banner id also would be ..._Test.
    static boolean isDebuggable = true;


    public static boolean isFullVersion() {
        return isFullVersion;
    }

    public static String getAppLanguage() {
        return appLanguage;
    }

    public static boolean getIsDebuggable() {
        return isDebuggable;
    }
}
