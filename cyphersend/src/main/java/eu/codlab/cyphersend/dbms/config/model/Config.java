package eu.codlab.cyphersend.dbms.config.model;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class Config {
    private long _id;
    private String _title;
    private String _content;

    public Config(long id, String title, String content){
        _id=id;
        _title = title;
        _content = content;
    }

    public long getId(){
        return _id;
    }

    public String getTitle(){
        return _title;
    }

    public String getContent(){
        return _content != null ? _content : "";
    }
    @Override
    public boolean equals(Object object){
        if(object == null){
            return false;
        }else{
            if(object instanceof Config){
                return _title != null && _title.equals(((Config) object).getTitle());
            }else if(object instanceof String){
                return _title != null && _title.equals((String) object);
            }
        }
        return false;
    }

    public void setContent(String content) {
        _content = content;
    }

    public boolean isContentSet() {
        return _content != null && _content.length() > 0;
    }
}
