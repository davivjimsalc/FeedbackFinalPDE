package com.example.feedbackfinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetallesActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvNombre, tvDetalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView);
        tvNombre = findViewById(R.id.tvNombreProductoDetalles);
        tvDetalles = findViewById(R.id.tvDetallesProducto);

        int imagenResId = getIntent().getIntExtra("imagen", R.drawable.ic_launcher_foreground);
        String nombre = getIntent().getStringExtra("nombre");
        String detalles = getIntent().getStringExtra("detalles");

        imageView.setImageResource(imagenResId);
        tvNombre.setText(nombre);
        tvDetalles.setText(detalles);
    }

    public void volverAtras(View view) {
        finish();
    }

    public void mostrarAcercaDe(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acerca de")
                .setMessage("MiAplicación - Inventario de Productos\n" +
                        "Versión: 1.0.0\n\n" +
                        "Aplicación para gestionar productos, permitiendo añadir, editar y eliminar información.\n\n" +
                        "Desarrollado por: David Jimenez\n" +
                        "Contacto: dsalcjim@myuax.com\n")
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}