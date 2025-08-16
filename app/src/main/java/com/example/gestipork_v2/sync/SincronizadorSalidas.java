package com.example.gestipork_v2.sync;

import android.content.Context;
import android.util.Log;

import com.example.gestipork_v2.base.FechaUtils;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.EliminacionPendiente;
import com.example.gestipork_v2.repository.EliminacionRepository;
import com.example.gestipork_v2.repository.SalidaRepository;

import java.util.List;

public class SincronizadorSalidas {

    private final Context context;
    private final SalidaRepository salidaRepository;
    private final EliminacionRepository eliminacionRepository;
    private final DBHelper dbHelper;
    private static final String TAG = "SincronizadorSalidas";

    public SincronizadorSalidas(Context context) {
        this.context = context;
        this.salidaRepository = new SalidaRepository(context);
        this.eliminacionRepository = new EliminacionRepository(context);
        this.dbHelper = new DBHelper(context);
    }

    public void sincronizar() {
        Log.d(TAG, "👉 Iniciando sincronización de SALIDAS...");

        // 1. Subir salidas no sincronizadas
        salidaRepository.subirSalidasNoSincronizadas();

        // 2. Descargar salidas nuevas o modificadas desde Supabase
        String ultimaFecha = dbHelper.obtenerUltimaFechaActualizacion("salidas");
        salidaRepository.descargarSalidasModificadas(ultimaFecha);

        // 3. Procesar eliminaciones pendientes
        List<EliminacionPendiente> eliminaciones = eliminacionRepository.obtenerPorTabla("salidas");

        for (EliminacionPendiente eliminacion : eliminaciones) {
            salidaRepository.marcarSalidaEliminadaEnSupabase(
                    eliminacion.getIdRegistro(),
                    eliminacion.getFechaEliminado()
            );
            eliminacionRepository.eliminarEliminacionPendiente(eliminacion.getId()); // ✅

        }

        Log.d(TAG, "✅ Sincronización de SALIDAS finalizada");
    }
}
