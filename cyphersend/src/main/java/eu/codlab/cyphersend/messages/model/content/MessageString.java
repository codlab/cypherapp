package eu.codlab.cyphersend.messages.model.content;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by kevinleperf on 01/04/2014.
 */
public class MessageString extends MessageContent {
    private String _message;

    public MessageString(boolean incognito){
        super(incognito);
    }

    public MessageString(String message, boolean incognito){
        super(incognito);
        _message = message;
        _incognito = incognito;
    }

    public String getMessage(){
        return _message;
    }

    @Override
    public void fromJSON(JSONObject object) {
        if(object.has("msg")){
            try{
                _message = object.getString("msg");
                _incognito = object.optBoolean("incognito", true);
            }catch(Exception e){

            }
        }
    }


    @Override
    public JSONObject toJSON() {
        try{
            JSONObject obj = new JSONObject();
            obj.put("type",MessageString.STRING);
            obj.put("incognito", _incognito);
            obj.put("msg", _message != null ? _message : "");

            return obj;
        }catch(Exception e){

        }
        return null;
    }
}
