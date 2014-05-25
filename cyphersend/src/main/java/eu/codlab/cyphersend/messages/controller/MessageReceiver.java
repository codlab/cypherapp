package eu.codlab.cyphersend.messages.controller;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import eu.codlab.cyphersend.messages.listeners.MessageReceiveListener;
import eu.codlab.cyphersend.messages.model.MessageRead;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MessageReceiver{
    private String _url;
    private String _identifier;
    private String _pass;
    private MessageReceiveListener _listener;
    public void setMessageReceiveListener(MessageReceiveListener listener){
        _listener = listener;
    }


    public MessageReceiver(MessageReceiveListener listener, String url, String identifier, String pass){
        _url = url;
        _identifier = identifier;
        _pass = pass;
        setMessageReceiveListener(listener);
    }

    private Receiver _receiver;

    private Receiver getReceiver(){
        if(_receiver == null)_receiver = new Receiver();
        return _receiver;
    }


    private String createUriString(String web){
        String website = web;
        if(!website.endsWith("/")){
            website += "/";
        }
        website+="service/list/"+_identifier+"/"+_pass;

        return website;

    }

    private class Receiver extends AsyncTask<String, Double, ArrayList<MessageRead> > {

        @Override
        protected ArrayList<MessageRead> doInBackground(String... website) {
            ArrayList<MessageRead> messages = new ArrayList<MessageRead>();

            URL url = null;
            try {
                url = new URL(createUriString(website[0]));
                URLConnection urlConnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                StringBuilder sb=new StringBuilder();
                InputStreamReader is = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();

                while(read != null) {
                    //System.out.println(read);
                    sb.append(read);
                    read =br.readLine();

                }

                JSONArray array = new JSONArray(sb.toString());

                if(array.length() > 0){
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.optJSONObject(i);
                        if(object != null && object.has("message") && object.has("sender") && object.has("receiver")){
                            MessageRead message = new MessageRead(object.getString("sender"),
                                    object.getString("receiver"), object.getString("message"));
                            messages.add(message);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
            return messages;
        }

        @Override
        public void onPostExecute(ArrayList<MessageRead>  result){
            if(_listener != null){
                _listener.onPostExecute();
                if(result == null){
                    _listener.onReceiveError();
                }else if(result.size() == 0){
                    _listener.onEmpty();
                }else{
                    MessageReceiveListener tmp = _listener;
                    for(int i=0;i<result.size();i++){
                        _listener.onMessageReceived(result.get(i));
                    }
                }
            }
        }
    }

    public void retrieve(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
            getReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
        }
        else {
            getReceiver().execute(_url);
        }
    }


}
