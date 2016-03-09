package ua.com.expertsoft.android_smeta.asynktasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 14.01.2016.
 */
public class UploadPhotoToServer extends AsyncTask<Void,Integer,Boolean>{
    
    ProgressDialog progressDialog;
    Context context;
    File dir;
    String uploadServerUri1 = "http://195.62.15.35:8084/OCAD/upload.php?proj_guid=";
    String uploadServerUri = "http://195.62.15.35:8084/OCAD/upload_multi.php?proj_guid=";
    URL serverUri;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    int serverResponseCode = 0;

    public UploadPhotoToServer(){}

    public UploadPhotoToServer(Context ctx, String filePaht){
        context = ctx;
        dir = new File(filePaht);
        uploadServerUri += "111-222";
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
            if ((progressDialog!= null)&(progressDialog.isShowing())){
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
    protected Boolean doInBackground(Void... params) {
        try {
            File tempFile;
            FileInputStream fileInputStream;
            serverUri = new URL(uploadServerUri);
            HttpURLConnection connection = (HttpURLConnection)serverUri.openConnection();
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
                fileInputStream = new FileInputStream(tempFile);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                String par ="Content-Disposition: form-data; name="+'"'
                        +"uploaded_file[]"+'"'+";filename="+'"'+""
                        + tempFile.getAbsolutePath() +'"' + lineEnd;
                dos.writeBytes(par);
                par ="Content-Type:multipart/form-data" + lineEnd;
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
            }
            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            if (serverResponseCode != 200){
                return false;
            }
            if(!serverResponseMessage.equals("OK")){
                return false;
            }
            dos.close();
            connection.disconnect();
        }catch(MalformedURLException e){
            e.printStackTrace();
            return false;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        progressDialog.incrementProgressBy(values[0]);
    }
    
    @Override
    protected void onPostExecute(Boolean result){
        freeDialog();
        if (!result){
            String caption = context.getResources().getString(R.string.error_upload_photos);
            Toast.makeText(context,caption,Toast.LENGTH_SHORT).show();
        }
        else{
            String caption = context.getResources().getString(R.string.message_success_upload);
            Toast.makeText(context,caption,Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}
