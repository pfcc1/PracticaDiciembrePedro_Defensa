package com.example.practicadiciembrepedro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

public class CambioEstado extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        //SharedPreferences sharedPreferences=context.getSharedPreferences(MainActivity.NOMBRE_FICHERO,Context.MODE_PRIVATE);
        //String checkboxActivo=sharedPreferences.getString(Menu.ESTADO_CHECKBOX_ALARMA_PANTALLA,null);

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)/* && checkboxActivo.equals("1")*/){
            Log.i("ESTADO PANTALLA","ALARMA PANTALLA");
            MediaPlayer mediaPlayer=MediaPlayer.create(context,R.raw.telefono_antiguo);
            mediaPlayer.start();
            //Menu.mediaPlayer.start();
        }else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.i("ESTADO","ARRANCO EL SERVICIO");
            ServicioIntensoAlarmaPantalla.encolarTrabajo(context, new Intent());

        }
    }
}
