package ua.com.expertsoft.android_smeta.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;

/*
 * Created by mityai on 17.02.2016.
 */
public class UpdateLanguage {
    public interface onUpdateLocaleListener{
        void onUpdateLocale();
    }
    public static void setDefaultLocale(String localeName, Context ctx){
        Locale locale = new Locale(localeName);
        Locale.setDefault(locale);
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Configuration config = new Configuration();
        config.locale = locale;
        ctx.getResources().updateConfiguration(config, dm);
    }

    public static String transferLanguageToLocale(String lang){
        String langs = lang.toLowerCase();
        switch(langs){
            case "english":
            case "английский":
            case "англійська":
                return "en";
            case "russian":
            case "русский":
            case "російська":
                return "ru";
            case "ukrainian":
            case "украинский":
            case "українська":
                return "uk";
        }
        return "ru";
    }

    public static void updateAppConfiguration(Context ctx, onUpdateLocaleListener listener){
        PreferenceManager.setDefaultValues(ctx, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String syncConnPref = sharedPref.getString(FragmentSettings.INTERFACE_LANGUAGE, "");
        if(! syncConnPref.equals("")) {
            setDefaultLocale(transferLanguageToLocale(syncConnPref), ctx);
        }else{
            setDefaultLocale("ru",ctx);
        }
        listener.onUpdateLocale();
    }
}
