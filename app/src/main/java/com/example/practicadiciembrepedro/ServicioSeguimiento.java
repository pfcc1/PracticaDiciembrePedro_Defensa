package com.example.practicadiciembrepedro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServicioSeguimiento extends BroadcastReceiver {
    Menu menu=new Menu();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            menu.DatosParaInsertar();
        }else if(intent.getAction().equals("android.net.wifi.STATE_CHANGE")){
            menu.DatosParaInsertar();
        }


    }


}
