package ua.com.expertsoft.android_smeta;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import ua.com.expertsoft.android_smeta.admob.DynamicAdMob;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.UserSubTask;
import ua.com.expertsoft.android_smeta.data.UserTask;
import ua.com.expertsoft.android_smeta.dialogs.ImportantColorDialog;
import ua.com.expertsoft.android_smeta.dialogs.ImportantColorDialog.OnGetImpotrantColor;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.TimeTaskDialog;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;


public class AddNewTasks extends AppCompatActivity implements View.OnClickListener,
        OnGetImpotrantColor, CompoundButton.OnCheckedChangeListener, TimeTaskDialog.OnGetTimeTaskListener {

    public static final String TASK_KEY = "task";
    public static final int RESULT_DELETE = 2;
    public static final int MAKE_PHOTO = 3;

    ActionBar bar;
    Toolbar toolbar;
    ImageView important;
    public static final int[] impColors = {R.color.colorNoImpotent,R.color.colorLittleImpotent,
                        R.color.colorMediumImpotent,R.color.colorVeryImpotent};

    EditText mainTask;
    LinearLayout scroll;
    UserTask task;
    UserSubTask subTask;
    CheckBox mainTaskDone;
    LinearLayout rememberMe;
    TextView time;
    DBORM base;
    Menu mainMenu;

    MenuItem asTextItem, asCheckListItem;
    boolean isAsText = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_add_new_tasks);
        new DynamicAdMob(this, (LinearLayout)findViewById(R.id.new_task_main_screen)).showAdMob();
        getOverflowMenu();
        toolbar = (Toolbar) findViewById(R.id.new_task_toolbar);
        important = (ImageView)findViewById(R.id.imgImportant);
        mainTask = (EditText)findViewById(R.id.mainTask);
        mainTaskDone = (CheckBox)findViewById(R.id.mainTaskDone);
        rememberMe = (LinearLayout)findViewById(R.id.reminder);
        time = (TextView)findViewById(R.id.useTimeRememberValue);
        if(time != null) {
            time.setOnClickListener(this);
        }
        String projName = getIntent().getStringExtra("projectName");

        if(projName!= null && projName.equals("")){
            rememberMe.setVisibility(View.VISIBLE);
        }
        scroll = (LinearLayout)findViewById(R.id.scrollLayout);
        base = new DBORM(this);
        task = (UserTask) getIntent().getSerializableExtra("taskData");
        if(task == null){
            task = new UserTask();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            task.setUserCalendarDate(calendar.getTime());
            task.setUserGuid(UUID.randomUUID().toString());
        }else{
            mainTask.setText(task.getUserTaskName());
            if(task.getUserCalendarTime() != null) {
                time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(task.getUserCalendarTime()));
            }
            important.setImageResource(impColors[task.getUserTaskImportance()]);
            mainTaskDone.setVisibility(View.VISIBLE);
            mainTaskDone.setChecked(task.getUserTaskDone());
            mainTaskDone.setOnCheckedChangeListener(this);
            for(UserSubTask subTask: task.getAllUsersSubTask()){
                scroll.addView(exitstsSubTaskView(subTask));
                isAsText = false;
            }
        }
        important.setOnClickListener(this);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        if(bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getIntent().getStringExtra("projectName"));
        }
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        task.setUserTaskName(mainTask.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(TASK_KEY, task);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void OnGetTime(int hour, int minute) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.DAY_OF_WEEK, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        task.setUserCalendarTime(cal.getTime());
        time.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(cal.getTime()));
    }

    @Override
    public boolean onKeyUp(int keycode, KeyEvent e) {
        switch (keycode) {
            // show the bar if the menu button is pressed
            case KeyEvent.KEYCODE_MENU:
                try {
                  //  toolbar.getMenu().performIdentifierAction(R.id.mainBarItem,0);
                }catch(NullPointerException exp){
                    exp.printStackTrace();
                }
                return false;
        }
        return super.onKeyUp(keycode, e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_new_task_menu, menu);
        mainMenu = menu;
        /*if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                    initItems(menu);
                    setItemsVisability(!isAsText);
                    MenuItem itemViewPhoto = menu.findItem(R.id.action_view_task_photos);
                    File[] foundFiles = (new File(MainActivity.photosDir+"/"+task.getUserGuid())).listFiles();
                    if(foundFiles != null && foundFiles.length > 0){
                        itemViewPhoto.setEnabled(true);
                    }
                    else{
                        itemViewPhoto.setEnabled(false);
                    }
                }
                catch(NoSuchMethodException e){
                    e.printStackTrace();
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setItemsVisability(boolean isVisible){
        asTextItem.setVisible(isVisible);
        asCheckListItem.setVisible(!isVisible);
    }

    private void initItems(Menu menu){
        asTextItem = menu.findItem(R.id.viewAsText);
        asCheckListItem = menu.findItem(R.id.viewAsCheckbox);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.viewAsText:
                getFulltextBySplitedParts();
                isAsText = true;
                break;
            case R.id.viewAsCheckbox:
                splitTextByEnter(mainTask.getText().toString());
                isAsText = false;
                break;
            case R.id.deleteTask:
                setResult(RESULT_DELETE);
                finish();
                break;
            case R.id.newTask:
                task.setUserTaskName(mainTask.getText().toString());
                setResult(RESULT_OK, new Intent().putExtra("doAddNewTask", true).putExtra(TASK_KEY, task));
                finish();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
          /* case R.id.mainBarItem:
                initItems(mainMenu);
                setItemsVisability(!isAsText);
                break;*/
            case R.id.action_make_photo_task:
                File dir;
                dir = new File(MainActivity.photosDir + task.getUserGuid());
                if (!dir.isDirectory()) {
                    dir.mkdirs();
                }
                showCamera(dir.getPath());
                break;
            case R.id.action_view_task_photos:
                openFolder();
                break;
        }
        return true;
    }


    private void showCamera(String dir){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File directory = new File(dir);
        if(! directory.isDirectory()){
            directory.mkdirs();
        }
        File tmpFile = new File(directory.getPath()+ File.separator, System.currentTimeMillis()+".jpg");
        Uri uri = Uri.fromFile(tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MAKE_PHOTO);
        }
    }

    public void openFolder()
    {
        Intent intent = new Intent(this, ViewPhotosActivity.class);
        intent.putExtra("photoDir", MainActivity.photosDir+"/"+task.getUserGuid());
        startActivity(intent);
    }

    private void splitTextByEnter(String totalText){
        String [] splited = totalText.split("\\r?\\n");
        mainTask.setText(splited[0]);
        mainTask.setTextSize(20f);
        String tasks;
        for(int i = 1; i< splited.length; i++){
            tasks = splited[i];
            scroll.addView(createSubTaskView(tasks));
        }
    }

    private View createSubTaskView(String name){
        View subTaskView;
        subTaskView = getLayoutInflater().inflate(R.layout.subtask_activity, null);
        ((EditText)subTaskView.findViewById(R.id.editSubTask)).setText(name);
        subTask = new UserSubTask();
        subTask.setUserSubTaskDone(false);
        subTask.setUserSubTaskTaskId(task.getUserTaskId());
        subTask.setUserSubTaskTaskForeign(task);
        subTask.setUserSubTaskName(name);
        task.setCurrentUserSubTask(subTask);
        subTaskView.setTag(subTask);
        subTaskView.setOnClickListener(this);
        return subTaskView;
    }

    private View exitstsSubTaskView(UserSubTask subtask){
        View subTaskView = getLayoutInflater().inflate(R.layout.subtask_activity, null);
        ((EditText)subTaskView.findViewById(R.id.editSubTask)).setText(subtask.getUserSubTaskName());
        ((CheckBox)subTaskView.findViewById(R.id.checkBoxDoneTask)).setChecked(subtask.getUserSubTaskDone());
        subTaskView.setTag(subtask);
        subTaskView.setOnClickListener(this);
        return subTaskView;
    }

    private void getFulltextBySplitedParts(){
        View subTaskView;
        mainTask.setTextSize(18f);
        String Fulltext = "";
        for(int i = 0; i< scroll.getChildCount(); i++){
            subTaskView = scroll.getChildAt(i);
            if(subTaskView instanceof LinearLayout){
                Fulltext += ((EditText)(subTaskView).findViewById(R.id.editSubTask)).getText().toString() + "\n";
            }
            else
            if(subTaskView instanceof EditText){
                Fulltext += ((EditText)subTaskView).getText().toString() + "\n";
            }
        }
        for(UserSubTask st : task.getAllUsersSubTask()){
            try {
                base.getHelper().getUserSubTaskDao().delete(st);
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        task.removeAllUsersSubTasks();
        scroll.removeAllViews();
        mainTask.setTextSize(18f);
        mainTask.setText(Fulltext);
        scroll.addView(mainTask);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imgImportant:
                ImportantColorDialog colorDialog = new ImportantColorDialog();
                colorDialog.show(getSupportFragmentManager(), "colors");
                break;
            case R.id.mainTaskDone:
                task.setUserTaskDone(((CheckBox)v).isChecked());
                break;
            case R.id.useTimeRememberValue:
                new TimeTaskDialog().show(getSupportFragmentManager(), "taskTime");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(requestCode){
            case MAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    File dir;
                    dir = new File(MainActivity.photosDir + task.getUserGuid());
                    if (!dir.isDirectory()) {
                        dir.mkdirs();
                    }
                    showCamera(dir.getPath());
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode,data);
    }

    @Override
    public void onGetColor(int colorItem) {
        important.setImageResource(impColors[colorItem]);
        task.setUserTaskImportance(colorItem);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.mainTaskDone:
                task.setUserTaskDone(isChecked);
                break;
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
