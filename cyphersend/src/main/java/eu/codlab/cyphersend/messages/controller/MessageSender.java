package eu.codlab.cyphersend.messages.controller;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;

public class MessageSender {
    private String _url;
    private MessageWrite _message;

    private MessageSenderListener _listener;

    public void setMessageSenderListener(MessageSenderListener listener) {
        _listener = listener;
    }

    private Receiver _receiver;

    private Receiver getReceiver() {
        if (_receiver == null) _receiver = new Receiver();
        return _receiver;
    }

    private String createUriString(String web){
        String website = web;
        if(!website.endsWith("/")){
            website += "/";
        }
        website+="service/add/"+_message.getSenderIdentifier()
                +"/"+_message.getReceiverIdentifier()+"/"+_message.encode();

        Log.d("website", website);
        return website;

    }
    private class Receiver extends AsyncTask<String, Double, Boolean> {

        @Override
        protected Boolean doInBackground(String... website) {
            URL url = null;
            try {
                url = new URL(createUriString(website[0]));
                URLConnection urlConnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                return Boolean.FALSE;

            }

            return Boolean.TRUE;
        }

        @Override
        public void onPostExecute(Boolean state) {
            if (_listener != null) {
                if (state == null) {
                    _listener.onSendError();
                } else if (state.booleanValue()) {
                    _listener.onOk();
                } else {
                    _listener.onSendError();
                }
            }
        }
    }

    public MessageSender(MessageSenderListener listener, String url, MessageWrite message) {
        _url = url;
        _message = message;
        setMessageSenderListener(listener);
    }

    public void send() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
        } else {
            getReceiver().execute(_url);
        }
    }
}
