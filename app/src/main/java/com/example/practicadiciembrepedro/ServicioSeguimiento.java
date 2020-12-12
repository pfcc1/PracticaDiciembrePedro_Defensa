package com.example.practicadiciembrepedro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServicioSeguimiento extends BroadcastReceiver {
    public static String ACCION_PANTALLA;
   static Menu menu;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            menu.DatosParaInsertar();
            ACCION_PANTALLA="1";

            System.out.println("ESTADO ACCION PANTALLA: "+ACCION_PANTALLA);
            System.out.println("ENCENDIDO PANTALLA ");
        }else if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
         menu.DatosParaInsertar();
        }else if(intent.getAction().equals("android.net.wifi.STATE_CHANGE")){
            menu.DatosParaInsertar();
        }


    }

    public static void setActividadMenu(Menu este){
        menu=este;

    }


}
