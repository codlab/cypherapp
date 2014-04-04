package eu.codlab.cyphersend.utils.keys;

import android.content.Context;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;

/**
 * Created by kevinleperf on 03/04/2014.
 */
public class KeyManager {
    private Context _context;
    private PublicKey _public_key;
    private PrivateKey _private_key;
    private String _identifier;
    private String _pass;
    private String _website;
    private String _name;

    public KeyManager(Context context){
        _context = context;
    }

    public boolean exportKeys(PublicKey publicKey, PrivateKey privateKey, String name,
                              String identifier, String pass, String website, String password) {
        try{

            String p = new String(Base64Coder.encode(CypherRSA.exportPublicKey(publicKey)));
            String P = new String(Base64Coder.encode(CypherRSA.exportPrivateKey(privateKey)));


            File outKeys = new File("/data/data/"+_context.getPackageName()+"/keys");///
            File outZip = new File("/data/data/"+_context.getPackageName()+"/export.zip");
            File outFile = new File("/sdcard/export.zip");

            if(outZip.exists())outZip.delete();

            if(!outKeys.exists())outKeys.createNewFile();

            FileOutputStream in = new FileOutputStream(outKeys);
            OutputStreamWriter osw = new OutputStreamWriter(in);
            osw.write(Base64Coder.encodeString(name)+";"+Base64Coder.encodeString(identifier)+";"+
                    Base64Coder.encodeString(pass)+";"+
                    Base64Coder.encodeString(website)+";"+
                    p+";"+P);
            osw.close();
            in.close();

            ZipFile zip = new ZipFile(outZip);
            ZipParameters params = new ZipParameters();
            params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            params.setEncryptFiles(true);
            params.setPassword(password);
            zip.createZipFile(outKeys,params);

            copyAndDelete(outZip, outFile);

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean importKeys(String password){
        try {
            File outZip = new File("/data/data/"+_context.getPackageName()+"/export.zip");
            File f = new File("/sdcard/export.zip");
            if(f.exists()){

                copyAndDelete(f, outZip);

                ZipFile zipFile = new ZipFile(outZip);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password);
                }
                InputStream read_keys = zipFile.getInputStream(zipFile.getFileHeader("keys"));
                BufferedReader read_keys_1 = new BufferedReader(new InputStreamReader(read_keys));
                StringBuffer lines = new StringBuffer(2048);
                String line = "";
                while( (line = read_keys_1.readLine()) != null){
                    lines.append(line);
                }
                try{
                    read_keys_1.close();
                    read_keys.close();
                }catch(Exception ee){}

                outZip.delete();
                Log.d("MainActivity", lines.toString());


                String [] split = lines.toString().split(";");
                if(split.length == 6){
                    _name = Base64Coder.decodeString(split[0]);
                    _identifier = Base64Coder.decodeString(split[1]);
                    _pass = Base64Coder.decodeString(split[2]);
                    _website = Base64Coder.decodeString(split[3]);
                    _public_key = CypherRSA.importPublicKey(Base64Coder.decode(split[4]));
                    _private_key = CypherRSA.importPrivateKey(Base64Coder.decode(split[5]));
                    return true;
                }else{
                    return false;
                }

            }else{
                Log.d("MainActivity","does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PublicKey getPublicKey(){
        return _public_key;
    }

    public PrivateKey getPrivateKey(){
        return _private_key;
    }

    public String getIdentifier(){
        return _identifier;
    }

    public String getPass(){
        return _pass;
    }

    public String getWebsite(){
        return _website;
    }

    public String getName(){
        return _name;
    }


    private void copyAndDelete(File input, File output) throws IOException {

        if(output.exists())output.delete();
        output.createNewFile();

        FileInputStream inEnd = new FileInputStream(input);
        FileOutputStream outEnd = new FileOutputStream(output);

        byte[] moveBuff = new byte[1024];

        int butesRead;
        while ((butesRead = inEnd.read(moveBuff)) > 0) {
            outEnd.write(moveBuff, 0, butesRead);
        }
        inEnd.close();
        outEnd.close();

        input.delete();
    }


}
