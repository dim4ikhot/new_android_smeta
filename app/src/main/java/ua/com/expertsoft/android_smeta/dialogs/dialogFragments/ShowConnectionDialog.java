package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 16.01.2016.
 */
public class ShowConnectionDialog extends DialogFragment implements DialogInterface.OnClickListener{

    Context context;
    static int CONNECTIONS = 7;

    public ShowConnectionDialog(){
    }

    public void setContext(Context ctx){
        context = ctx;
    }

    public static void setMobileDataEnabled(Context context, boolean enabled)
            throws ClassNotFoundException,
            NoSuchFieldException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod =
                connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    public static void setWifiDataEnabled(Context context,boolean enabled){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.isWifiEnabled();
        return (netInfo != null && netInfo.isConnectedOrConnecting())|(wifiManager.isWifiEnabled());
    }



    @Override
    public Dialog onCreateDialog(Bundle params){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.connection_what_use);
        String[] connections = {getActivity().getResources().getString(R.string.useWiFi),
                getActivity().getResources().getString(R.string.useMobileInternet)};

        String[] wifiOnly = {getActivity().getResources().getString(R.string.useWiFi)};
        if (getArguments() == null){
            dialogBuilder.setSingleChoiceItems(connections, -1, this);
        }else{
            getArguments().getInt("connections");
            dialogBuilder.setSingleChoiceItems(wifiOnly, -1, this);
        }
        return dialogBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case 0:
                setWifiDataEnabled(getActivity(), true);
                getActivity().startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),CONNECTIONS);
                break;
            case 1:
                try {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        setMobileDataEnabled(getActivity(), true);
                        getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), CONNECTIONS);
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
                        getActivity().startActivityForResult(intent, CONNECTIONS);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
        dialog.dismiss();
    }
}
