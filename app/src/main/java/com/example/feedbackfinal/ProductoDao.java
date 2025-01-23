package com.example.feedbackfinal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert
    void insertarProducto(ProductoEntity producto);

    @Update
    void actualizarProducto(ProductoEntity producto);

    @Delete
    void eliminarProducto(ProductoEntity producto);

    @Query("SELECT * FROM ProductoEntity")
    List<ProductoEntity> obtenerTodosLosProductos();

    @Query("SELECT * FROM ProductoEntity WHERE nombre = :nombre LIMIT 1")
    ProductoEntity buscarProductoPorNombre(String nombre);
}
