package es.vallesoft.valletpv.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JSON {

    public void serializar(String file, JSONObject obj, Context context){
        try{
            OutputStreamWriter fos = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            fos.write(obj.toString());
            fos.close();
        }catch(Exception e){
            Log.e("JSON", e.getMessage());
        }
    }

    public JSONObject deserializar(String file, Context context) throws JSONException {
        String strJSON = "";

        try{
            BufferedReader fos = new BufferedReader(new InputStreamReader(context.openFileInput(file)));
            String tmp = fos.readLine();
            while (tmp!=null) {
                strJSON += tmp;
                tmp = fos.readLine();
            }
            fos.close();
        }catch(Exception e){
            Log.e("JSON", e.getMessage());
        }

        if(strJSON== "") return null;
        else {
            return new JSONObject(strJSON);
        }

    }
}
