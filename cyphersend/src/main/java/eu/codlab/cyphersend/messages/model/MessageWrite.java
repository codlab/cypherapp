package eu.codlab.cyphersend.messages.model;

import java.security.PrivateKey;
import java.security.PublicKey;

import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.utils.SHA;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MessageWrite extends Message{
    protected PublicKey _public_key;
    protected String _b64_sender_identifier;
    protected String _b64_receiver_identifier;


    public MessageWrite(PublicKey public_key, PrivateKey personal, String b64_receiver_identifier, String message){
        setPublicKey(public_key);
        setMessage(message);
        setSenderIdentifier(new String(Base64Coder.encode(CypherRSA.encrypt(SHA.encode(_message), personal))));
        setReceiverIdentifier(b64_receiver_identifier);
    }

    private void setPublicKey(PublicKey key){
        _public_key = key;
    }

    public PublicKey getPublicKey(){
        return _public_key;
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

    public String encode(){
        return new String(Base64Coder.encode(CypherRSA.encrypt(Base64Coder.encodeString(getMessage()), getPublicKey())));
    }
}
