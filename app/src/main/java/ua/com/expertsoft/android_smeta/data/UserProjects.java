package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mityai on 17.12.2015.
 */
public class UserProjects implements Serializable {

    private static final long serialVersionUID = -222864131214757024L;

    public static final String USER_PROJECTS_FIELD_ID =  "user_proj_id";
    public static final String USER_PROJECTS_FIELD_TYPE_ID = "user_proj_exp_type_id";
    public static final String USER_PROJECTS_FIELD_NAME = "user_proj_name";

    @DatabaseField(canBeNull = false, generatedId = true, columnName = USER_PROJECTS_FIELD_ID)
    private int userProjId;

    @DatabaseField(canBeNull = false, columnName = USER_PROJECTS_FIELD_TYPE_ID)
    private int userProjTypeId;

    @DatabaseField(canBeNull = false, columnName = USER_PROJECTS_FIELD_NAME)
    private String userProjName;

    private ArrayList<UserTask> userTasks;

    //PUBLIC METHODS
    public int getUserProjId(){
        return userProjId;
    }

    public void setUserProjTypeId(int typeId){
        userProjTypeId = typeId;
    }
    public int getUserProjTypeId(){
        return userProjTypeId;
    }

    public void setUserProjName(String name){
        userProjName = name;
    }
    public String getUserProjName(){
        return userProjName;
    }

    public UserProjects(){
         userTasks = new ArrayList<UserTask>();
    }

    /**********************   USERS TASKS   ***************************/
    public void setCurrentUserTask(UserTask task){userTasks.add(task);}
    public ArrayList<UserTask> getAllUsersTask(){return userTasks;}
    public void setAllUsersTask(ArrayList<UserTask> listTasks){userTasks = listTasks;}
    public UserTask getCurrentUserTask(int position){return userTasks.get(position);}
    public int getUserTasksCount(){return userTasks.size();}

    public void removeUsersTask(int position){
        userTasks.get(position).removeAllUsersSubTasks();
        userTasks.remove(position);
    }
    public void removeUsersTask(UserTask position){
        position.removeAllUsersSubTasks();
        userTasks.remove(position);
    }
    public void removeAllUsersTasks(){
        Iterator<UserTask> i = userTasks.iterator();
        UserTask task;
        while(i.hasNext()){
            task = i.next();
            task.removeAllUsersSubTasks();
            i.remove();
        }
    }

    public void refreshUsersTasks(UserTask ut){
        for(UserTask task : userTasks){
            if(task.getUserTaskId() == ut.getUserTaskId()) {
                try {
                    userTasks.set(userTasks.indexOf(task), ut);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
