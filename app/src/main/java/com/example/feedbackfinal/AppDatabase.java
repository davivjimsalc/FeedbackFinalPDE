package com.example.feedbackfinal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProductoEntity.class, MovimientoEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductoDao productoDao();
    public abstract MovimientoDao movimientoDao();
}
