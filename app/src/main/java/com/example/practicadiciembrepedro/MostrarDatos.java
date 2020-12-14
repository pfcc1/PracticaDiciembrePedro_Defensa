package com.example.practicadiciembrepedro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MostrarDatos extends AppCompatActivity {
ManejadorBD manejadorBD;
ListView lista;
ArrayList<String> ubicacion=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_datos);

        lista=findViewById(R.id.lista);
        manejadorBD=new ManejadorBD(this);


        Cursor cursorListar=manejadorBD.listar();

        ArrayAdapter<String> adapter;

        List<String> list=new ArrayList<>();


        if(cursorListar!=null && cursorListar.getCount()>0){
            while(cursorListar.moveToNext()){
                String fila="";
                fila+=" ID: "+cursorListar.getString(0);
                fila+=" FECHA: "+cursorListar.getString(1);
                fila+=" HORA: "+cursorListar.getString(2);
                fila+=" BATERIA: "+cursorListar.getString(3);
                fila+=" LATITUD: "+cursorListar.getString(4);
                fila+=" LONGITUD: "+cursorListar.getString(5);
                list.add(fila);

                ubicacion.add(cursorListar.getString(4)+","+cursorListar.getString(5));
            }

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.es/maps/place/"+ubicacion.get(position)));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            });

            adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);

            lista.setAdapter(adapter);
            cursorListar.close();
        }else{
            Toast.makeText(this,"No hay registros",Toast.LENGTH_SHORT).show();
        }




    }
}