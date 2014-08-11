package eu.codlab.cyphersend.dbms.config.controller;

import android.content.Context;
import android.database.Cursor;

import eu.codlab.cyphersend.dbms.config.internal.SGBD;
import eu.codlab.cyphersend.dbms.config.model.Config;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ConfigController {
    public static String PUBLIC_KEY = "public_key";
    public static String PRIVATE_KEY = "private_key";

    private static ConfigController _instance;

    private Context _context;
    private SGBD _sgbd;

    private ConfigController(Context context) {
        _context = context;
        _sgbd = new SGBD(context);
        _sgbd.open();
    }

    private static ConfigController createNewInstance(Context context){
        return new ConfigController(context);
    }

    public static ConfigController getInstance(Context context) {
        if(_instance == null) _instance = createNewInstance(context);
        return _instance;
    }
    public Cursor getConfigs() {
        return _sgbd.getConfigs();
    }


    public ConfigController setConfig(String title, String content){
        setConfig(new Config(0, title, content));
        return this;
    }

    public long setConfig(Config config){
        try{
            _sgbd.open();
        }catch(Exception e){

        }
        Config conf = getConfig(config.getTitle());
        if(conf != null){
            conf.setContent(config.getContent());
            return _sgbd.updateConfig(conf.getId(), conf.getContent());
        }else{
            return _sgbd.addConfig(config.getTitle(), config.getContent());
        }
    }

    public Config getConfig(String title) {
        try{
            _sgbd.open();
        }catch(Exception e){

        }
        return _sgbd.getConfigFrom(title);
    }

    public void deleteConfig(Config config){
       _sgbd.deleteConfig(config.getId());
    }

    public void updateConfig(Config config) {
        _sgbd.updateConfig(config.getId(), config.getContent());
    }

    public void close() {
        _sgbd.close();
    }
}
