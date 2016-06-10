package ua.com.expertsoft.android_smeta;

/*
 * Created by mityai on 09.06.2016.
 */
public final class MySimpleClass {

    private static String someText;

    private static MySimpleClass _instance;

    private MySimpleClass(){}

    public static synchronized MySimpleClass getInstance(){
        if(_instance == null) {
            _instance = new MySimpleClass();
        }
        return _instance;
    }

    public void setSomeText(String text){
        someText = text;
    }

    public String getSomeText(){return someText;}
}
