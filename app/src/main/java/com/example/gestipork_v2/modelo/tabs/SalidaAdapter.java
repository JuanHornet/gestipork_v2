package com.example.gestipork_v2.modelo.tabs;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.modelo.SalidasExplotacion;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SalidaAdapter extends RecyclerView.Adapter<SalidaAdapter.ViewHolder> {

    private List<SalidasExplotacion> lista;
    private OnSalidaClickListener listener;

    public interface OnSalidaClickListener {
        void onEditarSalida(SalidasExplotacion salida);
        void onEliminarSalida(SalidasExplotacion salida);
    }

    public SalidaAdapter(List<SalidasExplotacion> lista, OnSalidaClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salida, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalidasExplotacion salida = lista.get(position);
        holder.textMotivo.setText(salida.getTipoSalida());

        // 🛠️ PARSEAR fechaSalida que ahora es un String
        String fechaRaw = salida.getFechaSalida(); // ya es un String tipo "yyyy-MM-dd"
        String fechaFormateada = "";

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat destino = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            fechaFormateada = destino.format(isoFormat.parse(fechaRaw));
        } catch (Exception e) {
            fechaFormateada = fechaRaw; // si falla, se muestra tal cual
        }

        holder.textFecha.setText(fechaFormateada);
        holder.textCantidad.setText(String.valueOf(salida.getnAnimales()));

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_accion_item); // Reutilizamos el mismo menú

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_editar_accion) {
                    listener.onEditarSalida(salida);
                    return true;
                } else if (item.getItemId() == R.id.menu_eliminar_accion) {
                    listener.onEliminarSalida(salida);
                    return true;
                }
                return false;
            });

            popup.show();

            // 🔴 Color rojo al botón eliminar
            MenuItem eliminarItem = popup.getMenu().findItem(R.id.menu_eliminar_accion);
            SpannableString s = new SpannableString(eliminarItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            eliminarItem.setTitle(s);

            return true;
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMotivo, textFecha, textCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMotivo = itemView.findViewById(R.id.text_motivo);
            textFecha = itemView.findViewById(R.id.text_fecha);
            textCantidad = itemView.findViewById(R.id.text_cantidad);
        }
    }
}
