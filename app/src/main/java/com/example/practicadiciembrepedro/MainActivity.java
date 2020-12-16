package com.example.practicadiciembrepedro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

public class MainActivity extends AppCompatActivity {

    private static final int PEDI_PERMISO_DE_ESCRITURA = 1;
    private static final int VENGO_DE_LA_CAMARA_CON_FICHERO =2 ;
    private static final int FOTO_SELECCIONADA_GALERIA = 3;
    private static final int PEDI_PERMISO_DE_LECTURA = 4;
    public static final String NOMBRE_FICHERO = "NOMBRE";
    public static final String RUTA_IMAGEN_CAMARA = "IMAGENCAMARA";
    public static final String RUTA_IMAGEN_GALERIA="IMAGENGALERIA";
    public static final String ESTADO_INICIO ="ESTADOINICIO" ;
    public static final String CONTRASENA_ACCESO = "CONTRAEÑAACCESO";
    private static final String ID_CANAL ="ID_CANAL" ;
    Button buttonCamara,buttonGaleria,buttonAccesoMenu;
    ImageView imageView;
    EditText editTextContraseña;
    File fichero;
    Uri imagenURI;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Para que los ficheros que yo haga los pueda ver la cámara:


         StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
         StrictMode.setVmPolicy(builder.build());


        buttonCamara=findViewById(R.id.buttonCamara);
        buttonGaleria=findViewById(R.id.buttonGaleria);
        buttonAccesoMenu=findViewById(R.id.buttonAccesoMenu);
        imageView=findViewById(R.id.imageView);
        editTextContraseña=findViewById(R.id.editTextTextClave);


