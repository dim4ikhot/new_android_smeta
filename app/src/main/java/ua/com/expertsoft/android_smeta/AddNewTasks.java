package ua.com.expertsoft.android_smeta;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_add_new_tasks);
        getOverflowMenu();
        toolbar = (Toolbar) findViewById(R.id.new_task_toolbar);
        important = (ImageView)findViewById(R.id.imgImportant);
        mainTask = (EditText)findViewById(R.id.mainTask);
        mainTaskDone = (CheckBox)findViewById(R.id.mainTaskDone);
        rememberMe = (LinearLayout)findViewById(R.id.reminder);
        time = (TextView)findViewById(R.id.useTimeRememberValue);
        time.setOnClickListener(this);
        if(getIntent().getStringExtra("projectName").equals("")){
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
        }else{
            mainTask.setText(task.getUserTaskName());
            if(task.getUserCalendarTime() != null) {
                time.setText(new SimpleDateFormat("HH:mm").format(task.getUserCalendarTime()));
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
        time.setText(new SimpleDateFormat("HH:mm").format(cal.getTime()));
    }

    @Override
    public boolean onKeyUp(int keycode, KeyEvent e) {
        switch (keycode) {
            // show the bar if the menu button is pressed
            case KeyEvent.KEYCODE_MENU:
                try {
                    toolbar.getMenu().performIdentifierAction(R.id.mainBarItem,0);
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

                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mainBarItem:
                initItems(mainMenu);
                setItemsVisability(!isAsText);
                break;
        }
        return true;
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
                Fulltext += ((EditText)((LinearLayout)subTaskView).findViewById(R.id.editSubTask)).getText().toString() + "\n";
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
