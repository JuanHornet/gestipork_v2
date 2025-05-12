package com.example.proyecto_gestipork.modelo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

import java.util.List;

public class PesarAdapter extends RecyclerView.Adapter<PesarAdapter.ViewHolder> {

    private Context context;
    private String codExplotacion, codLote;
    private List<String> fechas;

    public PesarAdapter(Context context, String codExplotacion, String codLote, List<String> fechas) {
        this.context = context;
        this.codExplotacion = codExplotacion;
        this.codLote = codLote;
        this.fechas = fechas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_peso, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String fecha = fechas.get(position);
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.obtenerPesosDeFecha(codExplotacion, codLote, fecha);

        int total = 0, suma = 0;
        int tramo_13 = 0, tramo_14 = 0, tramo_15 = 0, tramo_16 = 0;

        while (cursor.moveToNext()) {
            int peso = cursor.getInt(0);
            suma += peso;
            total++;
            if (peso <= 149) tramo_13++;
            else if (peso <= 161) tramo_14++;
            else if (peso <= 172) tramo_15++;
            else tramo_16++;
        }
        cursor.close();

        holder.txtFecha.setText(fecha);
        holder.txtTotal.setText("Animales pesados: " + total);
        holder.txtMedia.setText("Media: " + (total > 0 ? (suma / total) : 0) + " kg");
        holder.txtTramos.setText("-13@: " + tramo_13 + " | 13-14@: " + tramo_14 +
                " | 14-15@: " + tramo_15 + " | +15@: " + tramo_16);
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtTotal, txtMedia, txtTramos;

        ViewHolder(View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.text_fecha);
            txtTotal = itemView.findViewById(R.id.text_total_animales);
            txtMedia = itemView.findViewById(R.id.text_media_pesos);
            txtTramos = itemView.findViewById(R.id.text_tramos);
        }
    }
}
