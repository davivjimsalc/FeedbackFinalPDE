package com.example.feedbackfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MedirAceleracionActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private TextView tvAceleracionActual, tvAceleracionMedia;
    private Button btnVolver;
    private boolean enMovimiento = false;

    private float sumaX = 0, sumaY = 0, sumaZ = 0;
    private int contador = 0;
    private long tiempoInicioMovimiento = 0;

    private AppDatabase database;
    private MovimientoDao movimientoDao;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medir_aceleracion);

        tvAceleracionActual = findViewById(R.id.tvAceleracionActual);
        tvAceleracionMedia = findViewById(R.id.tvAceleracionMedia);
        btnVolver = findViewById(R.id.btnVolverAtras);

        // Inicializar el sensor y la base de datos
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "productos-db")
                .allowMainThreadQueries()
                .build();
        movimientoDao = database.movimientoDao();

        btnVolver.setOnClickListener(v -> finish());

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configurar preferencias y listener
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        aplicarPreferencias();
        preferenceChangeListener = (sharedPrefs, key) -> aplicarPreferencias();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Button btnCompartir = findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(v -> compartirResultados());

        Button btnEstadisticas = findViewById(R.id.btnEstadisticas);
        btnEstadisticas.setOnClickListener(v -> {
            Intent intent = new Intent(MedirAceleracionActivity.this, EstadisticasAceleracionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent servicioIntent = new Intent(this, MovimientoService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(servicioIntent);
        } else {
            startService(servicioIntent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
        aplicarPreferencias();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float aceleracionTotal = (float) Math.sqrt(x * x + y * y + z * z);

        tvAceleracionActual.setText(String.format("Aceleracion actual: %.2f m/s²", aceleracionTotal));

        if (aceleracionTotal > 0.5) { // Movimiento detectado
            if (!enMovimiento) {
                enMovimiento = true;
                tiempoInicioMovimiento = System.currentTimeMillis();
            }
            sumaX += Math.abs(x);
            sumaY += Math.abs(y);
            sumaZ += Math.abs(z);
            contador++;
        } else if (enMovimiento) { // Dispositivo detenido
            enMovimiento = false;

            long tiempoFinMovimiento = System.currentTimeMillis();
            long duracionMovimiento = tiempoFinMovimiento - tiempoInicioMovimiento;

            float mediaX = sumaX / contador;
            float mediaY = sumaY / contador;
            float mediaZ = sumaZ / contador;

            tvAceleracionMedia.setText(String.format("Aceleracion media:\nX: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²", mediaX, mediaY, mediaZ));

            // Guardar datos en la base de datos
            MovimientoEntity movimiento = new MovimientoEntity();
            movimiento.setTimestampInicio(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(tiempoInicioMovimiento)));
            movimiento.setDuracionMs(duracionMovimiento);
            movimiento.setAceleracionMediaX(mediaX);
            movimiento.setAceleracionMediaY(mediaY);
            movimiento.setAceleracionMediaZ(mediaZ);

            movimientoDao.insertarMovimiento(movimiento);

            // Resetear valores
            sumaX = 0;
            sumaY = 0;
            sumaZ = 0;
            contador = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_preferencias) {
            Intent intent = new Intent(this, PreferenciasActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void aplicarPreferencias() {
        // Leer preferencias
        String color = sharedPreferences.getString("color_datos", "black");
        int tamañoTexto = sharedPreferences.getInt("tamaño_texto", 18);

        // Aplicar los cambios en los TextViews
        tvAceleracionActual.setTextColor(Color.parseColor(color));
        tvAceleracionActual.setTextSize(tamañoTexto);
        tvAceleracionMedia.setTextColor(Color.parseColor(color));
        tvAceleracionMedia.setTextSize(tamañoTexto);
    }

    private void compartirResultados() {
        String mensaje = String.format("Resultados de aceleracion:\n" + "Aceleracion actual: %.2f m/s²\n" + "Aceleracion media: X: %.2f m/s², Y: %.2f m/s², Z: %.2f m/s²", 0.01, 0.00, 0.00, 0.00 );

        Intent compartirIntent = new Intent(Intent.ACTION_SEND);
        compartirIntent.setType("text/plain");
        compartirIntent.putExtra(Intent.EXTRA_SUBJECT, "Resultados de aceleracion");
        compartirIntent.putExtra(Intent.EXTRA_TEXT, mensaje);

        startActivity(Intent.createChooser(compartirIntent, "Compartir con..."));
    }

}
