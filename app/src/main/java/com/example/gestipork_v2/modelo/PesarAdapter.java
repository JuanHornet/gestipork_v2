package com.example.gestipork_v2.modelo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.util.List;
import java.util.Locale;

public class PesarAdapter extends RecyclerView.Adapter<PesarAdapter.ViewHolder> {

    private Context context;
    private String codExplotacion, codLote;
    private List<String> fechas;     // las fechas son el dataset

    public PesarAdapter(Context context, String codExplotacion, String codLote, List<String> fechas) {
        this.context = context;
        this.codExplotacion = codExplotacion;
        this.codLote = codLote;
        this.fechas = fechas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_peso, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String fecha = fechas.get(position);
        DBHelper db = new DBHelper(context);

        // Consultar pesos de esa fecha
        Cursor cursor = db.obtenerPesosPorLoteYFecha(codExplotacion, codLote, fecha);

        int total = 0, suma = 0;
        int tramo_13 = 0, tramo_14 = 0, tramo_15 = 0, tramo_16 = 0;

        while (cursor.moveToNext()) {
            int peso = cursor.getInt(1);  // peso está en columna 1
            suma += peso;
            total++;
            if (peso < 150) tramo_13++;
            else if (peso >= 150 && peso <= 161) tramo_14++;
            else if (peso >= 162 && peso <= 173) tramo_15++;
            else if (peso >= 174) tramo_16++;
        }
        cursor.close();

        // Mostrar datos
        holder.txtFecha.setText(fecha);
        holder.txtTotal.setText("Animales: " + total);
        holder.txtMediaKg.setText("Media kg: " + (total > 0 ? (suma / total) : 0));
        holder.txtMediaArrobas.setText("Media @: " + (total > 0 ? String.format(Locale.getDefault(), "%.2f", (suma / (double) total) / 11.5) : "0"));
        holder.txtTramo13.setText("-13@: " + tramo_13);
        holder.txtTramo14.setText("13-14@: " + tramo_14);
        holder.txtTramo15.setText("14-15@: " + tramo_15);
        holder.txtTramo16.setText("+15@: " + tramo_16);

        // Al pulsar abrir CargarPesosActivity con esa fecha
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CargarPesosActivity.class);
            intent.putExtra("cod_explotacion", codExplotacion);
            intent.putExtra("cod_lote", codLote);
            intent.putExtra("fecha", fecha);               // PASAMOS FECHA
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtTotal, txtMediaKg, txtMediaArrobas;
        TextView txtTramo13, txtTramo14, txtTramo15, txtTramo16;

        ViewHolder(View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.text_fecha);
            txtTotal = itemView.findViewById(R.id.text_total_animales);
            txtMediaKg = itemView.findViewById(R.id.text_media_pesos);
            txtMediaArrobas = itemView.findViewById(R.id.text_media_arrobas);
            txtTramo13 = itemView.findViewById(R.id.text_segmentacion13);
            txtTramo14 = itemView.findViewById(R.id.text_segmentacion14);
            txtTramo15 = itemView.findViewById(R.id.text_segmentacion15);
            txtTramo16 = itemView.findViewById(R.id.text_segmentacion16);
        }
    }
}
