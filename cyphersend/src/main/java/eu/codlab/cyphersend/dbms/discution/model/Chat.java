package eu.codlab.cyphersend.dbms.discution.model;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class Chat {
    private long _id;
    private String _content;
    private boolean _sender;

    public Chat(long id, String content, boolean sender){
        _id=id;
        _content = content;
        _sender = sender;
    }

    public long getId(){
        return _id;
    }

    public boolean isSender(){
        return _sender;
    }

    public String getContent(){
        return _content != null ?  "" : _content;
    }
    @Override
    public boolean equals(Object object){
        if(object == null){
            return false;
        }else{
            if(object instanceof Chat){
                return _content != null && _content.equals(((Chat) object).getContent());//_name.equals(((Device) object).getName());
            }else if(object instanceof String){
                return _content != null && _content.equals((String) object);//_name.equals((String)object);
            }
        }
        return false;
    }
}
