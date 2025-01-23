package com.example.feedbackfinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.List;

public class EstadisticasAceleracionActivity extends AppCompatActivity {

    private TextView tvPromedio, tvMaxima, tvTiempoTotal;
    private Button btnVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_aceleracion);

        tvPromedio = findViewById(R.id.tvPromedio);
        tvMaxima = findViewById(R.id.tvMaxima);
        tvTiempoTotal = findViewById(R.id.tvTiempoTotal);
        btnVolver = findViewById(R.id.btnVolver);

        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "productos-db")
                .allowMainThreadQueries()
                .build();
        MovimientoDao movimientoDao = database.movimientoDao();

        List<MovimientoEntity> movimientos = movimientoDao.obtenerTodosLosMovimientos();

        if (!movimientos.isEmpty()) {
            calcularEstadisticas(movimientos);
        } else {
            tvPromedio.setText("No hay datos disponibles.");
        }


        btnVolver.setOnClickListener(v -> finish());
    }


    private void calcularEstadisticas(List<MovimientoEntity> movimientos) {
        float sumaAceleracion = 0;
        float maximaAceleracion = 0;
        long tiempoTotal = 0;

        for (MovimientoEntity movimiento : movimientos) {
            float promedioMovimiento = (movimiento.getAceleracionMediaX() + movimiento.getAceleracionMediaY() + movimiento.getAceleracionMediaZ()) / 3;
            sumaAceleracion += promedioMovimiento;

            float maximaMovimiento = Math.max(movimiento.getAceleracionMediaX(), Math.max(movimiento.getAceleracionMediaY(), movimiento.getAceleracionMediaZ()));
            maximaAceleracion = Math.max(maximaAceleracion, maximaMovimiento);

            tiempoTotal += movimiento.getDuracionMs();
        }

        float promedioAceleracion = sumaAceleracion / movimientos.size();

        // Mostrar resultados
        tvPromedio.setText(String.format("Promedio de aceleracion: %.2f m/s²", promedioAceleracion));
        tvMaxima.setText(String.format("Maxima aceleracion: %.2f m/s²", maximaAceleracion));
        tvTiempoTotal.setText(String.format("Tiempo total de movimiento: %d segundos", tiempoTotal / 1000));
    }
}
