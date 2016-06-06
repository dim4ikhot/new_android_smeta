package ua.com.expertsoft.android_smeta.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ua.com.expertsoft.android_smeta.LoginActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;

/*
 * Created by mityai on 14.01.2016.
 */
public class UploadPhotoToServer extends AsyncTask<Void,Integer,Integer>{

    public final String BASE_URL = "/test_cad/php/usr_controller_api.php?action=put_photos&proj_guid=";
    public final String BASE_URL_LOAD = "/test_cad/php/usr_controller_api.php?action=get_photos&proj_guid=";
    
    ProgressDialog progressDialog;
    Context context;
    File dir;
    String uploadServerUri = "";
    String urlGetUploadedPhotos = "";
    URL serverUri;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024 * 1024;
    int serverResponseCode = 0;
    String mail, password;

    public UploadPhotoToServer(Context ctx, String filePaht){
        context = ctx;
        dir = new File(filePaht);
        getLoginParams(ctx);
        uploadServerUri = LoginActivity.getServies(ctx) + BASE_URL +
                ProjectInfo.project.getProjectGuid() + "&email="+mail + "&pass=" + password;
        urlGetUploadedPhotos = LoginActivity.getServies(ctx)+ BASE_URL_LOAD+
                ProjectInfo.project.getProjectGuid() + "&email="+mail + "&pass=" + password;
    }
    private void getLoginParams(Context ctx){
        mail = PreferenceManager.getDefaultSharedPreferences(ctx).getString(LoginActivity.EMAIL_KEY,"");
        password = PreferenceManager.getDefaultSharedPreferences(ctx).getString(LoginActivity.PASSWORD_KEY,"");
    }

    public void createDialog(){
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getResources().getString(R.string.dialog_upload));
            progressDialog.setMessage(context.getResources().getString(R.string.dialog_upload_mess));
            // Change style
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // switch-on animation
            progressDialog.setMax(dir.listFiles().length);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public void freeDialog(){
        try{
            if ((progressDialog!= null)&&(progressDialog.isShowing())){
                progressDialog.dismiss();
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            progressDialog = null;
        }
    }
    
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        createDialog();
    }
    
    @Override
    protected Integer doInBackground(Void... params) {
        int result = 0;
        try {
            File tempFile;
            int uploadedCount = 0;
            FileInputStream fileInputStream;
            HttpURLConnection connection;
            String JSONString = "";
            serverUri = new URL(urlGetUploadedPhotos);
            connection = (HttpURLConnection)serverUri.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                JSONString = streamToString(stream);
            }

            serverUri = new URL(uploadServerUri);
            connection = (HttpURLConnection)serverUri.openConnection();
            connection.setDoInput(true); // Allow Inputs
            connection.setDoOutput(true); // Allow Outputs
            connection.setUseCaches(false); // Don't use a Cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            dos = new DataOutputStream(connection.getOutputStream());
            for(int i= 0 ; i< dir.listFiles().length; i++)
            {
                tempFile = new File(dir.listFiles()[i].getAbsolutePath());
                if(! JSONString.contains(tempFile.getName())) {
                    fileInputStream = new FileInputStream(tempFile);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    String par = "Content-Disposition: form-data; name=" + '"'
                            + "uploaded_file[]" + '"' + ";filename=" + '"' + ""
                            + tempFile.getAbsolutePath() + '"' + lineEnd;
                    dos.writeBytes(par);
                    par = "Content-Type:multipart/form-data" + lineEnd;
                    dos.writeBytes(par);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    fileInputStream.close();
                    dos.flush();

                    publishProgress(1);
                    uploadedCount++;
                }
            }
            if(uploadedCount > 0) {
                // Responses from the server (code and message)
                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                if (serverResponseCode != 200) {
                    return 1;
                } else {
                    InputStream stream = connection.getInputStream();
                    JSONString = streamToString(stream);
                    if (JSONString != null) {

                    }
                }
                if (!serverResponseMessage.equals("OK")) {
                    return 1;
                }
            }else{
                result = 2;
            }
            dos.close();
            connection.disconnect();
        }catch(MalformedURLException e){
            e.printStackTrace();
            return 1;
        }catch(IOException e){
            e.printStackTrace();
            return 1;
        }
        return result;
    }

    private String streamToString(InputStream stream){
        String streamString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String ch;
        try {
            while ((ch = reader.readLine()) != null ) {
                streamString += ch + "\n";
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return streamString;
    }

    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        progressDialog.incrementProgressBy(values[0]);
    }
    
    @Override
    protected void onPostExecute(Integer result){
        freeDialog();
        InfoCommonDialog dlg = new InfoCommonDialog();
        if (result == 1){
            String caption = context.getResources().getString(R.string.error_upload_photos);
            dlg.setMessage(caption);
        }
        else if(result == 0){
            String caption = context.getResources().getString(R.string.message_success_upload);
            dlg.setMessage(caption);
        }
        else if (result == 2){
            String caption = context.getResources().getString(R.string.message_nothing_to_upload);
            dlg.setMessage(caption);
        }
        dlg.show(((AppCompatActivity)context).getSupportFragmentManager(),"showResultdialog");
        super.onPostExecute(result);
    }
}
