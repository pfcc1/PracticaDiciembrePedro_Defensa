package com.example.practicadiciembrepedro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Menu extends AppCompatActivity {
    public static final String ESTADO_CHECKBOX_ALARMA_PANTALLA = "1";
    private static final String ID_CANAL = "CANAL ALARMA";
    private static final String ESTADO_CHECKBOX_ALARMA_PROXIMIDAD = "2";
    private static final long TIEMPO_REFRESCO = 500;
    private static final int PERMISO_GPS = 3;
    private static final String ESTADO_ACTIVACION_SEGUIMIENTO = "4";
    private static final String RUTA_ARCHIVO_CSV = "5";
    public static MediaPlayer mediaPlayer;
    CambioEstado cambioEstado = new CambioEstado();
   CheckBox checkBoxAlarmaPantalla, checkBoxAlarmaProximidad;
    SharedPreferences sharedPreferences;
    String selCheckboxAlarmaPantalla = "0";
    String selCheckboxAlarmaProximidad = "0";
    SeekBar seekBar;
    TextView textViewContadorSeekbar, textViewEstadoSeguimiento;
    LocationManager locationManager;
    LocationListener locationListener;
    int metrosSeekbar;
    double longitudInicial, latitudInicial;
    int banderaProximidad = 0;
    int banderaProximidadGPS_BD = 0;
    ServicioSeguimiento servicioSeguimiento;

    int banderaActivacion_GPS_AlarmaProximidad=0;
    int banderaActivacion_GPS_Seguimiento=0;

    Button buttonActivarSeguimiento, buttonMostrarDatos, buttonBorrarDatos, buttonCompartirCSV;
    double latitudActual, longitudActual;
    ManejadorBD manejadorBD;
    AlertDialog alert = null;
   public int salirAlarma=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mediaPlayer = MediaPlayer.create(this, R.raw.telefono_antiguo);
        manejadorBD = new ManejadorBD(this);
        servicioSeguimiento = new ServicioSeguimiento();

        buttonActivarSeguimiento = findViewById(R.id.buttonActivarDesactivarSeguimiento);
        buttonMostrarDatos = findViewById(R.id.buttonDatos);
        buttonBorrarDatos = findViewById(R.id.buttonBorrarDatos);
        buttonCompartirCSV = findViewById(R.id.buttonCompartir);
        seekBar = findViewById(R.id.seekBar);
        textViewContadorSeekbar = findViewById(R.id.textViewContadorSeekbar);
        textViewEstadoSeguimiento = findViewById(R.id.textViewEstadoSeguimiento);

        checkBoxAlarmaPantalla = findViewById(R.id.checkBoxAlarmaPantalla);
        checkBoxAlarmaProximidad = findViewById(R.id.checkBoxAlarmaProximidad);



     //Obtengo los datos almacenados en el Fichero SharedPreferences
        sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);

        //Pasarle el contexto del Activity Menu
        //al servicio de seguimiento sino no funciona
        ServicioSeguimiento.setActividadMenu(this);


        System.out.println("ESTADO ACCION PANTALLA LLL: " + ServicioSeguimiento.ACCION_PANTALLA);
       /* if (ServicioSeguimiento.ACCION_PANTALLA == null) {
            ServicioSeguimiento.ACCION_PANTALLA = "0";
        }

        */


        //Comprobar estado Servicio Seguimiento
        String estadoSeguimiento = sharedPreferences.getString(ESTADO_ACTIVACION_SEGUIMIENTO, null);

        if (estadoSeguimiento == null) {
            estadoSeguimiento = "0";

        }


        if (estadoSeguimiento.equals("1")) {//Seguimiento Activado

            textViewEstadoSeguimiento.setText("Seguimiento Activado");
            PosicionGPSActual();
            IntentFilter intentFilter2 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
            intentFilter2.addAction("android.net.wifi.STATE_CHANGE");
            intentFilter2.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter2.addAction("android.media.VOLUME_CHANGED_ACTION");

            getBaseContext().registerReceiver(servicioSeguimiento, intentFilter2);
            // System.out.println("ESTADO ACCION PANTALLA: "+ServicioSeguimiento.ACCION_PANTALLA);
           /* if (ServicioSeguimiento.ACCION_PANTALLA == "1") {
                DatosParaInsertar();
            }

            */
ServicioSeguimiento.CAMBIO_VOLUMEN=0;

        } else if (estadoSeguimiento.equals("0")) {//Seguimiento Desactivado
            textViewEstadoSeguimiento.setText("Seguimiento Desactivado");
        }


        buttonActivarSeguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
                String estadoSeguimiento = sharedPreferences.getString(ESTADO_ACTIVACION_SEGUIMIENTO, null);

                System.out.println("Estado Seguimiento BOTON: " + estadoSeguimiento);

                if (estadoSeguimiento == null) {
                    estadoSeguimiento = "0";

                }

                if (estadoSeguimiento.equals("1")) {//Desactivar Seguimiento


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ESTADO_ACTIVACION_SEGUIMIENTO, "0");
                    editor.commit();

                    estadoSeguimiento = "0";
                    textViewEstadoSeguimiento.setText("Seguimiento Desactivado");

                } else if (estadoSeguimiento.equals("0")) {//Activar Seguimiento

                        PosicionGPSActual();


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ESTADO_ACTIVACION_SEGUIMIENTO, "1");
                    editor.commit();

                    IntentFilter intentFilter2 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
                    intentFilter2.addAction("android.net.wifi.STATE_CHANGE");
                    intentFilter2.addAction(Intent.ACTION_SCREEN_ON);
                    intentFilter2.addAction("android.media.VOLUME_CHANGED_ACTION");

                    getBaseContext().registerReceiver(servicioSeguimiento, intentFilter2);

                    banderaActivacion_GPS_Seguimiento=1;
                    estadoSeguimiento = "1";
                    textViewEstadoSeguimiento.setText("Seguimiento Activado");

                    ServicioSeguimiento.CAMBIO_VOLUMEN=0;
                }
            }
        });


        buttonMostrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentMostrarDatos = new Intent(getApplicationContext(), MostrarDatos.class);
                startActivity(intentMostrarDatos);
            }
        });

        buttonBorrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("ESTOY EN BORRAR");

                Cursor cursorBorrar = manejadorBD.listar();//Listar Datos
                int bandera = 0;
                if (cursorBorrar != null && cursorBorrar.getCount() > 0) {
                    while (cursorBorrar.moveToNext()) {

                        boolean Borrar = manejadorBD.borrar(cursorBorrar.getString(0));
                        bandera = 1;
                    }
                }

                cursorBorrar.close();//Cerrar Cursor


                if (bandera == 1) {
                    Toast.makeText(getApplicationContext(), "Borrado Todo Correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No hay registros para borrar", Toast.LENGTH_SHORT).show();
                }


            }
        });

        buttonCompartirCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exportarCSVYCompartir();

            }
        });


        //Alarma Pantalla
        selCheckboxAlarmaPantalla = sharedPreferences.getString(ESTADO_CHECKBOX_ALARMA_PANTALLA, null);

        System.out.println("ESTADO CHECKBOX: " + selCheckboxAlarmaPantalla);

        if (selCheckboxAlarmaPantalla == null) {
            selCheckboxAlarmaPantalla = "0";
        }

        if (selCheckboxAlarmaPantalla.equals("1")) {//Alarma Activada
            System.out.println("ESTOY DENTRO DE SETCHECKED");

            checkBoxAlarmaPantalla.setChecked(true);

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            getBaseContext().registerReceiver(cambioEstado, intentFilter);



        } else if (selCheckboxAlarmaPantalla.equals("0")) {//Alarma Desactivada
            sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ESTADO_CHECKBOX_ALARMA_PANTALLA, "0");
            editor.commit();
            checkBoxAlarmaPantalla.setChecked(false);
        }


        checkBoxAlarmaPantalla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // System.out.println("LO HE CHEQUEADO");
                System.out.println("IS CHECKED: " + isChecked);
                if (isChecked) {
                    sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ESTADO_CHECKBOX_ALARMA_PANTALLA, "1");
                    editor.commit();

                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                    getBaseContext().registerReceiver(cambioEstado, intentFilter);
                    lanzarNotificacionEstadoAlarma(true);
                    CambioEstado.ActivacionAlarmaPantalla=1;
                } else {
                    sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ESTADO_CHECKBOX_ALARMA_PANTALLA, "0");
                    editor.commit();

                    CambioEstado.ActivacionAlarmaPantalla=0;
                    selCheckboxAlarmaPantalla = "0";

                    lanzarNotificacionEstadoAlarma(false);
                }


            }
        });


        checkBoxAlarmaProximidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    banderaActivacion_GPS_AlarmaProximidad=1;
                    AlarmaProximidad();


                    banderaProximidadGPS_BD=0;
                        salirAlarma=0;



                }else{

                    longitudInicial=0;
                    latitudInicial=0;
                    banderaProximidad=0;

                        salirAlarma=1;

                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewContadorSeekbar.setText(String.valueOf(progress));
                metrosSeekbar = progress;
                System.out.println("METROS SEEKBAR: " + metrosSeekbar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISO_GPS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "No tienes permiso de GPS", Toast.LENGTH_SHORT).show();
                    }else {
                        if(banderaActivacion_GPS_AlarmaProximidad==1){

                            AlarmaProximidad();
                        }else if(banderaActivacion_GPS_Seguimiento==1){
                            PosicionGPSActual();
                        }

                    }



                } else {

                    Toast.makeText(this, "Debes darme permiso para continuar", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado = (resultado - parteEntera) * Math.pow(10, numeroDecimales);
        resultado = Math.round(resultado);
        resultado = (resultado / Math.pow(10, numeroDecimales)) + parteEntera;
        return resultado;
    }

    private void DialogoParaActivacionGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void exportarCSVYCompartir() {

        File carpeta = new File(Environment.getExternalStorageDirectory() + "/ExportarSQLiteCSV");
        String archivoCSV = carpeta.toString() + "/" + "Seguimiento.csv";

        boolean isCreate = false;
        if (!carpeta.exists()) {
            isCreate = carpeta.mkdir();
        }

        try {
            FileWriter fileWriter = new FileWriter(archivoCSV);

            ManejadorBD admin = new ManejadorBD(this);

            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("select * from SEGUIMIENTO", null);

            if (fila != null && fila.getCount() > 0) {
                while (fila.moveToNext()) {

                    fileWriter.append(fila.getString(0));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(4));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(5));
                    fileWriter.append("\n");

                }
            } else {
                Toast.makeText(getApplicationContext(), "No hay registros.", Toast.LENGTH_LONG).show();
            }

            db.close();
            fileWriter.close();
            Toast.makeText(getApplicationContext(), "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
        }

        //Compartir
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");

        Uri uri = Uri.fromFile(new File(archivoCSV));

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        Intent intentSeleccionador = (Intent.createChooser(intent, "Compartir con"));//Compartir con cualquier aplicación
        startActivity(intentSeleccionador);


    }



    private void AlarmaProximidad() {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_GPS);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    DialogoParaActivacionGPS();
                }

            } else {


                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.vuelve);
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    DialogoParaActivacionGPS();
                }
                locationListener = new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {

                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {


                    }

                    @Override
                    public void onLocationChanged(@NonNull Location location) {


                        if (banderaProximidad == 0) {
                            banderaProximidad = 1;
                            longitudInicial = location.getLongitude();
                            latitudInicial = location.getLatitude();
                            System.out.println("ESTOY EN ALARMA PROXIMIDAD INICIAL");

                        }
                        if (banderaProximidad == 1 && salirAlarma==0) {
                            Location locationInicial = new Location("Inicial");
                            locationInicial.setLatitude(latitudInicial);
                            locationInicial.setLongitude(longitudInicial);

                            System.out.println("Latitud Inicial: " + locationInicial.getLatitude());
                            System.out.println("Longitud Inicial: " + locationInicial.getLongitude());

                            location.setLatitude(location.getLatitude());
                            location.setLongitude(location.getLongitude());

                            System.out.println("Latitud Actual: " + location.getLatitude());
                            System.out.println("Longitud Actual: " + location.getLongitude());
                            System.out.println("ESTOY EN ELSE CALCULANDO METROS RECORRIDOS");


                            double metrosrecorridos = locationInicial.distanceTo(location);
                            double metr = redondearDecimales(metrosrecorridos, 2);

                            System.out.println("Metros: " + metr);
                            //System.out.println("Metros Recorridos: " + metrosrecorridos);
                            Toast.makeText(getApplicationContext(), "Metros Recorridos: " + metr, Toast.LENGTH_SHORT).show();


                            System.out.println("METROS SEEKBAR: " + metrosSeekbar);


                            if (metr >= metrosSeekbar) {

                                if (banderaProximidadGPS_BD == 0) {
                                    banderaProximidadGPS_BD = 1;

                                    DatosParaInsertar();

                                    if (mediaPlayer.isPlaying()) {

                                        System.out.println("Duración Cancion: " + mediaPlayer.getDuration());
                                        System.out.println("Posicion Cancion: " + mediaPlayer.getCurrentPosition());

                                    } else {
                                        mediaPlayer.start();

                                    }
                                }
                            }
                        }

                    }

                };


                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
            }
        }

