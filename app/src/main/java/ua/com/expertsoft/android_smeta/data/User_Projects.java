package ua.com.expertsoft.android_smeta.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mityai on 17.12.2015.
 */
public class User_Projects implements Serializable {

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

    private ArrayList<User_Task> userTasks;

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

    public User_Projects(){
         userTasks = new ArrayList<User_Task>();
    }

    /**********************   USERS TASKS   ***************************/
    public void setCurrentUserTask(User_Task task){userTasks.add(task);}
    public ArrayList<User_Task> getAllUsersTask(){return userTasks;}
    public void setAllUsersTask(ArrayList<User_Task> listTasks){userTasks = listTasks;}
    public User_Task getCurrentUserTask(int position){return userTasks.get(position);}
    public int getUserTasksCount(){return userTasks.size();}

    public void removeUsersTask(int position){
        userTasks.get(position).removeAllUsersSubTasks();
        userTasks.remove(position);
    }
    public void removeUsersTask(User_Task position){
        position.removeAllUsersSubTasks();
        userTasks.remove(position);
    }
    public void removeAllUsersTasks(){
        Iterator<User_Task> i = userTasks.iterator();
        User_Task task;
        while(i.hasNext()){
            task = i.next();
            task.removeAllUsersSubTasks();
            i.remove();
        }
    }

    public void refreshUsersTasks(User_Task ut){
        for(User_Task task : userTasks){
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
