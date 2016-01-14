package com.stbig.demokaraokesmart;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.stbig.demokaraokesmart.datos.AdapterCanciones;
import com.stbig.demokaraokesmart.datos.Datos;
import com.stbig.demokaraokesmart.http.ApiCanciones;
import com.stbig.demokaraokesmart.http.HttpConnectionWeb;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    AdapterCanciones adapter;
    ListView lista;
    Datos data;
    ProgressDialog progressDialog;
    private String cancion;
    private String name;
    private String video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista = (ListView) findViewById(R.id.listacanciones);

        data = new Datos(this);
        lista.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new AdapterCanciones(this,data.listarCanciones());
        lista.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadPista(adapter.canciones.get(position).getSound());
        cancion = adapter.canciones.get(position).getSound();
        name = adapter.canciones.get(position).getName();
        video = adapter.canciones.get(position).getMp4_source();
    }

    private void loadPista(final String urlmp3){
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpConnectionWeb con = new HttpConnectionWeb(urlmp3);
                boolean response = con.mp3Download();

                Message msg = new Message();
                msg.obj=response;
                responseWeb.sendMessage(msg);

            }
        }).start();

    }

    private Handler responseWeb = new Handler(){
        public void handleMessage(Message msg){

            if(Boolean.valueOf(msg.obj.toString())){
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("pista",cancion);
                intent.putExtra("video",video);
                intent.putExtra("name",name);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Fallo carga de pista", Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(MainActivity.this, "Fallo carga de pista",Toast.LENGTH_SHORT);
            }

            progressDialog.dismiss();
        };
    };

    private void showProgress(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Descargando pista...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
