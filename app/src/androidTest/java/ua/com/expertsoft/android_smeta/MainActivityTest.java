package ua.com.expertsoft.android_smeta;


import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    FloatingActionButton fab;
    ListOfOnlineCadBuildersTest builds = new ListOfOnlineCadBuildersTest();

    public MainActivityTest(){
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
    }

    @Test
    public void interfaceTest(){
        //assertEquals(true, activity != null);
        if(activity != null){
            if(activity.buildersUser.getAdapter()!= null) {
                fab.callOnClick();
            }else{
                try {
                    builds.setUp();
                    builds.testInternetConnection();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

}
