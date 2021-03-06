package eu.codlab.cyphersend.dbms.devices.controller;

import android.content.Context;

import java.util.ArrayList;

import eu.codlab.cyphersend.dbms.devices.internal.SGBD;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.utils.MD5;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class DevicesController {
    private Context _context;
    private SGBD _sgbd;

    private ArrayList<Device> _devices;

    private DevicesController(Context context) {
        _context = context;
        _sgbd = new SGBD(context);
        _sgbd.open();

        _devices = new ArrayList<Device>();

        Device[] interfaces = _sgbd.getInterfaces();
        if (interfaces != null)
            for (int i = 0; i < interfaces.length; i++) {
                _devices.add(interfaces[i]);
            }
    }

    private static DevicesController _instance;
    public static DevicesController getInstance(Context context){
        if(_instance == null)_instance = new DevicesController(context);
        return _instance;
    }

    public ArrayList<Device> getDevices() {
        return _devices;
    }


    public boolean hasDevice(String identifier) {
        Device _if = new Device(new Long(0), "", identifier, "", "");

        return _devices.contains(_if);
    }

    /**
     *
     * @param signature the signature result of the rsa(b64(md5(coded message)));
     * @param decoded_message the decoded_message obtained
     * @return
     */
    public Device getDeviceFromSignature(String signature, String decoded_message){
        String decoded_message_hash = MD5.encode(decoded_message);

        ArrayList<Device> devices = getDevices();
        Device device = null;

        if(devices != null){
            for(Device dev : devices){
                try {
                    String hash = CypherRSA.decrypt(Base64Coder.decode(signature), dev.getPublicKey()).replaceAll("\0", "");
                    if (hash.equals(decoded_message_hash)) {
                        device = dev;
                    }
                }catch(Exception e){
                    //this was not the good public key...
                }
            }
        }
        return device;
    }

    public Device addDevice(String name, String identifier, String publickey, String address) {
        if (hasDevice(name)) {
            return _devices.get(_devices.indexOf(name));
        }

        Device inter = new Device(_sgbd.addDevice(name,identifier, publickey, address),name, identifier,publickey, address);
        _devices.add(inter);
        return inter;
    }

    public Device getDevice(String identifier) {
        if (hasDevice(identifier)) {
            Device _if = new Device(new Long(0), "", identifier, "", "");
            return _devices.get(_devices.indexOf(_if));
        }
        return null;
    }
    public boolean hasWebDevices() {
        boolean has = false;
        ArrayList<Device> devices = getDevices();
        for(int i=0;i<devices.size();i++){
            if(devices.get(i).hasWebSite()){
                has = true;
                break;
            }
        }
        return has;
    }

    public boolean hasDevices(){
        return getDevices().size() > 0;
    }

    public boolean updateDeviceUrl(Device device, String device_url) {
        if(device_url == null)return false;
        if( (device.getWebsite() == null && device_url != null) ||
                (device.getWebsite() != null && device.getWebsite().equals(device_url) == false)){
            _sgbd.updateDeviceUrl(device.getId(), device_url);
            device.setWebsite(device_url);
            return true;
        }
        return false;
    }

    public boolean updateDeviceName(Device device, String device_name) {
        if(device_name == null)return false;
        if( (device.getName() == null && device_name != null) ||
            (device.getName() != null && device.getName().equals(device_name) == false)){
            _sgbd.updateDeviceName(device.getId(), device_name);
            device.setName(device_name);
            return true;
        }
        return false;
    }
}
