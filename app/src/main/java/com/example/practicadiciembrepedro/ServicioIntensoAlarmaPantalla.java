package com.example.practicadiciembrepedro;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class ServicioIntensoAlarmaPantalla extends JobIntentService {
    private static final int JOB_ID =1 ;
CambioEstado cambioEstado=new CambioEstado();
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        while(true){
            Log.d("ESTADO","Comienzo a currar");

            IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_ON);
            getBaseContext().registerReceiver(cambioEstado,intentFilter);

        }


    }

    static void encolarTrabajo(Context context, Intent work){
        enqueueWork(context,ServicioIntensoAlarmaPantalla.class,JOB_ID,work);
    }


}
