package eu.codlab.cyphersend.dbms.model;

import android.util.Log;

import java.security.PublicKey;

import eu.codlab.cyphersend.security.CypherRSA;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class Device {
    private long _id;
    private String _name;
    private final String _identifier;
    private String _publickey;
    private String _website;

    public Device(long id, String name, String identifier, String publickey, String website){
        _id=id;
        _name=name;
        _identifier = identifier;
        _publickey=publickey;
        _website=website;
    }

    public long getId(){
        return _id;
    }

    public String getIdentifier(){
        return _identifier;
    }
    public String getName(){
        return _name;
    }

    public String getPublic(){
        return _publickey;
    }

    public PublicKey getPublicKey(){
        return CypherRSA.decodePublicKey(_publickey);
    }

    public String getWebsite(){
        return _website;
    }

    @Override
    public boolean equals(Object object){
        if(object == null){
            return false;
        }else{
            if(object instanceof Device){
                Log.d("equals?", "! "+_name + " " + ((Device) object).getName());
                return _name != null && _name.equals(((Device) object).getName());
            }else if(object instanceof String){
                Log.d("equals?", "> "+_name + " " + ((String) object));
                return _name != null && _name.equals((String)object);
            }
        }
        return false;
    }
}
