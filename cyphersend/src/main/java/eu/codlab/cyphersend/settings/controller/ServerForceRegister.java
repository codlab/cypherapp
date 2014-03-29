package eu.codlab.cyphersend.settings.controller;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageRead;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.settings.listener.ServerForceRegisterListener;

public class ServerForceRegister {
    private String _url;
    private String _identifier;
    private String _pass;

    private ServerForceRegisterListener _listener;

    public void setServerForceRegisterListener(ServerForceRegisterListener listener) {
        _listener = listener;
    }

    private Register _register;

    private Register getReceiver() {
        if (_register == null) _register = new Register();
        return _register;
    }

    private String createUriString(String web) {
        String website = web;
        if (!website.endsWith("/")) {
            website += "/";
        }
        website += "service/register/" + _identifier
                + "/" + _pass;

        Log.d("website", website);
        return website;

    }

    private class Register extends AsyncTask<String, Double, Boolean> {

        @Override
        protected Boolean doInBackground(String... website) {
            URL url = null;
            try {
                url = new URL(createUriString(website[0]));
                URLConnection urlConnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                StringBuilder sb = new StringBuilder();
                InputStreamReader is = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();

                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }

                Log.d("url received", sb.toString() + "");
                JSONObject object = new JSONObject(sb.toString());

                return object != null && object.has("output") && "ok".equals(object.optString("output",null));
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }

        @Override
        public void onPostExecute(Boolean state) {
            if (_listener != null) {
                if (state == null) {
                    _listener.onForceRegisterKo();
                } else if (state.booleanValue()) {
                    _listener.onForceRegisterOk();
                } else {
                    _listener.onForceRegisterKo();
                }
            }
        }
    }

    public ServerForceRegister(ServerForceRegisterListener listener, String url, String identifier, String pass) {
        _url = url;
        _identifier = identifier;
        _pass = pass;
        setServerForceRegisterListener(listener);
    }

    public void send() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
        } else {
            getReceiver().execute(_url);
        }
    }
}