public void NotificacionVolumen(){
    String idChannel="Notificaciones con Fotos";
    String nombreCanal="Notificaciones con FOtos";

    NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,ID_CANAL)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Notificación de texto en varias lineas")
            .setAutoCancel(true)
            .setContentTitle("Texto inicial");

    NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
    bigPictureStyle.setBigContentTitle("Notificación Volumen");
    bigPictureStyle.setSummaryText("Cambio Volumen");

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
    notificationManager.notify(3,builder.build());
}


    private void PosicionGPSActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_GPS);

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DialogoParaActivacionGPS();
            }

        } else {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DialogoParaActivacionGPS();
            }

            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
        }

    }

    public int PorcentajeActualBateria(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float battery = (level / (float)scale)*100;

        return Math.round(battery*100)/100;
    }

   public void DatosParaInsertar(){


           Date fecha = new Date();
           DateFormat dateFormatFecha = new SimpleDateFormat("dd/MM/yyyy");
           DateFormat dateFormatHora = new SimpleDateFormat("HH:mm:ss");

           String fechaActual = dateFormatFecha.format(fecha);
           String horaActual = dateFormatHora.format(fecha);

           int porcentajeBateria = PorcentajeActualBateria();


           System.out.println("Fecha: " + fechaActual);
           System.out.println("Hora: " + horaActual);
           System.out.println("Bateria: " + porcentajeBateria);
           System.out.println("Latitud: " + latitudActual);
           System.out.println("Longitud: " + longitudActual);


           if(latitudActual!=0 && longitudActual!=0) {


               boolean insertar = manejadorBD.insertar(fechaActual, horaActual, String.valueOf(porcentajeBateria), String.valueOf(latitudActual), String.valueOf(longitudActual));

               if (insertar) {
                   Toast.makeText(getApplicationContext(), "Insertado Correctamente", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(getApplicationContext(), "Error no se ha Insertado", Toast.LENGTH_SHORT).show();
               }

           }
       }





    private void lanzarNotificacionEstadoAlarma(boolean estadoCheck){
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String contenText;
        if(estadoCheck){
            contenText="La Alarma de Pantalla esta activada";
        }else{
            contenText="La Alarma de Pantalla esta desactivada";
        }

        Context context;
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,ID_CANAL).

                setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Estado Alarma Pantalla")
                .setAutoCancel(true)


                .setContentText(contenText);


        //Version Android Moderna
        String idChannel="1";
        String nombreCanal="Nombre Canal";
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

        TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(getApplicationContext());
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("NOMBRE","NOTIFIACION SENCIALLA");
        taskStackBuilder.addNextIntent(intent);

        notificationManager.notify(1,builder.build());
    }


}

