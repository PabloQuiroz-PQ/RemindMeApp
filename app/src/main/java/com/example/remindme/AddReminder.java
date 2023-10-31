package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddReminder extends AppCompatActivity {
    private ImageButton btnDate;
    private TextView txtDate;
    private ImageButton btnGuardar;
    private TextView txtRecordatorio;
    private Date fechaRecordatorio;
    private int hora;
    private int minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // TIMEPICKER
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);
        hora = hour;
        minutos = minute;

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hora = hourOfDay;
                minutos = minute;
            }
        });

        //DATEPICKER
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        btnDate = findViewById(R.id.btnDate);
        txtDate = findViewById(R.id.txtDate);
        txtDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date(year - 1900, month, day)));
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        fechaRecordatorio = new Date(year - 1900, month, day);

        txtRecordatorio = findViewById(R.id.txtRecordatorio);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarReminder();
            }
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                fechaRecordatorio = new Date(year - 1900, month, day);
                txtDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(fechaRecordatorio));
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void guardarReminder() {
        String recordatorio = txtRecordatorio.getText().toString().trim();
        if (!recordatorio.isEmpty() && !recordatorio.equals("")) {
            Gson gson = new Gson();
            //Obtener el objeto SharedPreferences
            SharedPreferences preferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);

            Recordatorio[] recordatorios;
            ArrayList<Recordatorio> listaRecordatorios = new ArrayList<Recordatorio>();
            //Obtener el json guardado con la llave "recordatorios"
            String json = preferences.getString("recordatorios", null);

            // Validar si el JSON es nulo o vacío
            if (json != null && !json.isEmpty()) {
                // Convertir el json a un array de objetos Recordatorio
                recordatorios = gson.fromJson(json, Recordatorio[].class);

                // Validar si el array de recordatorios es nulo
                if (recordatorios != null) {
                    listaRecordatorios = new ArrayList<>(Arrays.asList(recordatorios));
                }
            }

            ContadorPersistente contadorPersistente = new ContadorPersistente(this);
            int idRecordatorio = contadorPersistente.obtenerContador();
            contadorPersistente.incrementarContador();

            // Crear un nuevo objeto Recordatorio con los datos que se reciben como parámetros
            Recordatorio nuevoRecordatorio = new Recordatorio(idRecordatorio, recordatorio, fechaRecordatorio, hora, minutos);
            listaRecordatorios.add(nuevoRecordatorio);

            //Convertir el array de objetos a un json
            json = gson.toJson(listaRecordatorios);

            //Guarda el nuevo recordatorio ya agregado al array de objetos
            SharedPreferences sharedPreferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("recordatorios", json);
            editor.apply();

            crearNotificacion(recordatorio, idRecordatorio);

            Toast.makeText(AddReminder.this, "¡Guardado éxitosamente!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(AddReminder.this, "Por favor ingrese que desea recordar", Toast.LENGTH_SHORT).show();
        }

    }
    private void crearNotificacion(String mensaje, int idRecordatorio) {  // Programar la fecha y hora en que se muestra la notificación
        Intent intent = new Intent(AddReminder.this, MyBroadcastReceiver.class);
        intent.putExtra("mensaje", mensaje);
        intent.putExtra("idRecordatorio", idRecordatorio);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddReminder.this, idRecordatorio, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaRecordatorio);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minutos);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}

