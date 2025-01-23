package com.example.feedbackfinal;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MovimientoService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "movimiento_channel";
    private boolean enMovimiento = false;
    private long tiempoInicioMovimiento = 0;
    private boolean ejecutando = true;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();

        // Crear canal de notificaciones al inicio
        crearCanalNotificacion();

        // Iniciar servicio en primer plano con notificacion inicial
        Notification notification = crearNotificacion("Servicio en ejecucion", "Esperando movimiento...");
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Iniciar el hilo de verificacion de movimiento
        new Thread(() -> {
            while (ejecutando) {
                try {
                    Thread.sleep(5000); // Verificar cada 5 segundos
                    verificarMovimiento();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ejecutando = false; // Detener el hilo al destruir el servicio
    }

    private void verificarMovimiento() {
        if (!enMovimiento) {
            enMovimiento = true;
            tiempoInicioMovimiento = System.currentTimeMillis();
        }

        // Verificar duracion del movimiento
        long duracionMovimiento = System.currentTimeMillis() - tiempoInicioMovimiento;
        if (duracionMovimiento > 10000) { // Más de 10 segundos en movimiento
            enviarNotificacion("Movimiento detectado", "El dispositivo estuvo en movimiento por mas de 10 segundos.");
            tiempoInicioMovimiento = 0;
            enMovimiento = false;
        }
    }

    private void enviarNotificacion(String titulo, String mensaje) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = crearNotificacion(titulo, mensaje);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private Notification crearNotificacion(String titulo, String mensaje) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de movimiento",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
