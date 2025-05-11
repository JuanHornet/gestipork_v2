package com.example.proyecto_gestipork.modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class ContarAdapter extends RecyclerView.Adapter<ContarAdapter.ViewHolder> {

    private List<Conteo> listaConteos;

    public ContarAdapter(List<Conteo> listaConteos) {
        this.listaConteos = listaConteos;
    }

    @NonNull
    @Override
    public ContarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conteo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContarAdapter.ViewHolder holder, int position) {
        Conteo conteo = listaConteos.get(position);
        holder.textAnimales.setText("Animales: " + conteo.getnAnimales());
        holder.textObservaciones.setText(conteo.getObservaciones().isEmpty() ? "Sin observaciones" : conteo.getObservaciones());
        holder.textFecha.setText(conteo.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaConteos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textAnimales, textObservaciones, textFecha;

        ViewHolder(View itemView) {
            super(itemView);
            textAnimales = itemView.findViewById(R.id.textAnimalesConteo);
            textObservaciones = itemView.findViewById(R.id.textObservacionesConteo);
            textFecha = itemView.findViewById(R.id.textFechaConteo);
        }
    }
}
