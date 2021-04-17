package com.example.despistados2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ConexionBDWebService {

    Context context;
    String funcion;


    public ConexionBDWebService(Context c) {
        this.context = c;
    }


    public void realizarConexion(String funcion, HashMap<String, String> parametros) {

        this.funcion = funcion;
       // HashMap<String, String> hm = new HashMap<String, String>();
        String usuario, contrasena, nombre, apellidos;

        switch (funcion) {

            case "identificacion":

                usuario = parametros.get("usuario");
                contrasena = parametros.get("contrasena");

                identificarUsuario(usuario, contrasena);

                break;

            case "registro":

                usuario = parametros.get("usuario");
                contrasena = parametros.get("contrasena");
                nombre = parametros.get("nombre");
                apellidos = parametros.get("apellidos");

                registrarUsuario(usuario, contrasena, nombre, apellidos);

                break;

            case "comprobacion":

                usuario = parametros.get("usuario");
                contrasena = parametros.get("contrasena");

                comprobarUsuario(usuario, contrasena);

                break;
        }


    }


    private void identificarUsuario(String usuario, String contrasena) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=false";

        new AsyncLogin().execute(url);

    }

    private void registrarUsuario(String usuario, String contrasena, String nombre, String apellidos) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/registrar.php?usuario=" + usuario + "&contrasena=" +
                contrasena + "&nombre=" + nombre + "&apellidos=" + apellidos;

        new AsyncLogin().execute(url);

    }

    private void comprobarUsuario(String usuario, String contrasena){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=true";

        new AsyncLogin().execute(url);

    }



    /*
            Basado en el código extraído de Github
        https://github.com/androidcss/login-system-with-android-php-and-mysql-using-httpurlconnection/blob/master/android/MyApplication/app/src/main/java/com/guru/login/MainActivity.java
            Autor: https://github.com/gururajkharvi
            Modificado por Jon Miguel para adaptar las funcionalidades y mensajes deseados
        */


    public class AsyncLogin extends AsyncTask<String, String, String> {


        ProgressDialog pd = new ProgressDialog(context);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setMessage("Cargando...");
            pd.setCancelable(false);
            pd.show();

        }


        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();


            } catch (IOException e) {
                e.printStackTrace();
                return "exception 2";
            }

            try {
                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String message = in.readLine();
                    in.close();

                    return message;


                } else {
                    return ("unsuccessfull");
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "exception 1";
            } finally {
                conn.disconnect();
            }


        }


        protected void onPostExecute(String result) {

            Log.d("RESULTADO", result.toString());


            pd.dismiss();

            //Obtenemos la clase del contexto
            String clase = context.getClass().toString();

            switch (clase) {
                case "class com.example.despistados2.MainActivity":
                    MainActivity m = (MainActivity) context;
                    m.ejecutarResultadoIdentificacion(result);
                    break;
                case "class com.example.despistados2.Registro":
                    Registro r = (Registro) context;
                    if(funcion.equals("comprobacion")){
                        r.ejecutarResultadoComprobacion(result);
                    }else if(funcion.equals("registro")){
                        r.ejecutarResultadoRegistro(result);
                    }
                    break;
            }



        }
    }


}