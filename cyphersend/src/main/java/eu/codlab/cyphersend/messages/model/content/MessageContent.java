package eu.codlab.cyphersend.messages.model.content;

import android.os.Message;

import org.json.JSONObject;

/**
 * Created by kevinleperf on 01/04/2014.
 */
public abstract class MessageContent {
    protected static int IMAGE = 1;
    protected static int STRING = 0;
    protected boolean _incognito;

    public abstract void fromJSON(JSONObject object);
    public abstract JSONObject toJSON();

    private MessageContent(){

    }
    protected MessageContent(boolean incognito){
        _incognito = incognito;
    }

    public boolean isIncognito(){
        return _incognito;
    }

    public static MessageContent getMessageFromJSON(JSONObject object){
        if(object.has("type")){
            try{
                if(object.getInt("type") == IMAGE){

                }else if(object.getInt("type") == STRING){
                    MessageString msg = new MessageString(true);
                    msg.fromJSON(object);
                    return msg;
                }
            }catch(Exception e){

            }
        }
        return null;
    }
}
