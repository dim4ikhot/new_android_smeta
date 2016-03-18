package ua.com.expertsoft.android_smeta.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.ProjectsData;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.UserProjectsCollection;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.ProjectExp;
import ua.com.expertsoft.android_smeta.data.UserProjects;
import ua.com.expertsoft.android_smeta.data.UserTask;

/**
 * Created by mityai on 18.12.2015.
 */
public class LoadingNavigatinMenu extends AsyncTask<Integer,Integer,Integer> {

    public interface OnTaskFinished{
        void onFinished();
    }

    private static final int LOAD_ALL_MENU = 0;
    private static final int LOAD_ONLY_USERS_MENU = 1;
    private static final int LOAD_DEFAULT_MENU = 0;
    private static final int LOAD_USERS_MENU = 1;

    private static final int TYPE_USERS = 4;

    NavigationView navigationView;
    Menu menu;
    DBORM database;
    List<ProjectExp> kind;
    List<UserProjects> userProj;
    MenuItem addedItem;
    SubMenu submenu, userSubMenu;
    MenuItem userItem;
    MenuItem selectedItem;
    View actionView;

    ProjectExp pr;
    UserProjects u_pr;
    ProjectsData projectsData;
    UserProjectsCollection userCollection;
    Context activity;
    Context context;

    public LoadingNavigatinMenu(Context activity, NavigationView navigationView,
                                DBORM data, UserProjectsCollection collection, Context ctx){
        this.navigationView = navigationView;
        database = data;
        //kind = database.getAllProjectsKind();
        //userProj = database.getAllUserProjects();
        userCollection = collection;
        this.activity = activity;
        context = ctx;
    }

    @Override
    protected void onPreExecute(){
        menu = navigationView.getMenu();
        submenu = menu.findItem(R.id.projectsTasks).getSubMenu();
        userItem = menu.findItem(R.id.usersTasks);
        userSubMenu = userItem.getSubMenu();
        userItem.setVisible(true);
        userSubMenu.clear();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        kind = database.getAllProjectsKind();
        userProj = database.getAllUserProjects();
        int whatLoad = params[0];
        //i == 0 - OCAD, 1 - KLP, 2 - ZML, 3 - ARP
        for (int i = 0; i < kind.size(); i++) {
            if (i != kind.size() - 1) {
                if (whatLoad == LOAD_ALL_MENU) {
                    pr = kind.get(i);
                    //Create new project data for new user project
                    projectsData = new ProjectsData();
                    projectsData.setProjectsType(pr.getProjExpType());
                    projectsData.setProjectsTypeStandart(pr);
                    pr.setAllProject(database.getAllProjectsData(pr.getProjExpId()));
                    userCollection.addNewProject(projectsData);
                }
            } else
            // i == 4 - Load Users Projects
            if (i == kind.size() - 1) {
                if (userProj.size() != 0) {
                    boolean projectExists;
                    for (int j = 0; j < userProj.size(); j++) {
                        u_pr = userProj.get(j);
                        //Create new project data for new user project
                        if (i+j < userCollection.getProjectCount()) {
                            projectsData = userCollection.getProject(i+j);
                            if(projectsData != null){
                                projectExists = true;
                            }else{projectsData = new ProjectsData();
                                projectExists = false;
                            }
                        }else {
                            projectsData = new ProjectsData();
                            projectExists = false;
                        }
                        projectsData.setProjectsType(TYPE_USERS);
                        projectsData.setProjectsTypeUsers(u_pr);
                        //get all tasks from base
                        ArrayList<UserTask> userTasks = database.getUsersTasks(u_pr);
                        //fill the sub tasks
                        for (UserTask ut : userTasks) {
                            ut.setAllUsersSubTask(database.getUsersSubTasks(ut));
                        }
                        u_pr.setAllUsersTask(userTasks);
                        if(!projectExists) {
                            userCollection.addNewProject(projectsData);
                        }else{
                            userCollection.addNewProject(i+j,projectsData);
                        }
                    }
                }
            }
        }
        return whatLoad;
    }

    private MenuItem addNewMenuItem(SubMenu menu, int groupId,
                                           int itemId, int order, CharSequence title){
        return menu.add(groupId, itemId, order, title)
                .setIcon(R.drawable.ic_assignment)
                .setActionView(R.layout.navigation_menu_item);

    }

    @Override
    protected void onPostExecute(Integer result){
        super.onPostExecute(result);
        for(int i = 0; i < userCollection.getAllProject().size(); i++){
            projectsData = userCollection.getAllProject().get(i);
            if(projectsData.getProjectsTypeStandart() != null) {
                if(result == LOAD_ALL_MENU) {
                    pr = projectsData.getProjectsTypeStandart();
                    String name = "";
                    switch(i){
                        case 0:
                            name = context.getResources().getString(R.string.nav_ocad_projects);
                            break;
                        case 1:
                            name = context.getResources().getString(R.string.nav_cpln_projects);
                            break;
                        case 2:
                            name = context.getResources().getString(R.string.nav_zml_projects);
                            break;
                        case 3:
                            name = context.getResources().getString(R.string.nav_arp_projects);
                            break;
                    }
                    addedItem = addNewMenuItem(submenu, 0, i, 0, name/*pr.getProjExpName()*/);
                    if (i == 0) {
                        selectedItem = addedItem;
                    }
                    actionView = addedItem.getActionView();
                    if (pr.getAllProjects().size() > 0) {
                        ((TextView) actionView.findViewById(R.id.subItemsCount)).setText(String.valueOf(pr.getAllProjects().size()));
                    }
                }
            }else{
                if(projectsData.getProjectsTypeUsers()!= null) {
                    u_pr = projectsData.getProjectsTypeUsers();
                    addedItem = addNewMenuItem(userSubMenu, 0, i, 0, u_pr.getUserProjName());
                    if(result == LOAD_ONLY_USERS_MENU) {
                        selectedItem = addedItem;
                    }
                    actionView = addedItem.getActionView();
                    if (u_pr.getAllUsersTask().size() > 0) {
                        ((TextView) actionView.findViewById(R.id.subItemsCount)).setText(String.valueOf(u_pr.getAllUsersTask().size()));
                    }
                }
            }
            if(actionView != null) {
                actionView.setTag(projectsData);
            }
        }
        if ((userProj!= null)&(userProj.size() == 0)) {
            if(userItem != null) {
                userItem.setVisible(false);
                selectedItem = submenu.findItem(0);
            }
        }
        ((MainActivity)activity).onNavigationItemSelected(selectedItem);
        if(context != null) {
            ((OnTaskFinished) context).onFinished();
        }
    }
}
