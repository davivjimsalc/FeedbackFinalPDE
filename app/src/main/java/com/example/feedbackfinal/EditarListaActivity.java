package com.example.feedbackfinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditarListaActivity extends AppCompatActivity {


    private EditText etNombreAñadir, etDescripcion, etNombre, etDescripcionEditar, etNombreEliminar;
    private ImageView imgSeleccionada, imgProductoEditar;
    private int idImagenDrawable = 0;
    private ArrayList<String> nombresProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_lista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etNombreAñadir = findViewById(R.id.etNombreAñadir);
        etDescripcion = findViewById(R.id.etDescripcionAñadir);
        imgSeleccionada = findViewById(R.id.imgSeleccionada);
        etNombre = findViewById(R.id.etNombreEditar);
        etDescripcionEditar = findViewById(R.id.etDescripcionEditar);
        etNombreEliminar = findViewById(R.id.etNombreEliminar);
        imgProductoEditar = findViewById(R.id.imgProductoEditar);

        nombresProductos = getIntent().getStringArrayListExtra("nombresProductos");
    }

    public void volverAtras(View view) {
        finish();
    }

    public void seleccionarImagenDesdeDrawable(View view) {
        final List<String> nombresImagenes = new ArrayList<>();
        final List<Integer> idsImagenes = new ArrayList<>();

        Field[] drawables = R.drawable.class.getFields();
        for (Field field : drawables) {
            try {
                String nombre = field.getName();
                if (!nombre.startsWith("ic_launcher")) {
                    nombresImagenes.add(nombre);
                    idsImagenes.add(field.getInt(null));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        final String[] arrayNombresImagenes = nombresImagenes.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una imagen");
        builder.setItems(arrayNombresImagenes, (dialog, which) -> {
            idImagenDrawable = idsImagenes.get(which);

            // Decidir que ImageView actualizar según el botón presionado
            if (view.getId() == R.id.btnSeleccionarImagen) {
                imgSeleccionada.setImageResource(idImagenDrawable);
            } else if (view.getId() == R.id.btnSeleccionarImagenEditar) {
                imgProductoEditar.setImageResource(idImagenDrawable);
            }

            Toast.makeText(this, "Imagen seleccionada: " + arrayNombresImagenes[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }



    public void añadirProducto(View view) {
        String nombre = etNombreAñadir.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() ||  idImagenDrawable == 0) {
            Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear intent para devolver los datos a MainActivity
        Intent intent = new Intent();
        intent.putExtra("accion", "añadir");
        intent.putExtra("nombre", nombre);
        intent.putExtra("descripcion", descripcion);

        intent.putExtra("idImagenDrawable", idImagenDrawable);


        setResult(RESULT_OK, intent);
        finish();
    }


    public void guardarCambiosProducto(View view) {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcionEditar.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || idImagenDrawable == 0) {
            Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("accion", "editar");
        intent.putExtra("nombreProducto", nombre);
        intent.putExtra("nuevaDescripcion", descripcion);

        intent.putExtra("idImagenDrawable", idImagenDrawable);

        setResult(RESULT_OK, intent);
        finish();
    }



    private boolean buscarProductoPorNombreInterno(String nombreBuscado) {
        return nombresProductos != null && nombresProductos.contains(nombreBuscado);
    }

    public void eliminarProducto(View view) {
        String nombre = etNombreEliminar.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Introduce el nombre del producto a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!buscarProductoPorNombreInterno(nombre)) {
            Toast.makeText(this, "El producto '" + nombre + "' no existe y no se puede eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("accion", "eliminar");
        intent.putExtra("nombreProducto", nombre);

        setResult(RESULT_OK, intent);
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
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}