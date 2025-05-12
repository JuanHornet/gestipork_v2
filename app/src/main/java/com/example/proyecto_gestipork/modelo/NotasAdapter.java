package com.example.proyecto_gestipork.modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    private List<Nota> listaNotas;

    public NotasAdapter(List<Nota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Nota nota = listaNotas.get(position);
        holder.txtFecha.setText(nota.getFecha());
        holder.txtObservacion.setText(nota.getObservacion());
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtObservacion;

        ViewHolder(View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.text_fecha_nota);
            txtObservacion = itemView.findViewById(R.id.text_observacion_nota);
        }
    }
}
