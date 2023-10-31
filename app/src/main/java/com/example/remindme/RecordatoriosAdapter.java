package com.example.remindme;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordatoriosAdapter extends RecyclerView.Adapter<RecordatoriosAdapter.ViewHolder> {
    private List<Recordatorio> listaRecordatorios;
    private MainActivity activity;

    public RecordatoriosAdapter(List<Recordatorio> listaRecordatorios, MainActivity activity) {
        this.listaRecordatorios = listaRecordatorios;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recordatorio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recordatorio recordatorio = listaRecordatorios.get(position);
        holder.textIdRecordatorio.setText(recordatorio.getIdRecordatorio() + "");
        holder.textRecordatorio.setText(recordatorio.getRecordatorio());

        String formattedDate = "";
        String formattedTime = "";
        Date fechaRecordatorio = recordatorio.getFecha();

        if (fechaRecordatorio != null) {
            formattedDate =  new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(fechaRecordatorio);

            int horaRecordatorio = recordatorio.getHora();
            int minutosRecordatorio = recordatorio.getMinutos();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, horaRecordatorio);
            calendar.set(Calendar.MINUTE, minutosRecordatorio);

            // Formatear la hora y minutos en el formato deseado
            formattedTime =  new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime());
        }

        holder.textFecha.setText(formattedDate);
        holder.textHoraMinutos.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return listaRecordatorios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textIdRecordatorio, textRecordatorio, textFecha, textHoraMinutos;
        ImageButton btnEliminar;
        public ViewHolder(View itemView) {
            super(itemView);
            textIdRecordatorio = itemView.findViewById(R.id.textIdRecordatorio);
            textRecordatorio = itemView.findViewById(R.id.textRecordatorio);
            textFecha = itemView.findViewById(R.id.textFecha);
            textHoraMinutos = itemView.findViewById(R.id.textHoraMinutos);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    activity.eliminarRecordatorio(listaRecordatorios.get(position).getIdRecordatorio());
                    listaRecordatorios.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}

