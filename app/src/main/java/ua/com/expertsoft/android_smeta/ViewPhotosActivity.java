package ua.com.expertsoft.android_smeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.language.UpdateLanguage;

public class ViewPhotosActivity extends AppCompatActivity {

    ViewPager pager;
    MyViewPagerAdapter pageAdapter;
    String dir;
    ArrayList<String> files;
    ActionBar bar;
    FloatingActionButton fab;
    boolean isHidden = false;
    CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_view_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = getResources().getString(R.string.title_activity_view_photos);
        bar = getSupportActionBar();
        if (bar != null){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        dir = getIntent().getStringExtra("photoDir");
        pager = (ViewPager)findViewById(R.id.pager);
        File f = new File(dir);
        files = new ArrayList<>(f.listFiles().length);
        for (int i = 0; i< f.listFiles().length;i++){
            files.add(f.listFiles()[i].getAbsolutePath());
        }
        //pageAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),f.listFiles().length,files);
        pageAdapter = new MyViewPagerAdapter(this,f.listFiles().length,files);
        pager.setAdapter(pageAdapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDeleteDialog dlg = new ShowDeleteDialog();
                dlg.show(getSupportFragmentManager(), "deletingDialog");
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        if (savedInstanceState != null){
            isHidden = savedInstanceState.getBoolean("isInFullMode",false);
            if(isHidden){
                bar.hide();
                fab.hide();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    public void deletePhoto(){
        int pos = pager.getCurrentItem();
        String path = files.get(pos);
        (new File(path)).delete();
        files.remove(pos);
        if (files.size()>0) {
            pageAdapter.setCounter(files.size());
            pageAdapter.notifyDataSetChanged();
        }else{
            finish();
        }
    }

    public static class ShowDeleteDialog extends DialogFragment implements DialogInterface.OnClickListener{
        AlertDialog dialog;
        @Override
        public Dialog onCreateDialog(Bundle params){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.delete_photo_title);
            dialogBuilder.setMessage(R.string.delete_photo_caption);
            dialogBuilder.setPositiveButton(R.string.delete_photo_positive_button, this);
            dialogBuilder.setNegativeButton(R.string.delete_photo_negative_button, this);
            dialog = dialogBuilder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            });
            return dialog;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == Dialog.BUTTON_POSITIVE){
                ((ViewPhotosActivity) getActivity()).deletePhoto();
            }
            dialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putBoolean("isInFullMode",isHidden);
    }

    class MyViewPagerAdapter extends PagerAdapter{
        ImageView photo;
        String imgPath;
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int countView = 0;
        ArrayList<String> pathes;
        Context context;

        public MyViewPagerAdapter(Context ctx, int count, ArrayList<String> pathes){
            countView = count;
            this.pathes = pathes;
            context = ctx;
        }
        public ImageView instantiateItem(ViewGroup container, int position) {
            imgPath =  pathes.get(position);
            lParams.gravity = Gravity.CENTER;
            photo = new ImageView(context);
            photo.setImageBitmap(BitmapFactory.decodeFile(imgPath));
            photo.setLayoutParams(lParams);
            photo.setTag(imgPath);
            photo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(! isHidden) {
                            bar.hide();
                            fab.hide();
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            isHidden = true;
                        }else{
                            bar.show();
                            fab.show();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            isHidden = false;
                        }
                    }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                        return true;
                    }
                    return false;
                }
            });
            container.addView(photo);
            return photo;
        }
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setCounter(int newCount){
            countView = newCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView)object);
        }

        @Override
        public int getCount() {
            return countView;
        }
    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }

}
