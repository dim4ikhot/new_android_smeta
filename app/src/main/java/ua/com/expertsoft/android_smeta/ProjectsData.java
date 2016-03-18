package ua.com.expertsoft.android_smeta;

import java.io.Serializable;

import ua.com.expertsoft.android_smeta.data.ProjectExp;
import ua.com.expertsoft.android_smeta.data.UserProjects;

/**
 * Created by mityai on 21.12.2015.
 */
public class ProjectsData implements Serializable {

    public static final String usersProjects = "users";
    public static final String standtartProjects = "standarts";

    private int projectsType;

    private ProjectExp projectsTypeStandart;
    private UserProjects projectsTypeUsers;


    public ProjectsData(){
    }

    //Setter & getter for standard project type
    public int getProjectsType(){
        return projectsType;
    }
    public void setProjectsType(int type){
        projectsType = type;
    }

    //Setter & getter for standart project type
    public ProjectExp getProjectsTypeStandart(){
        return projectsTypeStandart;
    }
    public void setProjectsTypeStandart(ProjectExp type){
        projectsTypeStandart = type;
    }

    //Setter & getter for user project type
    public UserProjects getProjectsTypeUsers(){
        return projectsTypeUsers;
    }
    public void setProjectsTypeUsers(UserProjects type){
        projectsTypeUsers = type;
    }

}
