package com.example.gestipork_v2.modelo.tabs;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;

import java.util.List;

public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.ViewHolder> {

    private List<Accion> acciones;
    private OnAccionLongClickListener longClickListener;

    public interface OnAccionLongClickListener {
        void onEditarAccion(Accion accion);
        void onEliminarAccion(Accion accion);
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
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_accion_item);

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_editar_accion) {
                    longClickListener.onEditarAccion(accion);
                    return true;
                } else if (item.getItemId() == R.id.menu_eliminar_accion) {
                    longClickListener.onEliminarAccion(accion);
                    return true;
                }
                return false;
            });

            // Mostrar popup y colorear "Eliminar" en rojo
            popup.show();

            MenuItem eliminarItem = popup.getMenu().findItem(R.id.menu_eliminar_accion);
            SpannableString s = new SpannableString(eliminarItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            eliminarItem.setTitle(s);

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