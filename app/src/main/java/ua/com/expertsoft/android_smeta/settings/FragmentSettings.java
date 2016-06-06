package ua.com.expertsoft.android_smeta.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.asynctasks.LoadingNavigatinMenu;
import ua.com.expertsoft.android_smeta.static_data.CompilerParams;

/*
 * Created by mityai on 15.02.2016.
 */
public class FragmentSettings extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public interface onChangeLanguageListener{
        void onChangeLanguage();
    }

    ListPreference dataLang, appLang;
    CheckBoxPreference checkBoxPreference;
    PreferenceCategory langSettings;
    PreferenceScreen mainScreen;
    SharedPreferences preferences;
    String app_lang;
    String data_lang;
    Boolean isChecked;

    public static final String INTERFACE_LANGUAGE = "interface_language";
    public static final String DATA_LANGUAGE = "data_language";
    public static final String SHOW_HIDDEN_WORKS = "show_hidden";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        dataLang = (ListPreference)findPreference(DATA_LANGUAGE);
        appLang = (ListPreference)findPreference("interface_language");
        mainScreen = getPreferenceScreen();
        langSettings = (PreferenceCategory) mainScreen.getPreference(0);
        String[] languages = getResources().getStringArray(R.array.languages);
        switch (CompilerParams.getAppLanguage()){
            case "ru":
                String[] languagesRus = new String[2];
                int counter = 0;
                for(String s: languages){
                    if(!s.toLowerCase().contains("укр") & !s.toLowerCase().contains("ukr")) {
                        languagesRus[counter] = s;
                        counter++;
                    }
                }
                appLang.setEntries(languagesRus);
                getParent(dataLang).removePreference(dataLang);
                break;
            case "uk":
                appLang.setEntries(languages);
                appLang.setSummary(languages[2]);
                break;
            case "en":
                getParent(langSettings).removePreference(langSettings);
                break;
        }
        String[] languages_data = getResources().getStringArray(R.array.languages_data);
        dataLang.setEntries(languages_data);
        checkBoxPreference = (CheckBoxPreference)findPreference(SHOW_HIDDEN_WORKS);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        app_lang = preferences.getString(INTERFACE_LANGUAGE,"");
        data_lang = preferences.getString(DATA_LANGUAGE,"");
        isChecked = preferences.getBoolean(SHOW_HIDDEN_WORKS,true);
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
        checkBoxPreference.setChecked(isChecked);
    }

    private PreferenceGroup getParent(Preference preference)
    {
        return getParent(getPreferenceScreen(), preference);
    }

    private PreferenceGroup getParent(PreferenceGroup root, Preference preference)
    {
        for (int i = 0; i < root.getPreferenceCount(); i++)
        {
            Preference p = root.getPreference(i);
            if (p == preference)
                return root;
            if (PreferenceGroup.class.isInstance(p))
            {
                PreferenceGroup parent = getParent((PreferenceGroup)p, preference);
                if (parent != null)
                    return parent;
            }
        }
        return null;
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
        else if (key.equals(SHOW_HIDDEN_WORKS)){

        }
    }
}
