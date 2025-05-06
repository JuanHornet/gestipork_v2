package com.example.proyecto_gestipork.modelo.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class SalidaAdapter extends RecyclerView.Adapter<SalidaAdapter.SalidaViewHolder> {

    private List<SalidaTab> lista;

    public SalidaAdapter(List<SalidaTab> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public SalidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salida, parent, false);
        return new SalidaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull SalidaViewHolder holder, int position) {
        SalidaTab salida = lista.get(position);
        holder.textMotivo.setText(salida.getMotivo());
        holder.textFecha.setText(salida.getFecha());
        holder.textCantidad.setText("" + salida.getCantidad());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class SalidaViewHolder extends RecyclerView.ViewHolder {
        TextView textMotivo, textFecha, textCantidad;

        public SalidaViewHolder(@NonNull View itemView) {
            super(itemView);
            textMotivo = itemView.findViewById(R.id.text_motivo);
            textFecha = itemView.findViewById(R.id.text_fecha);
            textCantidad = itemView.findViewById(R.id.text_cantidad);
        }
    }
}
