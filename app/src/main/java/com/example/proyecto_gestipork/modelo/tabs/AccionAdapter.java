package com.example.proyecto_gestipork.modelo.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.ViewHolder> {

    private List<Accion> acciones;
    private OnAccionLongClickListener longClickListener;

    public interface OnAccionLongClickListener {
        void onAccionLongClick(Accion accion);
    }

    public AccionAdapter(List<Accion> acciones, OnAccionLongClickListener listener) {
        this.acciones = acciones;
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accion, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Accion accion = acciones.get(position);
        holder.bind(accion);
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onAccionLongClick(accion);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return acciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTipo, textFecha, textCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTipo = itemView.findViewById(R.id.text_tipo);
            textFecha = itemView.findViewById(R.id.text_fecha);
            textCantidad = itemView.findViewById(R.id.text_cantidad);
        }

        public void bind(Accion accion) {
            textTipo.setText(accion.getTipo());
            textFecha.setText(accion.getFecha());
            textCantidad.setText(String.valueOf(accion.getCantidad()));
        }
    }
}
