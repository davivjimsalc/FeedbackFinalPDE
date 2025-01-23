package com.example.feedbackfinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ProductoEntity> listaProductos;
    private AppDatabase database;
    private ProductoDao productoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar base de datos y DAO
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "productos-db")
                .allowMainThreadQueries()
                .build();
        productoDao = database.productoDao();

        // Cargar productos desde la base de datos
        listaProductos = new ArrayList<>(productoDao.obtenerTodosLosProductos());

        if (listaProductos.isEmpty()) {
            ProductoEntity producto1 = new ProductoEntity();
            producto1.setNombre("Nintendo Switch");
            producto1.setDetalles("Consola de Nintendo");
            producto1.setImagenResId(R.drawable.nintendo);

            ProductoEntity producto2 = new ProductoEntity();
            producto2.setNombre("PC Gamer");
            producto2.setDetalles("Ordenador para juegos");
            producto2.setImagenResId(R.drawable.pc);

            productoDao.insertarProducto(producto1);
            productoDao.insertarProducto(producto2);

            listaProductos.add(producto1);
            listaProductos.add(producto2);
        }


        actualizarListaProductos();

    }

    public void irAEditarLista(View view) {
        Intent intent = new Intent(MainActivity.this, EditarListaActivity.class);

        ArrayList<String> nombresProductos = new ArrayList<>();
        for (ProductoEntity producto : listaProductos) {
            nombresProductos.add(producto.getNombre());
        }

        intent.putStringArrayListExtra("nombresProductos", nombresProductos);
        startActivityForResult(intent, 1);
    }

    public void verDetallesProducto(View view) {
        int index = (int) view.getTag();

        ProductoEntity producto = listaProductos.get(index);

        Intent intent = new Intent(MainActivity.this, DetallesActivity.class);
        intent.putExtra("imagen", producto.getImagenResId());
        intent.putExtra("nombre", producto.getNombre());
        intent.putExtra("detalles", producto.getDetalles());

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String accion = data.getStringExtra("accion");

            if ("añadir".equals(accion)) {
                String nombre = data.getStringExtra("nombre");
                String descripcion = data.getStringExtra("descripcion");
                int imagenResId = data.getIntExtra("idImagenDrawable", 0);

                ProductoEntity nuevoProducto = new ProductoEntity();
                nuevoProducto.setNombre(nombre);
                nuevoProducto.setDetalles(descripcion);
                nuevoProducto.setImagenResId(imagenResId);

                productoDao.insertarProducto(nuevoProducto);
                listaProductos.add(nuevoProducto);

                actualizarListaProductos();
                Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
            }
            else if ("editar".equals(accion)) {
                String nombreProducto = data.getStringExtra("nombreProducto");
                String nuevaDescripcion = data.getStringExtra("nuevaDescripcion");
                int imagenResId = data.getIntExtra("idImagenDrawable", 0);


                ProductoEntity producto = productoDao.buscarProductoPorNombre(nombreProducto);
                if (producto != null) {
                    producto.setDetalles(nuevaDescripcion);
                    if (imagenResId != 0) {
                        producto.setImagenResId(imagenResId);
                        producto.setImagenUri(null);
                    }

                    productoDao.actualizarProducto(producto);

                    listaProductos.clear();
                    listaProductos.addAll(productoDao.obtenerTodosLosProductos());

                    actualizarListaProductos();
                    Toast.makeText(this, "Producto editado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            else if ("eliminar".equals(accion)) {
                String nombreProducto = data.getStringExtra("nombreProducto");

                ProductoEntity producto = productoDao.buscarProductoPorNombre(nombreProducto);
                if (producto != null) {
                    productoDao.eliminarProducto(producto);

                    listaProductos.clear();
                    listaProductos.addAll(productoDao.obtenerTodosLosProductos());

                    actualizarListaProductos();
                    Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No se realizo ninguna accion", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarListaProductos() {
        LinearLayout layoutProductos = findViewById(R.id.layoutProductos);
        layoutProductos.removeAllViews();

        if (listaProductos == null || listaProductos.isEmpty()) {
            Log.e("MainActivity", "La lista de productos esta vacia o es nula");
            Toast.makeText(this, "No hay productos para mostrar", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < listaProductos.size(); i++) {
            ProductoEntity producto = listaProductos.get(i);

            Log.d("MainActivity", "Procesando producto: " + producto.getNombre() + ", ImagenResId: " + producto.getImagenResId());

            View productoView = getLayoutInflater().inflate(R.layout.item_producto, layoutProductos, false);

            ImageView imgProducto = productoView.findViewById(R.id.imgProducto);
            TextView tvNombreProducto = productoView.findViewById(R.id.tvNombreProducto);
            Button btnDetalles = productoView.findViewById(R.id.btnDetalles);

            if (producto.getImagenResId() != 0) {
                imgProducto.setImageResource(producto.getImagenResId());
                Log.d("MainActivity", "Imagen asignada: " + producto.getImagenResId());
            }

            tvNombreProducto.setText(producto.getNombre());

            btnDetalles.setTag(i);
            btnDetalles.setOnClickListener(this::verDetallesProducto);

            layoutProductos.addView(productoView);
        }
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

    public void irAMedirAceleracion(View view) {
        Intent intent = new Intent(MainActivity.this, MedirAceleracionActivity.class);
        startActivity(intent);
    }
}