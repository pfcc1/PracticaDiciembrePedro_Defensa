package com.example.practicadiciembrepedro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    public static MediaPlayer mediaPlayer;
    CambioEstado cambioEstado = new CambioEstado();
    CheckBox checkBoxAlarmaPantalla, checkBoxAlarmaProximidad;
    SharedPreferences sharedPreferences;
    String selCheckboxAlarmaPantalla = "0";
    String selCheckboxAlarmaProximidad = "0";
    SeekBar seekBar;
    TextView textViewContadorSeekbar,textViewEstadoSeguimiento;
    LocationManager locationManager;
    LocationListener locationListener;
    int metrosSeekbar;
    double longitudInicial, latitudInicial;
    int banderaProximidad = 0;
    int banderaCancionProximidad=0;
ManejadorBD manejadorBD=new ManejadorBD();
Button buttonActivarSeguimiento;
double latitudActual,longitudActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mediaPlayer = MediaPlayer.create(this, R.raw.telefono_antiguo);

        buttonActivarSeguimiento=findViewById(R.id.buttonActivarDesactivarSeguimiento);
        seekBar = findViewById(R.id.seekBar);
        textViewContadorSeekbar = findViewById(R.id.textViewContadorSeekbar);
        textViewEstadoSeguimiento=findViewById(R.id.textViewEstadoSeguimiento);

        checkBoxAlarmaPantalla = findViewById(R.id.checkBoxAlarmaPantalla);
        checkBoxAlarmaProximidad = findViewById(R.id.checkBoxAlarmaProximidad);

        sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);

        buttonActivarSeguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String estadoSeguimiento= sharedPreferences.getString(ESTADO_ACTIVACION_SEGUIMIENTO,null);

               if(estadoSeguimiento==null){
                   estadoSeguimiento="0";

               }

               if(estadoSeguimiento.equals("1")){
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString(ESTADO_ACTIVACION_SEGUIMIENTO, "1");
                   editor.commit();

                   textViewEstadoSeguimiento.setText("Seguimiento Activado");
               }else if(estadoSeguimiento.equals("0")){
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString(ESTADO_ACTIVACION_SEGUIMIENTO, "0");
                   editor.commit();

                   textViewEstadoSeguimiento.setText("Seguimiento Desactivado");
               }
            }
        });







        selCheckboxAlarmaPantalla = sharedPreferences.getString(ESTADO_CHECKBOX_ALARMA_PANTALLA, null);

        System.out.println("ESTADO CHECKBOX: " + selCheckboxAlarmaPantalla);

        if (selCheckboxAlarmaPantalla == null) {
            selCheckboxAlarmaPantalla = "0";
        }

        if (selCheckboxAlarmaPantalla.equals("1")) {
            System.out.println("ESTOY DENTRO DE SETCHECKED");

            checkBoxAlarmaPantalla.setChecked(true);

            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            getBaseContext().registerReceiver(cambioEstado, intentFilter);


        } else if (selCheckboxAlarmaPantalla.equals("0")) {
            sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ESTADO_CHECKBOX_ALARMA_PANTALLA, "0");
            editor.commit();
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
                    /*checkBoxAlarmaPantalla.setChecked(true);
                    checkBoxAlarmaPantalla.setEnabled(true);*/
                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                    getBaseContext().registerReceiver(cambioEstado, intentFilter);
                    lanzarNotificacionEstadoAlarma(true);

                } else {
                    sharedPreferences = getSharedPreferences(MainActivity.NOMBRE_FICHERO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ESTADO_CHECKBOX_ALARMA_PANTALLA, "0");
                    editor.commit();
                    //checkBoxAlarmaPantalla.setChecked(false);
                    selCheckboxAlarmaPantalla = "0";
                    lanzarNotificacionEstadoAlarma(false);
                }


            }
        });

        checkBoxAlarmaProximidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                     AlarmaProximidad();

                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewContadorSeekbar.setText(String.valueOf(progress));
                metrosSeekbar = progress;
                System.out.println("METROS SEEKBAR: "+metrosSeekbar);
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

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.

                    Toast.makeText(this, "No tienes Permisos de GPS", Toast.LENGTH_SHORT).show();
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_GPS);


        }
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.vuelve);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    
                }

                @Override
                public void onLocationChanged(@NonNull Location location) {


                    if (banderaProximidad == 0) {
                        banderaProximidad = 1;
                        longitudInicial = location.getLongitude();
                        latitudInicial = location.getLatitude();
                        System.out.println("ESTOY EN ALARMA PROXIMIDAD INICIAL");

                    }
                    if (banderaProximidad == 1) {
                        Location locationInicial = new Location("Inicial");
                        locationInicial.setLatitude(latitudInicial);
                        locationInicial.setLongitude(longitudInicial);

                        System.out.println("Latitud Inicial: "+locationInicial.getLatitude());
                        System.out.println("Longitud Inicial: "+locationInicial.getLongitude());

                        location.setLatitude(location.getLatitude());
                        location.setLongitude(location.getLongitude());

                        System.out.println("Latitud Actual: "+location.getLatitude());
                        System.out.println("Longitud Actual: "+location.getLongitude());
                        System.out.println("ESTOY EN ELSE CALCULANDO METROS RECORRIDOS");


                        double metrosrecorridos = locationInicial.distanceTo(location);
                        double metr = redondearDecimales(metrosrecorridos, 2);

                        System.out.println("Metros: " + metr);
                        //System.out.println("Metros Recorridos: " + metrosrecorridos);
                        Toast.makeText(getApplicationContext(), "Metros Recorridos: " + metr, Toast.LENGTH_SHORT).show();


                        System.out.println("METROS SEEKBAR: "+metrosSeekbar);


                            if (metr >= metrosSeekbar) {

                                if(mediaPlayer.isPlaying()){

                                    System.out.println("Duración Cancion: "+mediaPlayer.getDuration());
                                    System.out.println("Posicion Cancion: "+mediaPlayer.getCurrentPosition());

                                }else{
                                     mediaPlayer.start();

                                }

                            }

                    }

                }
            };


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
        }
    }

    private void PosicionGPSActual(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_GPS);


        }else {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitudActual=location.getLatitude();
                    longitudActual=location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO, 0, locationListener);
        }

    }

    private int PorcentajeActualBateria(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float battery = (level / (float)scale)*100;

        return Math.round(battery*100)/100;
    }

    public void DatosParaInsertar(){
        PosicionGPSActual();

        Date fecha=new Date();
        DateFormat dateFormatFecha = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateFormatHora=new SimpleDateFormat("HH:mm:ss");

        String fechaActual= dateFormatFecha.format(fecha);
        String horaActual=  dateFormatHora.format(fecha);

        int porcentajeBateria=PorcentajeActualBateria();



    boolean insertar=  manejadorBD.insertar(fechaActual,horaActual, String.valueOf(porcentajeBateria),String.valueOf(latitudActual),String.valueOf(longitudActual));

     if(insertar){
         Toast.makeText(getApplicationContext(),"Insertado Correctamente",Toast.LENGTH_SHORT).show();
     }else{
         Toast.makeText(getApplicationContext(),"Error no se ha Insertado",Toast.LENGTH_SHORT).show();
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

   /* class CambioEstadoPantalla extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.i("ESTADO PANTALLA","ALARMA PANTALLA");

                mediaPlayer.start();
            }
        }
    }

    */
}