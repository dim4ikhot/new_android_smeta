package ua.com.expertsoft.android_smeta;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Project_Exp;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.User_Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/**
 * Created by mityai on 21.12.2015.
 */
public class ProjectsData implements Serializable {

    public static final String usersProjects = "users";
    public static final String standtartProjects = "standarts";

    private int projectsType;

    private Project_Exp projectsTypeStandart;
    private User_Projects projectsTypeUsers;


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
    public Project_Exp getProjectsTypeStandart(){
        return projectsTypeStandart;
    }
    public void setProjectsTypeStandart(Project_Exp type){
        projectsTypeStandart = type;
    }

    //Setter & getter for user project type
    public User_Projects getProjectsTypeUsers(){
        return projectsTypeUsers;
    }
    public void setProjectsTypeUsers(User_Projects type){
        projectsTypeUsers = type;
    }

}
