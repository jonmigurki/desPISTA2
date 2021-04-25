package com.example.despistados2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    //Cuando generamos una instancia de esta clase, le pasamos también el contexto de la actividad
    //desde donde se genera, para así poder regresar a ella una vez hecha la conexión a la BD remota

    public void realizarConexion(String funcion, HashMap<String, String> parametros) {

        //Le pasamos los parámetros que necesitaremos para las distintas conexiones,
        //y la función que debe realizar (identificación, comprobación, registro, mostrado de puntos y monedas, etc.)


        this.funcion = funcion;
        String usuario, contrasena, nombre, apellidos, token, puntos, monedas, imagen, categoria, nivel, pistasUtilizadas;

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

                usuario = parametros.get("usuario");

                listarUsuarios(usuario);

                break;

            case "mostrarpuntosmonedas":

                usuario = parametros.get("usuario");

                mostrarPuntosMonedas(usuario);

                break;


            case "mostrardatosusuario":

                usuario = parametros.get("usuario");

                mostrarDatosUsuario(usuario);

                break;


            case "mostrarimagenusuario":

                usuario = parametros.get("usuario");

                mostrarImagenUsuario(usuario);

                break;

            case "actualizarPistasUtilizadas":

                usuario = parametros.get("usuario");
                categoria = parametros.get("categoria");
                nivel = parametros.get("nivel");
                pistasUtilizadas = parametros.get("pistasUtilizadas");

                actualizarPistasUtilizadas(usuario, categoria, nivel, pistasUtilizadas);

                break;

            case "actualizarNivelResuelto":

                usuario = parametros.get("usuario");
                categoria = parametros.get("categoria");
                nivel = parametros.get("nivel");
                puntos = parametros.get("puntos");
                monedas = parametros.get("monedas");

                actualizarNivelResuelto(usuario, categoria, nivel, puntos, monedas);

                break;

            case "actualizarRestarPuntos":

                usuario = parametros.get("usuario");
                puntos = parametros.get("puntos");

                actualizarRestarPuntos(usuario, puntos);

                break;


            case "mostrarPistas":

                usuario = parametros.get("usuario");
                categoria = parametros.get("categoria");
                nivel = parametros.get("nivel");

                mostrarPistas(usuario, categoria, nivel);

                break;


            case "mostrarResuelto":

                usuario = parametros.get("usuario");
                categoria = parametros.get("categoria");
                nivel = parametros.get("nivel");

                mostrarResuelto(usuario, categoria, nivel);

                break;

            case "enviarDinero":

                usuario = parametros.get("usuario");
                token = parametros.get("token");
                monedas = parametros.get("monedas");

                enviarDinero(usuario, token, monedas);

                break;

            case "actualizarDatosUsuario":

                usuario = parametros.get("usuario");
                contrasena = parametros.get("contrasena");
                nombre = parametros.get("nombre");
                apellidos = parametros.get("apellidos");
                imagen = parametros.get("imagen");

                actualizarDatosUsuario(usuario, contrasena, nombre, apellidos, imagen);

                break;

        }


    }



    /*
    Le especificamos la url con toda la información que necesitará (conexiones GET)
     */

    private void identificarUsuario(String usuario, String contrasena) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=false";

        new AsyncGET().execute(url);

    }

    private void registrarUsuario(String usuario, String contrasena, String nombre, String apellidos, String token) {

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/registrar.php?usuario=" + usuario + "&contrasena=" +
                contrasena + "&nombre=" + nombre + "&apellidos=" + apellidos + "&token=" + token;

        new AsyncGET().execute(url);

    }

    private void comprobarUsuario(String usuario, String contrasena){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/identificar.php?usuario=" + usuario + "&contrasena=" + contrasena + "&comprobar=true";

        new AsyncGET().execute(url);

    }


    private void enviarMensaje(String token, String monedas, String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/enviarmensaje.php?usuario=" + usuario + "&monedas=" + monedas + "&token=" + token;

        Log.d("TOKEN", token);

        new AsyncGET().execute(url);

    }


    private void listarUsuarios(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/listarusuarios.php?usuario=" + usuario;

        Log.d("URL", url);

        new AsyncGET().execute(url);


    }


    private void mostrarPuntosMonedas(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario + "&mostrar=puntosmonedas";

        new AsyncGET().execute(url);
    }



    private void mostrarDatosUsuario(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario + "&mostrar=datos";

        new AsyncGET().execute(url);

    }


    private void mostrarImagenUsuario(String usuario){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario + "&mostrar=imagen";

        new AsyncGET().execute(url);


    }

    private void actualizarPistasUtilizadas(String usuario, String categoria, String nivel, String pistasUtilizadas){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&categoria=" + categoria +
                "&nivel=" + nivel + "&pistasutilizadas=" + pistasUtilizadas + "&actualizar=pistas";

        new AsyncGET().execute(url);



    }

    private void actualizarNivelResuelto(String usuario, String categoria, String nivel, String puntos, String monedas){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&categoria=" + categoria +
                "&nivel=" + nivel + "&puntos=" + puntos + "&monedas=" + monedas + "&actualizar=resuelto";

        new AsyncGET().execute(url);


    }

    private void actualizarRestarPuntos(String usuario, String puntos){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&puntos=" + puntos +
               "&actualizar=restarpuntos";

        new AsyncGET().execute(url);


    }


    private void mostrarPistas(String usuario, String categoria, String nivel){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario + "&categoria=" + categoria +
                "&nivel=" + nivel + "&mostrar=pistas";

        new AsyncGET().execute(url);




    }


    private void mostrarResuelto(String usuario, String categoria, String nivel){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/mostrar.php?usuario=" + usuario + "&categoria=" + categoria +
                "&nivel=" + nivel + "&mostrar=resuelto";

        new AsyncGET().execute(url);




    }


    private void enviarDinero(String usuario, String token, String monedas){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&token=" + token +
                "&monedas=" + monedas + "&actualizar=enviardinero";

        new AsyncGET().execute(url);





    }


    private void actualizarDatosUsuario(String usuario, String contrasena, String nombre, String apellidos, String imagen){

        String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/jmiguel013/WEB/actualizar.php?usuario=" + usuario + "&contrasena=" + contrasena +
                "&nombre=" + nombre + "&apellidos=" + apellidos + "&imagen=" + imagen + "&actualizar=datosusuario";

        new AsyncGET().execute(url);


    }



    /*
            Basado en el código extraído de Github
        https://github.com/androidcss/login-system-with-android-php-and-mysql-using-httpurlconnection/blob/master/android/MyApplication/app/src/main/java/com/guru/login/MainActivity.java
            Autor: https://github.com/gururajkharvi
            Modificado por Jon Miguel para adaptar las funcionalidades y mensajes deseados
        */


    public class AsyncGET extends AsyncTask<String, String, String> {


        ProgressDialog pd = new ProgressDialog(context);
        HttpURLConnection conn;
        URL url = null;



        //Método que se ejecuta mientras está realizándose la conexión

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.setMessage("Cargando...");
            pd.setCancelable(false);
            pd.show();

        }


        //Conexión a la BD
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


        //Una vez finalizada la conexión, recogemos el resultado y hacemos las llamadas
        //a las actividades desde donde provenía la llamada para finalizar con las ejecuciones
        protected void onPostExecute(String result) {

            if(!result.equals("")){
                Log.d("FUNCION " + funcion, result);
            }

            pd.dismiss();

            //Obtenemos la clase del contexto
            String clase = context.getClass().toString();

            switch (clase) {
                case "class com.example.despistados2.MainActivity":
                    MainActivity m = (MainActivity) context;
                    m.ejecutarResultadoIdentificacion(result);
                    break;

                case "class com.example.despistados2.Registro":
                    Log.d("REGISTROOO", "REGISTROOO");
                    Log.d("resultadooo", result);
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
                    }else if(funcion.equals("firebase")){
                        menu.mostrarPuntosYMonedas();
                    }

                    break;


                case "class com.example.despistados2.Nivel":
                    Nivel nivel = (Nivel) context;

                    if(funcion.equals("mostrarpuntosmonedas")){
                        nivel.ejecutarResultadoMostradoPuntosMonedas(result);
                    }

                    break;

                case "class com.example.despistados2.Adivinanza":
                    Adivinanza adivinanza = (Adivinanza) context;

                    if(funcion.equals("mostrarpuntosmonedas")){
                        adivinanza.ejecutarResultadoMostradoPuntosMonedas(result);
                    }else if(funcion.equals("mostrarPistas")){
                        adivinanza.ejecutarResultadoMostradoPistas(result);
                    }else if(funcion.equals("mostrarResuelto")){
                        adivinanza.ejecutarResultadoMostradoResuelto(result);
                    }

                    break;

                case "class com.example.despistados2.Modificar":

                    Modificar modificar = (Modificar) context;

                    if(funcion.equals("mostrardatosusuario")){
                        modificar.ejecutarResultadoMostradoDatos(result);
                    }

                    break;



            }



        }
    }


}
