package com.example.practicadiciembrepedro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import static com.example.practicadiciembrepedro.MainActivity.NOMBRE_FICHERO;
import static com.example.practicadiciembrepedro.MainActivity.RUTA_IMAGEN_CAMARA;
import static com.example.practicadiciembrepedro.MainActivity.RUTA_IMAGEN_GALERIA;

public class ActiviyDatosAcceso extends AppCompatActivity {

    private static final String ID_CANAL ="1";
    Button buttonAcceso;
    ImageView imageViewAcceso;
    EditText editTextContraseñaAcceso;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activiy_datos_acceso);

       // ServicioIntensoAlarmaPantalla.encolarTrabajo(ActiviyDatosAcceso.this,new Intent());

        MostrarNotificacionAlAbrirlaAplicacion();

        buttonAcceso=findViewById(R.id.buttonAcceso);
        imageViewAcceso=findViewById(R.id.imageViewAcceso);
        editTextContraseñaAcceso=findViewById(R.id.editTextTextContraseñaAcceso);
        sharedPreferences=getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);

        String imagenCamara=sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null);
        String imagenGaleria=sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null);

        System.out.println("IMAGEN GALERIA: "+imagenGaleria);
        if(imagenCamara!=null){

            imageViewAcceso.setImageURI(Uri.parse(imagenCamara));
        }


        if(imagenGaleria!=null){
            imageViewAcceso.setImageURI(Uri.parse(imagenGaleria));
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

    private Bitmap getBitmapFromUri ( Uri uri ) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver (). openFileDescriptor ( uri , "r" );
        FileDescriptor fileDescriptor = parcelFileDescriptor . getFileDescriptor ();
        Bitmap image = BitmapFactory . decodeFileDescriptor ( fileDescriptor );
        parcelFileDescriptor . close ();
        return image ;
    }

    private void MostrarNotificacionAlAbrirlaAplicacion() {
        ManejadorBD manejadorBD=new ManejadorBD(this);

        int contadorRegistros=0;
        Cursor cursorListar=manejadorBD.listar();

        if(cursorListar!=null && cursorListar.getCount()>0){
            while(cursorListar.moveToNext()){
                contadorRegistros++;

            }
        }


        sharedPreferences=getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);
        String fotocamara=sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null);
        String fotogaleria=sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null);
        Uri uriIMAGEN = null;




        String idChannel="Notificaciones con Fotos";
        String nombreCanal="Notificaciones con FOtos";

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,ID_CANAL)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notificación de texto en varias lineas")
                .setAutoCancel(true)
                .setContentTitle("Texto inicial");

        NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle("Notificación Acceso");
        bigPictureStyle.setSummaryText("Registros Almacenados: "+contadorRegistros);


        if(fotocamara!=null){

            try {
                Uri urifoto = Uri.parse(fotocamara);
                Bitmap bitmap=getBitmapFromUri(urifoto);
                bigPictureStyle.bigPicture(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(fotogaleria!=null){

            try {
                Uri uriGaleria = Uri.parse(fotogaleria);
                Bitmap bitmap=getBitmapFromUri(uriGaleria);
                bigPictureStyle.bigPicture(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        builder.setStyle(bigPictureStyle);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(idChannel,nombreCanal,NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);//Habilitar Led
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);

            builder.setChannelId(idChannel);
            notificationManager.createNotificationChannel(notificationChannel);
        }else{
            builder.setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_LIGHTS);

        }
        notificationManager.notify(2,builder.build());


    }
}