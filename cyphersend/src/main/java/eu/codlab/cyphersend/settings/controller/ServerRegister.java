package eu.codlab.cyphersend.settings.controller;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import eu.codlab.cyphersend.settings.listener.ServerRegisterListener;

public class ServerRegister {
    private String _url;
    private String _identifier;
    private String _pass;

    private ServerRegisterListener _listener;

    public void setServerRegisterListener(ServerRegisterListener listener) {
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

                JSONObject object = new JSONObject(sb.toString());

                return object != null && object.has("output") && "ok".equals(object.optString("output",null));
            } catch (Exception e) {
                e.printStackTrace();
                return null;

            }
        }

        @Override
        public void onPostExecute(Boolean state) {
            if (_listener != null) {
                if (state == null) {
                    _listener.onRegisterTimeout();
                } else if (state.booleanValue()) {
                    _listener.onRegisterOk();
                } else {
                    _listener.onRegisterKo();
                }
            }
        }
    }

    public ServerRegister(ServerRegisterListener listener, String url, String identifier, String pass) {
        _url = url;
        _identifier = identifier;
        _pass = pass;
        setServerRegisterListener(listener);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void send() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
        } else {
            getReceiver().execute(_url);
        }
    }
}
