package ua.com.expertsoft.android_smeta.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 15.02.2016.
 */
public class FragmentSettings extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public interface onChangeLanguageListener{
        void onChangeLanguage();
    }

    ListPreference dataLang;
    String app_lang;
    String data_lang;

    public static final String INTERFACE_LANGUAGE = "interface_language";
    public static final String DATA_LANGUAGE = "data_language";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        dataLang = (ListPreference)findPreference(DATA_LANGUAGE);
        app_lang = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(INTERFACE_LANGUAGE,"");
        data_lang = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(DATA_LANGUAGE,"");
        boolean isRussian = isDataLanguageRus(getActivity());
        switch (MainActivity.transferLanguageToLocale(app_lang)){
            case "ru":
                data_lang = isRussian ? "Русский" : "Украинский";
                break;
            case "uk":
                data_lang = isRussian ? "Російська" : "Українська";
                break;
            case "en":
                data_lang = isRussian ? "Russian" : "Ukrainian";
                break;
        }
        dataLang.setSummary(data_lang);
    }

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public static boolean isDataLanguageRus(Context ctx){
        String langs = PreferenceManager.getDefaultSharedPreferences(ctx).getString(DATA_LANGUAGE,"");
        switch(langs.toLowerCase()){
            case "russian":
            case "русский":
            case "російська":
                return true;
            case "ukrainian":
            case "украинский":
            case "українська":
                return false;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(INTERFACE_LANGUAGE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            ((onChangeLanguageListener)getActivity()).onChangeLanguage();
        }else
        if (key.equals(DATA_LANGUAGE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
