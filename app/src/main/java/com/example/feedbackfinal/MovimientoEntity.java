package com.example.feedbackfinal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MovimientoEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "timestamp_inicio")
    private String timestampInicio;

    @ColumnInfo(name = "duracion_ms")
    private long duracionMs;

    @ColumnInfo(name = "aceleracion_media_x")
    private float aceleracionMediaX;

    @ColumnInfo(name = "aceleracion_media_y")
    private float aceleracionMediaY;

    @ColumnInfo(name = "aceleracion_media_z")
    private float aceleracionMediaZ;

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestampInicio() {
        return timestampInicio;
    }

    public void setTimestampInicio(String timestampInicio) {
        this.timestampInicio = timestampInicio;
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(long duracionMs) {
        this.duracionMs = duracionMs;
    }

    public float getAceleracionMediaX() {
        return aceleracionMediaX;
    }

    public void setAceleracionMediaX(float aceleracionMediaX) {
        this.aceleracionMediaX = aceleracionMediaX;
    }

    public float getAceleracionMediaY() {
        return aceleracionMediaY;
    }

    public void setAceleracionMediaY(float aceleracionMediaY) {
        this.aceleracionMediaY = aceleracionMediaY;
    }

    public float getAceleracionMediaZ() {
        return aceleracionMediaZ;
    }

    public void setAceleracionMediaZ(float aceleracionMediaZ) {
        this.aceleracionMediaZ = aceleracionMediaZ;
    }
}
