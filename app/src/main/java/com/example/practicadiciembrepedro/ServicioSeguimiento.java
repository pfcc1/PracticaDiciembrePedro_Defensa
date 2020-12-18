package com.example.practicadiciembrepedro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.widget.Toast;

import static android.content.Context.AUDIO_SERVICE;

public class ServicioSeguimiento extends BroadcastReceiver {
    public static String ACCION_PANTALLA;
    public static int CAMBIO_VOLUMEN;
   static Menu menu;
    @Override
    public void onReceive(Context context, Intent intent) {

        // KeyEvent ke = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            menu.DatosParaInsertar();
            ACCION_PANTALLA = "1";

            System.out.println("ESTADO ACCION PANTALLA: " + ACCION_PANTALLA);
            System.out.println("ENCENDIDO PANTALLA ");
        } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            menu.DatosParaInsertar();
        } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
            menu.DatosParaInsertar();
        } else if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {


            int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
            int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

            if (newVolume != oldVolume) {

                    if (newVolume > oldVolume) {

                        Toast.makeText(context, "Volume Up pressed", Toast.LENGTH_SHORT).show();
                        menu.DatosParaInsertar();
                        menu.NotificacionVolumen();
                    } else {

                        Toast.makeText(context, "Volume Down pressed", Toast.LENGTH_SHORT).show();
                        menu.DatosParaInsertar();
                        menu.NotificacionVolumen();
                    }
                }


        }
    }


    public static void setActividadMenu(Menu este){
        menu=este;

    }


}
