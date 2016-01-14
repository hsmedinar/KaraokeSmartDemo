package com.stbig.demokaraokesmart.datos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbig.demokaraokesmart.R;
import com.stbig.demokaraokesmart.objects.Cancion;

import java.util.ArrayList;

/**
 * Created by root on 29/12/15.
 */
public class AdapterCanciones extends BaseAdapter {

    private Context context;
    public ArrayList<Cancion> canciones;

    public AdapterCanciones(Context context, ArrayList<Cancion> canciones){
        this.context=context;
        this.canciones=canciones;
    }

    @Override
    public int getCount() {
        return canciones.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Cancion c = (Cancion) canciones.get(position);

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cancion, parent, false);
            holder = new ViewHolder();

            assert convertView != null;

            holder.nombre = (TextView) convertView.findViewById(R.id.nombre);
            holder.artista = (TextView) convertView.findViewById(R.id.artista);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nombre.setText(c.getName());
        holder.artista.setText(c.getArtist());

        return convertView;
    }

    static class ViewHolder {
        TextView nombre;
        TextView artista;
    }
}