        //Obtengo los datos almacenados en el Fichero SharedPreferences
        sharedPreferences=getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);
        String fotocamara=sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null);
        String fotogaleria=sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null);


        switch (getEstadosInicioAplicacion()){
            case 0:


                break;

            case 1://Si es la Segunda vez que entro a la aplicacion



               String contrasena= sharedPreferences.getString(CONTRASENA_ACCESO,editTextContraseña.getText().toString());

                if(fotocamara!=null || fotogaleria!=null && contrasena.length()>=6){
                    finish();//Elimino la actividad MainActivity para
                    //que al darle al boton atras no se vaya a esta Actividad
                    // ServicioIntensoAlarmaPantalla.encolarTrabajo(this,new Intent());
                    //ServicioIntensoSeguimiento.encolarTrabajo(this,new Intent());

                    Intent intentAcceso = new Intent(this,ActiviyDatosAcceso.class);
                    startActivity(intentAcceso);
                }





                break;


            case 2:


                break;

        }

        System.out.println("FOTO CAMARA SHARE: "+fotocamara);
        System.out.println("FOTO GALERIA SHARE: "+fotogaleria);


        //Introduzco en el ImageView la imagen
        if(fotocamara!=null){
            fotogaleria=null;
          imageView.setImageURI(Uri.parse(fotocamara));
        }

        if(fotogaleria!=null){
            fotocamara=null;
         imageView.setImageURI(Uri.parse(fotogaleria));
        }



        buttonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermisoParafoto();
            }
        });

        buttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComprobarPermisoGaleria();

            }
        });

        buttonAccesoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences=getSharedPreferences(NOMBRE_FICHERO,MODE_PRIVATE);
                if(editTextContraseña.getText().toString()==null){
                    Toast.makeText(getApplicationContext(),"La contraseña no puede estar vacia",Toast.LENGTH_SHORT).show();
                }else if(editTextContraseña.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"La contraseña no puede tener menos de 6 carácteres",Toast.LENGTH_SHORT).show();
                }else if(sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null)==null &&
                        sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null)==null){
                        Toast.makeText(getApplicationContext(),"No has seleccionado ninguna foto",Toast.LENGTH_SHORT).show();
                }else if(sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null)==null &&
                        sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null)==null &&
                        editTextContraseña.getText().toString()==null){
                    Toast.makeText(getApplicationContext(),"No has introducido una contraseña y no has seleccionado una foto",Toast.LENGTH_SHORT).show();
                }else if(sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null)==null &&
                        sharedPreferences.getString(RUTA_IMAGEN_CAMARA,null)==null &&
                        editTextContraseña.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"No has seleccionado una foto y la contraseña es inferior a 6 carácteres",Toast.LENGTH_SHORT).show();
                }
                else{
                    GuardarContraseña();
                    Intent intentMenu=new Intent(getApplicationContext(),Menu.class);
                    startActivity(intentMenu);
                }
            }
        });




    }






    private void ComprobarPermisoGaleria() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE},PEDI_PERMISO_DE_LECTURA);

        }
        else{
            AccesoGaleria();
        }
    }

    private void AccesoGaleria(){
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,FOTO_SELECCIONADA_GALERIA);


    }

    private void pedirPermisoParafoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){ //No tengo permiso

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PEDI_PERMISO_DE_ESCRITURA);

        }else{
            hacerFotoAltaResolucion();
        }
    }

    private void hacerFotoAltaResolucion() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        try {
            fichero = crearFicheroDeFoto();
            System.out.println("Fichero RUTA: "+fichero.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero));


        if (intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);

        //    System.out.println("ESTOY En HACER FOTO ALTA RESOLUCION EN INTENT RESOLVER");
        }else{
            Toast.makeText(MainActivity.this, "Necesitas un programa que haga fotos.", Toast.LENGTH_SHORT).show();
        }
    }

    private File crearFicheroDeFoto()  throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "misFotos_"+fechaYHora;
        File carpetaFotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagenAltaResolucion = null;

            imagenAltaResolucion = File.createTempFile(nombreFichero,".jpg", carpetaFotos);

        return imagenAltaResolucion;
    }

    private int getEstadosInicioAplicacion() {
        SharedPreferences sp = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt(ESTADO_INICIO, -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt(ESTADO_INICIO, currentVersionCode).apply();
        return result;
    }

    private void GuardarContraseña(){
        sharedPreferences = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(CONTRASENA_ACCESO,editTextContraseña.getText().toString());
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case PEDI_PERMISO_DE_ESCRITURA:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  hacerFotoAltaResolucion();
                }else{
                    Toast.makeText(this, "Sin permiso de escritura no puedo hacer foto a alta resolución.", Toast.LENGTH_SHORT).show();
                }
                break;


            case PEDI_PERMISO_DE_LECTURA:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    AccesoGaleria();
                }else{
                    Toast.makeText(this, "No tienes Permiso de Lectura", Toast.LENGTH_SHORT).show();
                }
                break;




        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("RESQUESTCODE: "+requestCode);
        switch (requestCode){
            case FOTO_SELECCIONADA_GALERIA:
                if(resultCode==RESULT_OK && data!=null) {
                    System.out.println("HOLA");

                    Uri uri=data.getData();
                    sharedPreferences = getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(RUTA_IMAGEN_GALERIA, String.valueOf(uri));

                   // System.out.println("URI FOTO: " + data.getData());

                    editor.commit();


                    imageView.setImageURI(Uri.parse(sharedPreferences.getString(RUTA_IMAGEN_GALERIA,null)));

                }
                break;


            case VENGO_DE_LA_CAMARA_CON_FICHERO:
                System.out.println("ESTOY EN CASE VENGO DE LA CAMARA");

                if(resultCode==RESULT_OK) {
                    sharedPreferences= getSharedPreferences(NOMBRE_FICHERO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(RUTA_IMAGEN_CAMARA, String.valueOf(Uri.fromFile(fichero)));

                    System.out.println("URI FOTO: " + fichero.getAbsolutePath());

                    editor.commit();

                    imageView.setImageURI(Uri.parse(sharedPreferences.getString(RUTA_IMAGEN_CAMARA, null)));

                break;
            }

        }
    }


}