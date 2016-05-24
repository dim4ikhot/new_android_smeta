package ua.com.expertsoft.android_smeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.data.UserProjects;
import ua.com.expertsoft.android_smeta.data.UserTask;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.CommonData;
import ua.com.expertsoft.android_smeta.adapters.UsersProjectsAdapter;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.UserSubTask;
import ua.com.expertsoft.android_smeta.dialogs.DialogEnterTask;
import ua.com.expertsoft.android_smeta.dialogs.EditExistsUserProject;

public class EditingList extends AppCompatActivity implements DialogEnterTask.OnGetGroupTaskName,
        AdapterView.OnItemClickListener, EditExistsUserProject.OnDeleteProjectListener,
        EditExistsUserProject.OnUpdateProjectListener{

    DialogEnterTask newTask;
    android.support.v7.app.ActionBar bar;
    UserProjectsCollection projCollection;

    ListView groupTasksList;
    UsersProjectsAdapter groupListAdapter;
    DBORM database;
    ArrayList<UserProjects> userProjectsList;
    ProjectsData prdata;
    UserProjects currentProj;
    CharSequence title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_editing_list);
        title = getResources().getString(R.string.edit_group);
        bar = getSupportActionBar();
        if(bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        projCollection = CommonData.userCollection; //(UserProjectsCollection)getIntent().getSerializableExtra("userProjects");
        database = new DBORM(this);

        if(projCollection != null){
            userProjectsList = new ArrayList<>();
            for(ProjectsData prDat : projCollection.getAllProject()) {
                if(prDat.getProjectsTypeUsers() != null) {
                    userProjectsList.add(prDat.getProjectsTypeUsers());
                }
            }
        }else {
            userProjectsList = database.getAllUserProjects();
            projCollection = new UserProjectsCollection();
            for(UserProjects pr: userProjectsList) {
                prdata = new ProjectsData();
                prdata.setProjectsTypeUsers(pr);
                projCollection.addNewProject(prdata);
            }
        }
        groupTasksList = (ListView)findViewById(R.id.listGroupTasks);
        groupListAdapter = new UsersProjectsAdapter(userProjectsList,this);
        groupTasksList.setAdapter(groupListAdapter);
        groupTasksList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_category_position, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id =  item.getItemId();
        switch(id){
            case R.id.addGroupPosition:
                newTask = new DialogEnterTask();
                newTask.show(getSupportFragmentManager(), "new_task");
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        CommonData.userCollection = projCollection;
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void getTaskName(String taskName) {
        try {
            Dao<UserProjects, Integer> userDao = database.getHelper().getUserProjectsDao();
            UserProjects userProj = new UserProjects();
            userProj.setUserProjName(taskName);
            //Change some later
            userProj.setUserProjTypeId(4);

            userDao.create(userProj);
            prdata = new ProjectsData();
            prdata.setProjectsTypeUsers(userProj);
            prdata.setProjectsType(4);
            projCollection.addNewProject(prdata);
            userProjectsList.add(userProj);
        }catch(SQLException e){
            e.printStackTrace();
        }
        groupListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EditExistsUserProject editprojects = new EditExistsUserProject();
        Bundle params = new Bundle();
        currentProj = (UserProjects)view.getTag();
        params.putSerializable("userProject", currentProj);
        params.putInt("listPosition", position);
        editprojects.setArguments(params);
        editprojects.show(getSupportFragmentManager(), "editDialog");
    }

    @Override
    public void onDeleteProject(UserProjects proj, int position) {
        userProjectsList.remove(position);
        try {
            for(UserTask t : projCollection.getProject(4+position).getProjectsTypeUsers().getAllUsersTask()){
                for(UserSubTask st : t.getAllUsersSubTask()){
                    database.getHelper().getUserSubTaskDao().delete(st);
                }
                database.getHelper().getUseTasksDao().delete(t);
            }
            database.getHelper().getUserProjectsDao().delete(proj);
        }catch(SQLException e){
            e.printStackTrace();
        }
        projCollection.getProject(4+position).getProjectsTypeUsers().removeAllUsersTasks();
        projCollection.removeProject(4 + position);
        groupListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onUpdateProject(UserProjects proj, String newName, int position) {
        proj.setUserProjName(newName);
        userProjectsList.set(position, proj);
        projCollection.updateProject(4 + position, proj);
        try {
            database.getHelper().getUserProjectsDao().update(proj);
        }catch(SQLException e){
            e.printStackTrace();
        }
        groupListAdapter.notifyDataSetChanged();
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
