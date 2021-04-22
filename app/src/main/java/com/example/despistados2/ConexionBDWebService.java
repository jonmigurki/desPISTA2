package com.example.despistados2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

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
        String usuario, contrasena, nombre, apellidos, token, monedas, imagen;

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
                token = parametros.get("token");

                registrarUsuario(usuario, contrasena, nombre, apellidos, token);

                break;

            case "comprobacion":

                usuario = parametros.get("usuario");
                contrasena = parametros.get("contrasena");

                comprobarUsuario(usuario, contrasena);

                break;


            case "firebase":

                token = parametros.get("token");
                monedas = parametros.get("monedas");
                usuario = parametros.get("usuario");

                enviarMensaje(token, monedas, usuario);

                break;

            case "listarusuarios":

                usuario = parametros.get("usuarios");

                listarUsuarios(usuario);

                break;

            case "mostrarpuntosmonedas":

                usuario = parametros.get("usuario");

                mostrarPuntosMonedas(usuario);

                break;

            case "actualizar":

                usuario = parametros.get("usuario");
                imagen = parametros.get("imagen");

                actualizarDatos(usuario, imagen);
        }


    }


    private void identificarUsuario(String usuario, String contrasena) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=false";

        new AsyncLogin().execute(url);

    }

    private void registrarUsuario(String usuario, String contrasena, String nombre, String apellidos, String token) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/registrar.php?usuario=" + usuario + "&contrasena=" +
                contrasena + "&nombre=" + nombre + "&apellidos=" + apellidos + "&token=" + token;

        new AsyncLogin().execute(url);

    }

    private void comprobarUsuario(String usuario, String contrasena){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=true";

        new AsyncLogin().execute(url);

    }


    private void enviarMensaje(String token, String monedas, String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/enviarmensaje.php?usuario=" + usuario + "&monedas=" + monedas + "&token=" + token;

        Log.d("TOKEN", token);

        new AsyncLogin().execute(url);

    }


    private void listarUsuarios(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/listarusuarios.php?usuario=" + usuario;

        Log.d("URL", url);

        new AsyncLogin().execute(url);


    }


    private void mostrarPuntosMonedas(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario;

        new AsyncLogin().execute(url);
    }


    private void actualizarDatos(String usuario, String imagen){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&imagen=" + imagen;

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
                        r.ejecutarResultadoRegistro(result.toString());
                    }
                    break;

                case "class com.example.despistados2.Menu":
                    Menu menu = (Menu) context;

                    if(funcion.equals("listarusuarios")) {
                        try {
                            menu.ejecutarResultadoListadoUsuarios(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(funcion.equals("mostrarpuntosmonedas")){
                        menu.ejecutarResultadoMostradoPuntosMonedas(result);
                    }

                    break;


            }



        }
    }


}
