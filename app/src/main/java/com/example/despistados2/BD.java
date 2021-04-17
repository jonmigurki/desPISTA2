package com.example.despistados2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BD extends SQLiteOpenHelper {

    //Clase que se encarga de crear la base de datos y las dos tablas 'USUARIOS' y 'LOGROS'

    public BD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE USUARIOS ('ID' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +     //Id 1, 2, 3, ...
                "'USUARIO' VARCHAR(255) NOT NULL, " +                                               //Nombre del usuario
                "'CONTRASENA' VARCHAR(255) NOT NULL, " +                                            //Contraseña del usuario
                "'PUNTOS' INT NOT NULL, " +                                                        //Puntos del usuario
                "'MONEDAS' INT NOT NULL)");                                                       //Monedas del usuario

       db.execSQL("CREATE TABLE LOGROS ('USUARIO' VARCHAR(255) NOT NULL," +                     //Nombre del usuario
               "'CATEGORIA' INTEGER NOT NULL," +                                                //Categoría: 1, 2, 3...
               "'NIVEL' INTEGER NOT NULL," +                                                    //Nivel: 1, 2, 3...
               "'RESUELTO' INTEGER NOT NULL," +                                             //Resuelto: 0 si false, 1 si true
               "'PISTAS' INTEGER NOT NULL)");                                           //Pistas que ha abierto: 1, 2, 3...5


        db.execSQL("INSERT INTO USUARIOS ('USUARIO', 'CONTRASENA', 'PUNTOS', 'MONEDAS') VALUES ('admin', '1234', 0, 50)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
