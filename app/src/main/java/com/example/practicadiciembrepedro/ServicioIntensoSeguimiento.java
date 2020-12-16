package com.example.practicadiciembrepedro;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class ServicioIntensoSeguimiento extends JobIntentService {
    private static final int JOB_ID =2 ;
ServicioSeguimiento servicioSeguimiento=new ServicioSeguimiento();
    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        while (true) {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            getBaseContext().registerReceiver(servicioSeguimiento, intentFilter);
        }

    }

    static void encolarTrabajo(Context context, Intent work){
        enqueueWork(context,ServicioIntensoSeguimiento.class,JOB_ID,work);
    }

}
