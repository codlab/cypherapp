package eu.codlab.cyphersend.messages.model.content;

import org.json.JSONObject;

/**
 * Created by kevinleperf on 01/04/2014.
 */
public class MessageString extends MessageContent {
    private String _message;

    public MessageString(){

    }

    public MessageString(String message){
        _message = message;
    }

    public String getMessage(){
        return _message;
    }

    @Override
    public void fromJSON(JSONObject object) {
        if(object.has("msg")){
            try{
                _message = object.getString("msg");
            }catch(Exception e){

            }
        }
    }

    @Override
    public JSONObject toJSON() {
        try{
            JSONObject obj = new JSONObject();
            obj.put("type",MessageString.STRING);
            obj.put("msg", _message != null ? _message : "");

            return obj;
        }catch(Exception e){

        }
        return null;
    }
}
