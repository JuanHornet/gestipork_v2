package com.example.gestipork_v2.modelo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.repository.NotaRepository;
import com.example.gestipork_v2.base.FechaUtils;

import java.util.List;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    private List<Nota> listaNotas;

    public NotasAdapter(List<Nota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Nota nota = listaNotas.get(position);
        holder.txtFecha.setText(nota.getFecha());
        holder.txtObservacion.setText(nota.getObservacion());

        // ✅ Acción eliminar lógica
        holder.btnEliminar.setOnClickListener(v -> {
            Context context = v.getContext();
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Nota")
                    .setMessage("¿Deseas eliminar esta nota?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        DBHelper dbHelper = new DBHelper(context);
                        String fechaActual = FechaUtils.obtenerFechaActual(); // Formato ISO

                        dbHelper.getWritableDatabase().execSQL(
                                "DELETE FROM notas WHERE id = ?",
                                new Object[]{nota.getId()}
                        );


                        // Registrar eliminación en Supabase
                        NotaRepository notaRepository = new NotaRepository(context);
                        notaRepository.marcarNotaComoEliminada(nota.getId(), fechaActual);

                        listaNotas.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaNotas.size());

                        // Comprobar si hay que mostrar texto vacío
                        if (context instanceof NotasActivity) {
                            ((NotasActivity) context).comprobarNotasVacias();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtObservacion;
        ImageView btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.text_fecha_nota);
            txtObservacion = itemView.findViewById(R.id.text_observacion_nota);
            btnEliminar = itemView.findViewById(R.id.btnEliminarNota);
        }
    }
}
