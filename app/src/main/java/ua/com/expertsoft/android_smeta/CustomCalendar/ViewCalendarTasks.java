package ua.com.expertsoft.android_smeta.CustomCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import ua.com.expertsoft.android_smeta.AddNewTasks;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.adapters.UsersTasksAdapter;
import ua.com.expertsoft.android_smeta.data.CalendarDate;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.User_SubTask;
import ua.com.expertsoft.android_smeta.data.User_Task;

public class ViewCalendarTasks extends AppCompatActivity implements AdapterView.OnItemClickListener,
        UsersTasksAdapter.onGetTaskDoneListener {

    final static int ADD_NEW_TASK = 1;
    final static int EDIT_EXISTS_TASK = 2;


    private CharSequence mTitle = "";
    User_Task usersTask;
    DBORM database;
    ListView tasks;
    LinearLayout emptyBox;
    CalendarDate calendarData;
    UsersTasksAdapter adp;
    User_Task tempTask;
    ActionBar bar;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = new DBORM(this);
        tasks = (ListView)findViewById(R.id.buildersUsersList);
        tasks.setOnItemClickListener(this);
        emptyBox = (LinearLayout)findViewById(R.id.layoutEmptyBox);
        calendarData = (CalendarDate)getIntent().getSerializableExtra("calendarDate");
        if(calendarData == null){
            calendarData = (CalendarDate)getIntent().getSerializableExtra("infoFromNotify");
        }
        bar = getSupportActionBar();
        if(calendarData != null){
            if(bar != null){
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle(bar.getTitle() + " " + sdf.format(calendarData.getDate()));
            }
            refreshUsersTasksList();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCalendarTasks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewTasks.class);
                intent.putExtra("projectName", mTitle);
                startActivityForResult(intent, ADD_NEW_TASK);
            }
        });
    }

    private boolean setTasksVisible(){
        if (!calendarData.hasTasks()) {
            emptyBox.setVisibility(View.VISIBLE);
            tasks.setVisibility(View.GONE);
            return false;
        } else {
            emptyBox.setVisibility(View.GONE);
            tasks.setVisibility(View.VISIBLE);
            updateUsersTasksList();
            return true;
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK, new Intent().putExtra("calendarDate", calendarData));
        finish();
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
    public void onGetUserTaskDone(User_Task ut) {
        try{
            if(ut.getUserSubTasksCount() > 0){
                for(User_SubTask ust: ut.getAllUsersSubTask()){
                    ust.setUserSubTaskDone(ut.getUserTaskDone());
                    database.getHelper().getUserSubTaskDao().createOrUpdate(ust);
                }
            }
            database.getHelper().getUseTasksDao().createOrUpdate(ut);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void refreshUsersTasksList(){
        if (setTasksVisible()) {
            adp.notifyDataSetChanged();
        }
    }

    private void updateUsersTasksList(){
        adp = new UsersTasksAdapter(calendarData.getAllTasks(), this);
        tasks.setAdapter(adp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case ADD_NEW_TASK:
                    if (resultCode == RESULT_OK) {
                        //Update users builds
                        usersTask = (User_Task) data.getSerializableExtra(AddNewTasks.TASK_KEY);
                        usersTask.setUserCalendarDate(calendarData.getDate());
                        if (usersTask != null) {
                            try {
                                //Create new users task
                                if (!usersTask.getUserTaskName().equals("")) {
                                    usersTask.setUserTaskProjectForeign(null);
                                    usersTask.setUserTaskUProjId(-1);
                                    database.getHelper().getUseTasksDao().create(usersTask);

                                    //Add sub tasks
                                    if (usersTask.getUserSubTasksCount() > 0) {
                                        User_SubTask userSubTask;
                                        for (int i = 0; i < usersTask.getUserSubTasksCount(); i++) {
                                            userSubTask = usersTask.getCurrentUserSubTask(i);
                                            try {
                                                userSubTask.setUserSubTaskTaskForeign(usersTask);
                                                userSubTask.setUserSubTaskTaskId(usersTask.getUserTaskId());
                                                database.getHelper().getUserSubTaskDao().create(userSubTask);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    calendarData.setCurrentTask(usersTask);
                                    refreshUsersTasksList();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case EDIT_EXISTS_TASK:
                    if (resultCode == Activity.RESULT_OK) {
                        usersTask = (User_Task) data.getSerializableExtra(AddNewTasks.TASK_KEY);
                        if (usersTask != null) {
                            try {
                                database.getHelper().getUseTasksDao().createOrUpdate(usersTask);
                                int index = calendarData.getAllTasks().indexOf(tempTask);
                                calendarData.getAllTasks().set(index, usersTask);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (usersTask.getUserSubTasksCount() > 0) {
                                User_SubTask userSubTask;
                                for (int i = 0; i < usersTask.getUserSubTasksCount(); i++) {
                                    userSubTask = usersTask.getCurrentUserSubTask(i);
                                    try {
                                        userSubTask.setUserSubTaskTaskForeign(usersTask);
                                        userSubTask.setUserSubTaskTaskId(usersTask.getUserTaskId());
                                        database.getHelper().getUserSubTaskDao().createOrUpdate(userSubTask);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            refreshUsersTasksList();
                        }
                    } else if (resultCode == AddNewTasks.RESULT_DELETE) {
                        for (User_SubTask userSubTask : tempTask.getAllUsersSubTask()) {
                            try {
                                database.getHelper().getUserSubTaskDao().delete(userSubTask);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            database.getHelper().getUseTasksDao().delete(tempTask);
                            calendarData.removeTask(tempTask);
                            refreshUsersTasksList();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "MainActivity: onActivityResult", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddNewTasks.class);
        tempTask = (User_Task)view.getTag(); //projectsData.getProjectsTypeUsers().getAllUsersTask().get(position);
        intent.putExtra("taskData",tempTask);
        intent.putExtra("projectName", "");
        startActivityForResult(intent, EDIT_EXISTS_TASK);
    }
}
