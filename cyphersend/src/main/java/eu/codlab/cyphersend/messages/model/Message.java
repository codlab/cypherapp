package eu.codlab.cyphersend.messages.model;

/**
 * Created by kevinleperf on 29/06/13.
 */
abstract class Message {
    protected String _message;

    protected void setMessage(String message){
        _message = message;
    }

    public String getMessage(){
        return _message;
    }

    public static enum Type{
        STRING,
        IMAGE,
        FILE
    }
}
