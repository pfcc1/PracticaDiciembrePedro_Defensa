package com.example.practicadiciembrepedro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ActiviyDatosAcceso extends AppCompatActivity {

    Button buttonAcceso;
    ImageView imageView;
    EditText editTextContraseñaAcceso;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activiy_datos_acceso);

       // ServicioIntensoAlarmaPantalla.encolarTrabajo(ActiviyDatosAcceso.this,new Intent());

        buttonAcceso=findViewById(R.id.buttonAcceso);
        imageView=findViewById(R.id.imageViewAcceso);
        editTextContraseñaAcceso=findViewById(R.id.editTextTextContraseñaAcceso);
        sharedPreferences=getSharedPreferences(MainActivity.NOMBRE_FICHERO,MODE_PRIVATE);

        String imagenCamara=sharedPreferences.getString(MainActivity.RUTA_IMAGEN_CAMARA,null);
        String imagenGaleria=sharedPreferences.getString(MainActivity.RUTA_IMAGEN_GALERIA,null);

        if(imagenCamara!=null){
            imageView.setImageURI(Uri.parse(imagenCamara));
        }

        if(imagenGaleria!=null){
            imageView.setImageURI(Uri.parse(imagenGaleria));
        }

        buttonAcceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contraseñaAcceso=sharedPreferences.getString(MainActivity.CONTRASENA_ACCESO,null);
                int estadoInicio=sharedPreferences.getInt(MainActivity.ESTADO_INICIO,-1);
                System.out.println("Contraseña INTRODUCIDA1: "+contraseñaAcceso);
                System.out.println("CONTRASEÑA INTRODUCIDA2: "+editTextContraseñaAcceso.getText().toString());

                if (!editTextContraseñaAcceso.getText().toString().equals(contraseñaAcceso)){
                    Toast.makeText(getApplicationContext(),"Contraseña Incorrecta",Toast.LENGTH_SHORT).show();
                }else if(editTextContraseñaAcceso.getText().toString().equals(contraseñaAcceso)){


                        Intent intentMenu = new Intent(getApplicationContext(),Menu.class);
                        startActivity(intentMenu);


                }
            }
        });
    }



   /* @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent intent = new Intent(this, ActiviyDatosAcceso.class);
        startActivity(intent);

    }

    */
}