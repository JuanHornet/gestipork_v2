package com.example.gestipork_v2.modelo;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.util.List;

public class ContarAdapter extends RecyclerView.Adapter<ContarAdapter.ViewHolder> {

    private final List<Conteo> lista;

    public ContarAdapter(List<Conteo> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Obtener contexto desde parent
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_conteo, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conteo conteo = lista.get(position);
        holder.txtNumero.setText(conteo.getnAnimales() + " animales");

        if (conteo.getObservaciones() != null && !conteo.getObservaciones().isEmpty()) {
            holder.txtObservaciones.setText(conteo.getObservaciones());
            holder.txtObservaciones.setVisibility(View.VISIBLE);
        } else {
            holder.txtObservaciones.setVisibility(View.GONE);
        }

        holder.txtFecha.setText("Fecha: " + conteo.getFecha());

        holder.imgDelete.setOnClickListener(v -> new AlertDialog.Builder(holder.context)
                .setTitle("Eliminar conteo")
                .setMessage("¿Deseas eliminar este registro de conteo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DBHelper dbHelper = new DBHelper(holder.context);
                    Conteo conteoDB = dbHelper.obtenerConteoPorId(conteo.getId()); // debes tener este método

                    if (conteoDB != null) {
                        String fechaEliminado = new java.text.SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss",
                                java.util.Locale.getDefault()
                        ).format(new java.util.Date());

                        boolean hayConexion = com.example.gestipork_v2.network.SupabaseConfig.hayConexionInternet(holder.context);

                        // Borrar de SQLite
                        dbHelper.eliminarConteoLocalmente(conteo.getId()); // este método debe hacer el delete y update en local

                        if (hayConexion) {
                            new com.example.gestipork_v2.sync.SincronizadorEliminaciones(holder.context)
                                    .sincronizarEliminacionInmediata("contar", conteo.getId(), fechaEliminado);
                        } else {
                            new com.example.gestipork_v2.repository.EliminacionRepository(holder.context)
                                    .insertarEliminacionPendiente(conteo.getId(), "contar", fechaEliminado);
                        }

                        if (holder.context instanceof ContarActivity) {
                            ((ContarActivity) holder.context).recargarLista();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show());
    }


    @Override
        public int getItemCount () {
            return lista.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtNumero, txtObservaciones, txtFecha;
            ImageView imgDelete;
            Context context;

            public ViewHolder(@NonNull View itemView, Context context) {
                super(itemView);
                this.context = context;
                txtNumero = itemView.findViewById(R.id.txtNumero);
                txtObservaciones = itemView.findViewById(R.id.txtObservaciones);
                txtFecha = itemView.findViewById(R.id.txtFecha);
                imgDelete = itemView.findViewById(R.id.imgDelete);
            }
        }

    }