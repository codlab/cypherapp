package eu.codlab.cyphersend.messages.model;

import java.security.PrivateKey;
import java.security.PublicKey;

import eu.codlab.cyphersend.messages.model.content.MessageString;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.utils.MD5;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MessageWrite extends Message{
    protected PublicKey _public_key;
    protected PrivateKey _private_key;

    protected String _b64_receiver_identifier;

    protected String _signature;
    protected String _encoded;


    public MessageWrite(PublicKey public_key, PrivateKey my_private, String b64_receiver_identifier){
        setPrivateKey(my_private);
        setPublicKey(public_key);
        setReceiverIdentifier(b64_receiver_identifier);
    }

    private void setPrivateKey(PrivateKey key){
        _private_key = key;
    }

    private void setPublicKey(PublicKey key){
        _public_key = key;
    }

    public PublicKey getPublicKey(){
        return _public_key;
    }

    public String getSenderIdentifier(){
        return _signature;
    }

    private void setReceiverIdentifier(String b64_receiver_identifier){
        _b64_receiver_identifier = b64_receiver_identifier;
    }

    public String getReceiverIdentifier(){
        return _b64_receiver_identifier;
    }

    public void encodeMessage(String message){
        MessageString msg = new MessageString(message);
        _signature = new String(Base64Coder.encode(CypherRSA.encrypt(MD5.encode(message), _private_key)));
        _encoded = new String(Base64Coder.encode(CypherRSA.encrypt(Base64Coder.encodeString(msg.toJSON().toString()), getPublicKey())));
    }

    public  String getEncodedMessage(){
        return _encoded;
    }
}
