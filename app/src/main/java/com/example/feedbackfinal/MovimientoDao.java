package com.example.feedbackfinal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovimientoDao {

    @Insert
    void insertarMovimiento(MovimientoEntity movimiento);

    @Query("SELECT * FROM MovimientoEntity ORDER BY id DESC")
    List<MovimientoEntity> obtenerTodosLosMovimientos();
}
