package com.stbig.demokaraokesmart.http;

import android.graphics.Bitmap;
import android.util.Log;


import com.stbig.demokaraokesmart.helper.Files;
import com.stbig.demokaraokesmart.helper.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by helbert on 16/05/15.
 */
public class HttpConnectionWeb {

    private HttpURLConnection conexion;

    private URL url;
    private String TAG_HTTP = "HttpDebug";
    private String link;
    private HashMap<String, String> params;

    public HttpConnectionWeb(String link){
        this.link=link;

        params = new HashMap<>();
    }


    public String connect() throws IOException {

        String linea;

        StringBuilder construye = new StringBuilder();

        try{
            this.url= new URL(link + getPostDataString(params));
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
           // conexion.setDoInput(true);
           // conexion.setDoOutput(true);

           // Log.i(TAG_HTTP, "parametros " + getPostDataString(params));

           // PrintWriter printer = new PrintWriter(conexion.getOutputStream());
           // printer.print(getPostDataString(params));
           // printer.close();

            InputStreamReader input = new InputStreamReader(conexion.getInputStream());
            BufferedReader buffer = new BufferedReader(input);

          //  Log.i(TAG_HTTP, String.valueOf(conexion.getResponseCode()));

            if(conexion.getResponseCode()==HttpURLConnection.HTTP_OK){

                while((linea=buffer.readLine()) !=null){
                    construye.append(linea);
                }

               // Log.i(TAG_HTTP, " res " + construye.toString());

                return construye.toString();

            }

            return "";

        }catch(Exception e){
            Log.e(TAG_HTTP, e.getMessage());
            return "";
        }

    }

    public void AddParam(String name, String value) {
        params.put(name, value);
    }

    private String getPostDataString(HashMap<String, String> paramsPost) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : paramsPost.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public boolean mp3Download() {
        try {

            String characters[] = link.split("/");
            String nameMp3 = characters[characters.length-1];

            Files fileDir = new Files();
            fileDir.setNameFiles(nameMp3);


            File f = new File (fileDir.getPathService());
            if (f.exists ()) return true;


            URL mp3Url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)mp3Url.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            return true;
        } catch (Throwable ex){

            Log.e("error download", ex.getMessage());
            return false;
        }


    }

}

