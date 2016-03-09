package ua.com.expertsoft.android_smeta;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by mityai on 04.03.2016.
 */
public class LoadFromLAN extends AsyncTask<Void,Void,Integer> {

    public static final int GIVE_ME_BUILDS_PARAMS = 0;

    Socket sendSocket;
    String messValue, IP;
    int portValue;
    String message = "";
    ArrayList<String> ipArray;
    Context context;

    public LoadFromLAN(Context ctx, String message, String ip, int projectExpType){
        messValue = message;
        IP = ip;
        switch(projectExpType){
            case 1:
                portValue = 1149;
                break;
            case 2:
            case 3:
                portValue = 1150;
                break;
        }
        ipArray = new ArrayList<>();
        context = ctx;
    }

    public boolean isReachableByTcp(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // TODO Auto-generated method stub
        int whatReturn = 0;
        boolean network;
        try {
            try {
                network = isReachableByTcp(IP, portValue, 50);
                if (network) {
                    sendSocket = new Socket(IP, portValue);
                    byte[] mybytearray = messValue.getBytes();
                    OutputStream os = sendSocket.getOutputStream();
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();

                    while (true) {
                        InputStream is = sendSocket.getInputStream();
                        InputStreamReader reader = new InputStreamReader(is, "windows-1251");
                        char[] readerChar = new char[is.available()];
                        reader.read(readerChar,0,readerChar.length);
                        message = String.copyValueOf(readerChar);
                        String[] result = message.split("#");
                        if (result[0].equals("done")){
                            reader.close();
                            break;
                        }
                    }

                    sendSocket.close();

                    whatReturn = 1;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return whatReturn;
    }

    protected void onPostExecute(Integer result){
        super.onPostExecute(result);
        switch(result){
            case 0:
                Toast.makeText(context, "Соединение не установлено", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(context, "Отправлено и получено: " + message, Toast.LENGTH_LONG).show();
                Log.d("socketsWork", message);
                break;
        }
    }
}
