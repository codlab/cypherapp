package eu.codlab.cyphersend.messages.model;

/**
 * Created by kevinleperf on 29/06/13.
 */
abstract class Message {
    protected String _message;
    protected boolean _incognito;

    protected void setMessage(String message, boolean incognito){
        _message = message;
        _incognito = incognito;
    }

    public String getMessage(){
        return _message;
    }

    public boolean isIncognito(){
        return _incognito;
    }


    public static enum Type{
        STRING,
        IMAGE,
        FILE
    }
}
