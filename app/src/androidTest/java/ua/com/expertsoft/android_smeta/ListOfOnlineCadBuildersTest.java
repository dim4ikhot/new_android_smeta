package ua.com.expertsoft.android_smeta;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListOfOnlineCadBuildersTest extends ActivityInstrumentationTestCase2<ListOfOnlineCadBuilders>{

    ListOfOnlineCadBuilders activity;

    public ListOfOnlineCadBuildersTest(){
       super(ListOfOnlineCadBuilders.class);
    }

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }


    @Test
    public void testInternetConnection(){
        if (activity.buildList.getCount() != 0){
            activity.buildList.getAdapter().getItem(0);
        }
    }
}
