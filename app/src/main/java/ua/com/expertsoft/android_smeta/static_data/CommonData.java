package ua.com.expertsoft.android_smeta.static_data;

import android.content.Context;
import android.support.design.widget.NavigationView;

import ua.com.expertsoft.android_smeta.UserProjectsCollection;
import ua.com.expertsoft.android_smeta.data.DBORM;

/*
 * Created by mityai on 02.02.2016.
 */
public final class CommonData {

    private static DBORM database;
    private static NavigationView navigation;
    private static UserProjectsCollection userCollection;
    private static Context context;

    private static CommonData _instanse;

    public static synchronized CommonData getInstance(){
        if(_instanse == null){
            _instanse = new CommonData();
        }
        return _instanse;
    }

    public void setDatabase(DBORM data){
        database = data;
    }

    public DBORM getDatabase(){return database;}

    //Nav. view
    public void setNavigation(NavigationView v){
        navigation = v;
    }
    public NavigationView getNavigation(){return navigation;}

    //User collection
    public void setUserCollection(UserProjectsCollection collection){
        userCollection = collection;
    }
    public UserProjectsCollection getUserCollection(){return userCollection;}

    //Context
    public void setContext(Context ctx){context = ctx;}
    public Context getContext(){return context;}
}
