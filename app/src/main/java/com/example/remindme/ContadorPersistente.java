package com.example.remindme;

import android.content.Context;
import android.content.SharedPreferences;
public class ContadorPersistente {

    private static final String PREF_NAME = "MiContadorPrefs";
    private static final String CONTADOR_KEY = "contador";

    private SharedPreferences prefs;
    private int contador;

    public ContadorPersistente(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        contador = prefs.getInt(CONTADOR_KEY, 0);
    }

    public int obtenerContador() {
        return contador;
    }

    public int incrementarContador() {
        contador++;
        guardarContador();
        return contador;
    }

    private void guardarContador() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CONTADOR_KEY, contador);
        editor.apply();
    }
}
