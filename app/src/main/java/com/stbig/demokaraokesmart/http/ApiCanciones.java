package com.stbig.demokaraokesmart.http;

import android.content.Context;
import android.util.Log;

import com.stbig.demokaraokesmart.R;
import com.stbig.demokaraokesmart.datos.Datos;
import com.stbig.demokaraokesmart.helper.DBitem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 23/11/15.
 */
public class ApiCanciones {
    private Context context;
    private HttpConnectionWeb cn;
    private Datos data;
    private JSONObject jObj;

    public ApiCanciones(Context context){
        this.context=context;
        cn = new HttpConnectionWeb(context.getString(R.string.url));
        data = new Datos(context);
    }


    public boolean requestCanciones(){


        try {

            String json = cn.connect();

            if(!json.isEmpty()){

                jObj = new JSONObject(json);

                JSONArray d = jObj.getJSONArray("data");

                if(registerRadio(d))
                    return true;

            }

            return false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public boolean registerRadio(JSONArray radios){

        try{
            for(byte x=0;x<radios.length() -1 ;x++){

                JSONObject node = radios.getJSONObject(x);

                data.registerCancion(node.getInt(DBitem.CANCION_ID),
                        node.getString(DBitem.CANCION_NAME),
                        node.getString(DBitem.CANCION_DURATION),
                        node.getInt(DBitem.CANCION_INITIAL),
                        node.getString(DBitem.CANCION_MP4),
                        node.getString(DBitem.CANCION_VIDEO),
                        node.getString(DBitem.CANCION_SOUND),
                        node.getString(DBitem.CANCION_ARTIST));

            }

            return true;
        }catch(JSONException e){
            return false;
        }

    }




}
