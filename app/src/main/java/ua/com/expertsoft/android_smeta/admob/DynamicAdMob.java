package ua.com.expertsoft.android_smeta.admob;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.static_data.CompilerParams;

/*
 * Created by mityai on 08.06.2016.
 */
public class DynamicAdMob {

    ViewGroup adMobParent;
    Context context;

    public DynamicAdMob(Context ctx, ViewGroup parent){
        adMobParent = parent;
        context = ctx;
    }

    public void showAdMob(){
        //ADS PATH
        AdView programmAdView = new AdView(context);
        programmAdView.setAdSize(AdSize.BANNER);
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT|Gravity.BOTTOM;
        programmAdView.setLayoutParams(params);
        if(!CompilerParams.isFullVersion()) {
            try {
                String bannerId = context.getResources()
                        .getString(R.string.banner_ad_unit_app_identifier_test);
                String adUnitId = context.getResources()
                        .getString(R.string.banner_ad_unit_id_test);

                if(!CompilerParams.getIsDebuggable()) {
                    switch(CompilerParams.getAppLanguage()){
                        case "ru":
                            bannerId = context.getResources()
                                    .getString(R.string.banner_ad_unit_app_identifier_rus);
                            adUnitId = context.getResources()
                                    .getString(R.string.banner_ad_unit_id_rus);
                            break;
                        case "uk":
                            bannerId = context.getResources()
                                    .getString(R.string.banner_ad_unit_app_identifier_ukr);
                            adUnitId = context.getResources()
                                    .getString(R.string.banner_ad_unit_id_ukr);
                            break;
                        case "en":
                            bannerId = context.getResources()
                                    .getString(R.string.banner_ad_unit_app_identifier_eng);
                            adUnitId = context.getResources()
                                    .getString(R.string.banner_ad_unit_id_eng);
                            break;
                    }
                }
                programmAdView.setAdUnitId(adUnitId);
                MobileAds.initialize(context.getApplicationContext(), bannerId);
                AdRequest adRequest = new AdRequest.Builder().build();
                if(programmAdView != null) {
                    programmAdView.loadAd(adRequest);
                    adMobParent.addView(programmAdView);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
