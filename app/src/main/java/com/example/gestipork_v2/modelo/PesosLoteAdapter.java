package com.example.gestipork_v2.modelo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;

import java.util.List;

public class PesosLoteAdapter extends RecyclerView.Adapter<PesosLoteAdapter.ViewHolder> {

    public interface OnPesoClickListener {
        void onEliminarPeso(int position);
    }

    private Context context;
    private List<PesoItem> listaPesos;
    private OnPesoClickListener listener;

    public PesosLoteAdapter(Context context, List<PesoItem> listaPesos, OnPesoClickListener listener) {
        this.context = context;
        this.listaPesos = listaPesos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PesosLoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_peso_lote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesosLoteAdapter.ViewHolder holder, int position) {
        PesoItem pesoItem = listaPesos.get(position);
        int peso = pesoItem.getPesoKg();

        holder.txtPeso.setText(String.valueOf(peso));


        // Peso en rojo si <150kg, si no negro
        if (peso < 150) {
            holder.txtPeso.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.txtPeso.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarPeso(position));
    }

    @Override
    public int getItemCount() {
        return listaPesos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPeso;
        ImageView btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPeso = itemView.findViewById(R.id.txtPeso);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPeso);
        }
    }
}
