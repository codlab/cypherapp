package eu.codlab.cyphersend.messages.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;

import eu.codlab.cyphersend.messages.model.content.MessageContent;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MessageRead extends Message{
    protected PrivateKey _private_key;
    protected String _b64_sender_identifier;
    protected String _b64_receiver_identifier;


    public MessageRead(String b64_sender_identifier, String b64_receiver_identifier, String message, boolean incognito){
        setSenderIdentifier(b64_sender_identifier);
        setReceiverIdentifier(b64_receiver_identifier);
        setMessage(message, incognito);
    }

    private void setPrivateKey(PrivateKey key){
        _private_key = key;
    }

    public PrivateKey getPrivateKey(){
        return _private_key;
    }

    private void setSenderIdentifier(String b64_sender_identifier){
        _b64_sender_identifier = b64_sender_identifier;
    }

    public String getSenderIdentifier(){
        return _b64_sender_identifier;
    }

    private void setReceiverIdentifier(String b64_receiver_identifier){
        _b64_receiver_identifier = b64_receiver_identifier;
    }

    public String getReceiverIdentifier(){
        return _b64_receiver_identifier;
    }

    public MessageContent decode(PrivateKey key){
        try {
            String result = Base64Coder.decodeString(CypherRSA.decrypt(Base64Coder.decode(getMessage()), key).replaceAll("\0", ""));

            return MessageContent.getMessageFromJSON(new JSONObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
