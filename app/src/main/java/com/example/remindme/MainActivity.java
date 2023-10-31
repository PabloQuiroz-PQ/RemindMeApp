package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton add_reminder;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprueba si ya tienes el permiso concedido
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Si no lo tienes, pídelo al usuario
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        }

        createNotificationChannel();

        cargarRecordatorios();

        add_reminder = findViewById(R.id.add_reminder);
        add_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un intent para iniciar otra actividad
                Intent intent = new Intent(MainActivity.this, AddReminder.class);
                // Inicia la actividad con el intent
                startActivity(intent);
            }
        });
    }

    // Recibe la respuesta del usuario en el método onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Comprueba si se trata del mismo código de solicitud
        if (requestCode == REQUEST_CODE) {
            // Comprueba si el usuario concedió o denegó el permiso
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "La aplicación no tiene permiso para notificar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRecordatorios();
    }

    private void cargarRecordatorios(){
        SharedPreferences sharedPreferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);
        String jsonRecordatorios = sharedPreferences.getString("recordatorios", null);
        List<Recordatorio> recordatorios = null;
        if (jsonRecordatorios != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Recordatorio>>() {}.getType();
            recordatorios = gson.fromJson(jsonRecordatorios, listType);

            RecyclerView recyclerView = findViewById(R.id.rvRecordatorios);
            RecordatoriosAdapter adaptador = new RecordatoriosAdapter(recordatorios, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adaptador);
        }
    }

    public void eliminarRecordatorio(int idRecordatorio){
        Gson gson = new Gson();
        Recordatorio[] recordatorios;
        ArrayList<Recordatorio> listaRecordatorios = new ArrayList<Recordatorio>();

        SharedPreferences preferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);
        //Obtener el json guardado con la llave "recordatorios"
        String json = preferences.getString("recordatorios", null);
        if (json != null && !json.isEmpty()) {
            recordatorios = gson.fromJson(json, Recordatorio[].class);
            if (recordatorios != null) {
                listaRecordatorios = new ArrayList<>(Arrays.asList(recordatorios));
                for (Recordatorio recordatorio : listaRecordatorios) {
                    if (recordatorio.getIdRecordatorio() == idRecordatorio) {
                        listaRecordatorios.remove(recordatorio);
                        break;
                    }
                }
            }
        }
        json = gson.toJson(listaRecordatorios);

        //Guarda array de objetos con el objeto eliminado
        SharedPreferences sharedPreferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("recordatorios", json);
        editor.apply();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
        // Crea un PendingIntent que coincida con el que configuraste previamente
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idRecordatorio, intent, PendingIntent.FLAG_MUTABLE);

        // Cancela la alarma
        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("RemindMe", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel For RemindMe");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}