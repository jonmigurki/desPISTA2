package com.example.despistados2;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class Adivinanza extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana de adivinar

    //Los elementos del layout
    TextView puntos, monedas, categoria, nivel, usuarioI;
    Button atras, pista, resolver, comprobar;
    EditText respuesta;

    //Contexto de la aplicación
    Context context;

    //Variables que guardan la información acerca del usuario identificado, la categoría escogida, el id de la categoría escogida,
    // el nivel escogido, el id del nivel escogido y el número de niveles que se encuentran en la categoría, respectivamente
    String usuario, cat, num_cat, niv, num_niv, num_niveles;

    //Pistas que el usuario ha utilizado -> al comienzo 1
    int pistasUtilizadas = 1;

    //Lista de pistas que el usuario ha abierto hasta el momento
    String[] pistasAbiertas;
    //Lista de pistas totales que ese nivel tiene
    String[] pistas;

    //Puntos y monedas actuales del usuario
    int puntosUsuario;
    int monedasUsuario;

    //El usuario ya ha resuelto este nivel?
    boolean resuelto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Antes de que se cargue el layout de la actividad, obtenemos la Localizacion de
        // la aplicación para saber en qué idioma escribir los botones y textos
        Locale nuevaloc = new Locale(Idioma.locale);
        Locale.setDefault(nuevaloc);

        Configuration configuration =
                getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context2 = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration,context2.getResources().getDisplayMetrics());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adivinanza);

        //Obtenemos el contexto de la aplicación y lo actualizamos a la variable global
        context = this.getApplicationContext();

        //Obtenemos los elementos del layout y los inicializamos
        puntos = (TextView) findViewById(R.id.txtPuntos1);
        monedas = (TextView) findViewById(R.id.txtMonedas1);
        categoria = (TextView) findViewById(R.id.txtCategoria);
        nivel = (TextView) findViewById(R.id.txtNivel);

        atras = (Button) findViewById(R.id.btnAtras);
        pista = (Button) findViewById(R.id.btnPista);
        resolver = (Button) findViewById(R.id.btnResolver);
        comprobar = (Button) findViewById(R.id.btnComprobar);

        respuesta = (EditText) findViewById(R.id.txtRespuesta);

        //Obtenemos la información proveniente de la anterior actividad
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            cat = extras.getString("categoria");
            num_cat = extras.getString("num_categoria");
            niv = extras.getString("nivel");
            num_niv = extras.getString("num_nivel");
            num_niveles = extras.getString("num_niveles");
        }

        //Escribimos la categoría y nivel escogidos en el layout
        categoria.setText(cat.toString());

        if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {

            nivel.setText("Nivel " + num_niv.toString());

        } else {
            nivel.setText("Level " + num_niv.toString());
        }

        //Y escribimos el usuario identificado y mostramos los puntos y monedas de éste
        usuarioI = (TextView) findViewById(R.id.txtIdentificado);
        usuarioI.setText(usuario);
        mostrarPuntosYMonedas();

        //Comprobamos si este nivel está resuelto o no y actualizamos el booleano a la variable global
        resuelto();

        //Método que se encarga de comprobar cuántas pistas están abiertas en este nivel
        obtenerDatosUsuario();

        //Cargamos las pistas
        pistas = obtenerPistas();

        //Cargamos las pistas abiertas hasta el momento
        pistasAbiertas = actualizarListaPistas();

        //Accedemos al ListView y creamos el adaptador que visualizará las pistas cargadas
        ListView lista = (ListView) findViewById(R.id.lista3);
        AdaptadorPistas eladap = new AdaptadorPistas(getApplicationContext(), pistasAbiertas);
        lista.setAdapter(eladap);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Si el usuario pulsa el elemento 4 del ListView (pista nº 5 => Imagen pista) se crea un AlertDialog mostrando la imagen
                if (position == 4) {

                    /*
                        Basado en el código extraído de Stack Overflow
                        Pregunta: https://stackoverflow.com/questions/6276501/how-to-put-an-image-in-an-alertdialog-android
                        Autor: https://stackoverflow.com/users/2160667/miguel-rivero
                        Modificado por Jon Miguel para incluir los textos deseados al AlertDialog
                     */

                    /*
                        Basado en el código extraído de Stack Overflow
                        Pregunta: https://stackoverflow.com/questions/45521227/is-it-possible-to-access-r-drawable-variables-dynamically
                        Autor: https://stackoverflow.com/users/5131801/mohammadreza-eram
                        Modificado por Jon Miguel para obtener los resources de imagenes en drawable
                     */

                    ImageView image = new ImageView(Adivinanza.this);
                    String i = "imagen" + num_cat + num_niv;
                    int im = getResources().getIdentifier(i, "drawable", context.getPackageName());
                    image.setImageResource(im);

                    String m = "";
                    if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                        m = "Imagen de la pista";
                    } else {
                        m = "Clue image";
                    }


                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(Adivinanza.this).
                                    setMessage(m).
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    builder.create().show();


                }
            }
        });


        //Si el usuario pulsa el botón "Atrás" se vuelve a la anterior actividad
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Adivinanza.this, Nivel.class);
                i.putExtra("categoria", cat);
                i.putExtra("usuario", usuario);
                i.putExtra("num_categoria", num_cat);

                startActivity(i);
                finish();
            }
        });


        //Si el usuario pulsa el botón "Resolver" comprueba cuántas monedas le quedan. Si puede resolver por 20 monedas,
        //aparece un AlertDialog preguntando por confirmación. Si el usuario pulsa sí se resuelve el nivel, y si pulsa no se cierra.
        resolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!resuelto) {
                    if (monedasUsuario >= 20) {
                        //Bajar teclado
                        View view = Adivinanza.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        String m1 = "";
                        String m2 = "";
                        String m3 = "";

                        if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                            m1 = "Resolver";
                            m2 = "¿Estás segur@ de que quieres resolver? Te costará 20 monedas.";
                            m3 = "Sí";
                        } else {
                            m1 = "Solve";
                            m2 = "Are you sure you want to solve it? It will cost you 20 coins.";
                            m3 = "Yes";
                        }


                        //Mostramos el dialog indicando que el usuario ha fallado
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Adivinanza.this);
                        alertdialog.setTitle(m1);
                        alertdialog.setMessage(m2);
                        alertdialog.setPositiveButton(m3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                resolver();
                                mostrarNotificacion();

                            }
                        });

                        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        alertdialog.show();

                    } else {

                        String m = "";
                        if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                            m = "No tienes monedas suficientes para poder resolver. " +
                                    "Resuelve otros niveles para así conseguir más monedas";
                        } else {
                            m = "You don't have enough coins to solve it. " +
                                    "Finish other levels and earn more coins";
                        }

                        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    String m = "";
                    if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                        m = "Este nivel ya está resuelto";
                    } else {
                        m = "This level is already solved";
                    }

                    Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Se añade una nueva pista al ListView y actualizamos las variables pistasUtilizadas y pistasAbiertas
        pista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!resuelto) {
                    if (pistasUtilizadas == 5) {
                        //Crear toast

                        String m = "";
                        if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                            m = "Ya has abierto todas las pistas disponibles. " +
                                    "Piensa bien la respuesta, y si te rindes resuélvela pagando 20 monedas.";
                        } else {
                            m = "You have already unlocked all the available clues. " +
                                    "Think again the answer, and if you don't know it solve it paying 20 coins.";
                        }

                        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                    } else {
                        pistasUtilizadas++;
                        pistasAbiertas = actualizarListaPistas();
                        AdaptadorPistas adap = new AdaptadorPistas(getApplicationContext(), pistasAbiertas);
                        lista.setAdapter(adap);

                        //Actualizamos la base de datos para guardar el número de pistas utilizadas
                        BD GestorDB = new BD(context, "BD", null, 1);
                        SQLiteDatabase bd = GestorDB.getWritableDatabase();

                        //Miramos en la BD
                        bd.execSQL("UPDATE LOGROS SET PISTAS=" + pistasUtilizadas + " WHERE USUARIO = '" + usuario + "' AND " +
                                "CATEGORIA = '" + num_cat + "' AND NIVEL = '" + num_niv + "'");

                    }
                } else {

                    String m = "";
                    if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                        m = "Este nivel ya está resuelto";
                    } else {
                        m = "This level is already solved";
                    }

                    Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                }


            }
        });


        //Se comprueba que lo que ha escrito el usuario es la solución correcta
        comprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!resuelto) {
                    //Recogemos lo que el usuario ha escrito y comprobamos la respuesta
                    if (respuesta.getText().toString().equals("")) {

                        String m = "";
                        if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                            m = "Debes escribir algo para comprobar la respuesta." +
                                    "Pero asegúrate bien, que cada error te resta 2 puntos.";
                        } else {
                            m = "You must write something to check the answer. " +
                                    "But think about it carefully, each error takes 2 points away.";
                        }

                        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                    } else {

                        //Ignoramos las mayúsculas en la respuesta
                        if (respuesta.getText().toString().equalsIgnoreCase(niv)) {

                            //Bajamos el teclado del teléfono
                            View view = Adivinanza.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            //Calculamos los puntos
                            int pm = calcularPuntosMonedas();

                            String m1 = "";
                            String m2 = "";
                            if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                                m1 = "HAS ACERTADO";
                                m2 = "Enhorabuena!! Has ganado " + pm + " puntos y " + pm + " monedas";
                            } else {
                                m1 = "YOU GUESSED IT";
                                m2 = "Congratulations!! You earned " + pm + " points and " + pm + " coins";
                            }


                            //Mostramos el dialog indicando que el usuario ha acertado
                            AlertDialog.Builder alertdialog = new AlertDialog.Builder(Adivinanza.this);
                            alertdialog.setTitle(m1);
                            alertdialog.setMessage(m2);
                            alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            //Si el usuario quiere compartir el resultado
                            alertdialog.setNeutralButton("Compartir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT, "He adivinado el *nivel " + num_niv + "* de la categoría *" + cat + "* en desPISTAdos " +
                                            "y me han dado *" + pm + " puntos y monedas*!! Intenta superarme xd!");
                                    intent.setType("text/plain");
                                    intent.setPackage("com.whatsapp");
                                    startActivity(intent);

                                }
                            });


                            alertdialog.show();

                            //Actualizamos los puntos y monedas del usuario
                            puntosUsuario = puntosUsuario + pm;
                            monedasUsuario = monedasUsuario + pm;

                            //Las reescribimos en el layout
                            puntos.setText(String.valueOf(puntosUsuario));
                            monedas.setText(String.valueOf(monedasUsuario));

                            //Si los puntos <0 => rojo /// si >=0 => negro
                            actualizarColorPuntos();

                            BD GestorDB = new BD(context, "BD", null, 1);
                            SQLiteDatabase bd = GestorDB.getWritableDatabase();
                            bd.execSQL("UPDATE LOGROS SET RESUELTO=1 WHERE USUARIO = '" + usuario + "' AND " +
                                    "CATEGORIA = '" + num_cat + "' AND NIVEL = '" + num_niv + "'");

                            bd.execSQL("UPDATE USUARIOS SET PUNTOS=" + puntosUsuario + " WHERE USUARIO='" + usuario + "'");
                            bd.execSQL("UPDATE USUARIOS SET MONEDAS=" + monedasUsuario + " WHERE USUARIO='" + usuario + "'");

                            resuelto();

                            //Comprobamos que se hayan finalizado todos los niveles de la categoría elegida para mostrar la notificación
                            mostrarNotificacion();


                        } else {

                            //Bajar teclado
                            /*
                                Extraído de Stack Overflow
                                Pregunta: https://es.stackoverflow.com/questions/298/c%C3%B3mo-puedo-abrir-y-cerrar-el-teclado-virtual-soft-keyboard
                                Autor: https://es.stackoverflow.com/users/95/jorgesys
                             */
                            View view = Adivinanza.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            String m1 = "";
                            String m2 = "";
                            if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                                m1 = "HAS FALLADO";
                                m2 = "Lo siento! Prueba con otra cosa. (Recuerda que debes escribir las tildes correctamente).";
                            } else {
                                m1 = "YOU FAILED";
                                m2 = "I'm sorry! Try another thing.";
                            }


                            //Mostramos el dialog indicando que el usuario ha fallado
                            AlertDialog.Builder alertdialog = new AlertDialog.Builder(Adivinanza.this);
                            alertdialog.setTitle(m1);
                            alertdialog.setMessage(m2);
                            alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alertdialog.show();


                            puntosUsuario = puntosUsuario - 2;
                            puntos.setText(String.valueOf(puntosUsuario));

                            actualizarColorPuntos();

                            BD GestorDB = new BD(context, "BD", null, 1);
                            SQLiteDatabase bd = GestorDB.getWritableDatabase();

                            bd.execSQL("UPDATE USUARIOS SET PUNTOS=" + puntosUsuario + " WHERE USUARIO='" + usuario + "'");


                        }

                    }
                } else {

                    String m = "";
                    if (String.valueOf(getResources().getConfiguration().locale).contains("es")) {
                        m = "Este nivel ya está resuelto";
                    } else {
                        m = "This level is already solved";
                    }

                    Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    //Si los puntos son negativos, se visualizan en rojo
    private void actualizarColorPuntos(){

        if(puntosUsuario<0){
            puntos.setText(String.valueOf(puntosUsuario));
            puntos.setTextColor(Color.RED);

        }else{
            puntos.setTextColor(Color.BLACK);
        }


    }



    //Método privado que se encarga de mostrar los puntos y las monedas que el usuario tiene (se hace una consulta a la BD)
    private void mostrarPuntosYMonedas() {

        //Hacemos una consulta a la BD

        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT PUNTOS, MONEDAS FROM USUARIOS WHERE USUARIO = '" + usuario + "'", null);
        if (cursor.moveToFirst()) {
            int p = cursor.getInt(cursor.getColumnIndex("PUNTOS"));
            int m = cursor.getInt(cursor.getColumnIndex("MONEDAS"));

            puntosUsuario = p;
            monedasUsuario = m;

            puntos.setText(String.valueOf(p));
            monedas.setText(String.valueOf(m));
        }
        cursor.close();
        GestorDB.close();

    }


    //Si nunca se ha almacenado nada sobre este nivel (el usuario nunca ha entrado) se actualiza la base de datos
    //indicando que el número de pistas utilizadas para este nivel es 1 (ya se ha abierto una)
    private void obtenerDatosUsuario(){

        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT PISTAS FROM LOGROS WHERE USUARIO = '" + usuario + "' AND " +
                "CATEGORIA = '" + num_cat + "' AND NIVEL = '" + num_niv + "'", null);

        int cursorCount = cursor.getCount();

        if(cursorCount==0){         //El usuario nunca ha entrado en este nivel de esta categoría, por lo que hacemos un INSERT

            bd.execSQL("INSERT INTO LOGROS ('USUARIO', 'CATEGORIA', 'NIVEL', 'RESUELTO', 'PISTAS') VALUES ('" + usuario + "', " + Integer.valueOf(num_cat) +
                    ", " + Integer.valueOf(num_niv) + ", 0, 1)");

            pistasUtilizadas = 1;



        }else{

            if (cursor.moveToFirst()) {
                int pistas = cursor.getInt(cursor.getColumnIndex("PISTAS"));

                pistasUtilizadas = pistas;

            }

        }





        cursor.close();
        GestorDB.close();




    }


    //Se lee el fichero .txt y se obtienen las pistas en base a la categoría y el nivel elegido
    private String[] obtenerPistas(){

        InputStream is = this.getResources().openRawResource(R.raw.data_es);

        String idioma = String.valueOf(getResources().getConfiguration().getLocales().get(0));

        if (idioma.contains("en")) {
            is = this.getResources().openRawResource(R.raw.data_en);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String[] p = new String[5];


        try {
            String linea = reader.readLine();

            boolean pistasEncontradas = false;

            while(!pistasEncontradas){

                linea = reader.readLine();

                if(linea.equals(niv)){
                    pistasEncontradas=true;

                }

            }

            for(int x = 0; x < 5; x++){

                linea = reader.readLine();

                String[] l = linea.split(";");

                p[x]=l[1];


            }




        } catch (IOException e) {
            e.printStackTrace();
        }


        return p;



    }


    private String[] actualizarListaPistas(){

        String[] p = new String[pistasUtilizadas];

        for(int x=0; x < pistasUtilizadas; x++){

            p[x]=pistas[x];


        }

        return p;

    }


    //Método que se encarga de calcular los puntos y monedas para el usuario en base a las pistas utilizadas
    private int calcularPuntosMonedas(){

        switch(pistasUtilizadas) {

            case 1:
                return 10;

            case 2:
                return 5;

            case 3:
                return 3;

            case 4:
                return 2;

            case 5:
                return 1;

        }

        return 0;
    }





    private void resolver(){

        //Se le muestra un Dialog al usuario con la respuesta y se le restan 20 monedas

        //Bajar teclado
        View view = Adivinanza.this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Mostramos el dialog indicando que el usuario ha fallado
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(Adivinanza.this);

        String m1 = "";
        String m2 = "";
        if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
            m1 = "La respuesta correcta era...";
            m2 = "Qué lástima que no hayas podido adivinarlo. Seguro que el próximo nivel lo acertarás a la primera :D";
        }else{
            m1 = "The correct answer was...";
            m2 = "What a shame, you couldn't guess it. I'm sure you will rock next level :D";
        }

        alertdialog.setTitle(m1 + " " + niv);
        alertdialog.setMessage(m2);
        alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertdialog.show();


        monedasUsuario = monedasUsuario-20;
        monedas.setText(String.valueOf(monedasUsuario));


        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        bd.execSQL("UPDATE USUARIOS SET MONEDAS=" + monedasUsuario + " WHERE USUARIO='" + usuario + "'");

        bd.execSQL("UPDATE LOGROS SET RESUELTO=1 WHERE USUARIO = '" + usuario + "' AND " +
                "CATEGORIA = '" + num_cat + "' AND NIVEL = '" + num_niv + "'");


        resuelto();


    }


    private void resuelto(){


        //Hacemos una consulta a la BD

        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT RESUELTO FROM LOGROS WHERE USUARIO = '" + usuario + "' AND " +
                "CATEGORIA = '" + num_cat + "' AND NIVEL = '" + num_niv + "'", null);



        if (cursor.moveToFirst()) {
            int r = cursor.getInt(cursor.getColumnIndex("RESUELTO"));

            if(r==0){
                resuelto = false;
            }else{
                resuelto = true;
            }
        }

        cursor.close();
        GestorDB.close();


    }


    //Método que se va a encargar de mostrar una notificación si todos los niveles de una categoría han sido adivinados
    private void mostrarNotificacion(){

        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT * FROM LOGROS WHERE USUARIO = '" + usuario + "' AND CATEGORIA = '" + num_cat + "' AND RESUELTO=1", null);

        int cursorCount = cursor.getCount();
        cursor.close();
        GestorDB.close();

        if (cursorCount == Integer.valueOf(num_niveles)) {

            NotificationManager nm = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(context, "noti");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("noti", "noticategoria",
                        NotificationManager.IMPORTANCE_DEFAULT);

                nm.createNotificationChannel(elCanal);

                elCanal.setDescription("Categoría finalizada");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
            }

            elBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("desPISTAdos -- ¡ENHORABUENA!")
                    .setContentText("¡Has finalizado la categoría! Te regalamos 20 monedas.")
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);


            nm.notify(12345, elBuilder.build());


            monedasUsuario = monedasUsuario+20;

            GestorDB = new BD(context, "BD", null, 1);
            bd = GestorDB.getWritableDatabase();

            bd.execSQL("UPDATE USUARIOS SET MONEDAS=" + monedasUsuario + " WHERE USUARIO='" + usuario + "'");


            monedas.setText(String.valueOf(monedasUsuario));



        }



    }



    //Método que se encarga de visualizar un Dialog cuando el usuario le da al botón de atrás de su teléfono
    public void onBackPressed() {

        String texto1 = "";
        String texto2 = "";
        String texto3 = "";

        if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
            texto1 = "Salir";
            texto2 = "¿Estás segur@ de que quieres cerrar sesión?";
            texto3 = "Sí";
        }else{
            texto1 = "Exit";
            texto2 = "Are you sure you want to log out?";
            texto3 = "Yes";
        }

        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setTitle(texto1);
        alertdialog.setMessage(texto2);
        alertdialog.setPositiveButton(texto3, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //    Menu.super.onBackPressed();
                Intent i = new Intent(Adivinanza.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertdialog.show();

    }



}