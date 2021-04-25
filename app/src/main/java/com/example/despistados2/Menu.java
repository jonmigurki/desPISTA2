package com.example.despistados2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana del menú (cateogrías)

    //Obtenemos los elementos del layout
    TextView usuario, puntos, monedas;
    Button compartir, enviarDinero, modificarDatos;

    Context context;

    String user;

    String token = "";

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


        //Cargo el token del dispositivo
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Log.d("ERROR EN FIREBASE", String.valueOf(task.getException()));
                    return;
                }

                token = task.getResult().getToken();
            }
        });


        //Cargamos las categorías
        String[] categorias = cargarCategorias();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //Obtenemos el usuario que se ha identificado
        String u = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            u = extras.getString("usuario");
        }

        //Hacemos que se visualicen el nombre del usuario, sus puntos y sus monedas en la ventana
        usuario = (TextView) findViewById(R.id.txtIdentificado);
        usuario.setText(u);
        user = u;
        puntos = (TextView) findViewById(R.id.txtPuntos1);
        monedas = (TextView) findViewById(R.id.txtMonedas1);
        mostrarPuntosYMonedas();



        //Establecemos el contexto de la aplicación
        context = this.getApplicationContext();

        compartir = (Button) findViewById(R.id.btnCompartir);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String texto = "";

                if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
                    texto = "Estoy jugando a desPISTAdos y es súper entretenido. ¡Corre y descárgatelo!";
                }else{
                    texto = "I'm playing desPISTAdos and it's super entertaining. Go and download it!";
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, texto);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                startActivity(intent);

            }
        });

        enviarDinero = (Button) findViewById(R.id.btnEnviarDinero);
        enviarDinero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(monedas.getText().toString().equals("0")){
                    Toast.makeText(Menu.this, "No tienes suficiente dinero para enviar a nadie", Toast.LENGTH_SHORT).show();
                }else{

                    getUsuariosJSON();


                }


/*
                Log.d("TOKEN", token.toString());
                Log.d("HOLA", "HOLAAAA");
                //Hacemos una conexión al servidor PHP
                ConexionBDWebService conexion = new ConexionBDWebService(Menu.this);
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("token", token);

                conexion.realizarConexion("firebase", hm);
*/
            }
        });


        modificarDatos = (Button) findViewById(R.id.btnModificarDatos);
        modificarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Modificar.class);
                i.putExtra("usuario", user);

                startActivity(i);
                finish();
            }
        });

        //Accedemos al ListView y creamos el adaptador que visualizará las categorías cargadas
        ListView lista = (ListView) findViewById(R.id.lista1);
        AdaptadorCategorias eladap= new AdaptadorCategorias(getApplicationContext(),categorias);
        lista.setAdapter(eladap);

        //Cuando el usuario seleccione una categoría, realizaremos un Intent explícito a una nueva ventana
        //para visualizarle los niveles disponibles en esa categoría. Además, necesitaremos pasarle el nombre
        //del usuario para recogerlo después y poder hacer los updates necesarios en la BD y guardar sus puntuaciones
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Menu.this, Nivel.class);
                i.putExtra("categoria", ((TextView)view.findViewById(R.id.etiqueta)).getText().toString());
                i.putExtra("usuario", user);
                i.putExtra("num_categoria", String.valueOf(position+1));

                startActivity(i);

                finish();

            }
        });

    }



    //Método privado que se encarga de cargar las categorías leyendo el fichero de texto
    private String[] cargarCategorias() {

        String linea;

        InputStream is = this.getResources().openRawResource(R.raw.data_es);

        String idioma = String.valueOf(getResources().getConfiguration().getLocales().get(0));


        if (idioma.contains("en")) {
            is = this.getResources().openRawResource(R.raw.data_en);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String[] c = null;

        //Leemos la primera línea de data.txt
        try {
            linea = reader.readLine();

            c = linea.split(";");


        } catch (IOException e) {
            e.printStackTrace();
        }


        return c;

    }


    //Método privado que se encarga de mostrar los puntos y las monedas que el usuario tiene (se hace una consulta a la BD)
    public void mostrarPuntosYMonedas() {

        //Hacemos una consulta a la BD
/*
        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT PUNTOS, MONEDAS FROM USUARIOS WHERE USUARIO = '" + user + "'", null);
        if (cursor.moveToFirst()) {
            int p = cursor.getInt(cursor.getColumnIndex("PUNTOS"));
            int m = cursor.getInt(cursor.getColumnIndex("MONEDAS"));

            puntos.setText(String.valueOf(p));
            monedas.setText(String.valueOf(m));
        }
        cursor.close();
        GestorDB.close();
*/

        ConexionBDWebService conexion = new ConexionBDWebService(Menu.this);
        HashMap<String, String> hm = new HashMap<String,String>();
        hm.put("usuario", user);

        conexion.realizarConexion("mostrarpuntosmonedas", hm);

    }


    public void getUsuariosJSON(){

        ConexionBDWebService conexion = new ConexionBDWebService(Menu.this);
        HashMap<String, String> hm = new HashMap<String,String>();
        hm.put("usuario", user);
        Log.d("USUARIO", user);
        conexion.realizarConexion("listarusuarios", hm);

    }


    public void ejecutarResultadoListadoUsuarios(String resultado) throws JSONException {

        //Decodificar los usuarios
        JSONArray jsonarray = new JSONArray(resultado);
        Log.d("json", jsonarray.get(0).toString());

        String[] listausuarios = new String[jsonarray.length()];
        String[] listatokens = new String[jsonarray.length()];

        for(int x = 0; x < jsonarray.length(); x++){
            JSONObject jsonusuario = (JSONObject) jsonarray.get(x);
            listausuarios[x] = (String) jsonusuario.get("USUARIO");
            listatokens[x] = (String) jsonusuario.get("TOKEN");
        }

        Log.d("LISTAUSUARIOS", listausuarios[0].toString());
        Log.d("LISTATOKENS", listatokens[0].toString());


        seleccionarUsuario(listausuarios, listatokens);

    }


    private void seleccionarUsuario(String[] listausuarios, String[] listatokens){

        AlertDialog.Builder b = new AlertDialog.Builder(Menu.this);
        b.setTitle("Elige un usuario para enviar dinero");

        b.setItems(listausuarios, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Toast.makeText(Menu.this, "Has seleccionado " + listausuarios[which], Toast.LENGTH_SHORT).show();

                seleccionarDinero(listausuarios[which], listatokens[which]);

            }

        });

        b.show();


    }


    private void seleccionarDinero(String usuario, String token){

        EditText inputEditTextField = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("¿Cuánto dinero le quieres enviar a " + usuario + "?")
                .setView(inputEditTextField)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String dinero = inputEditTextField.getText().toString();
                        //Toast.makeText(Menu.this, "Has querido mandar " + editTextInput, Toast.LENGTH_SHORT).show();
                        if(Integer.parseInt(dinero) > Integer.parseInt(monedas.getText().toString())){
                           //No tengo suficiente dinero para enviar
                           //Bajamos el teclado
                            View view = Menu.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }


                            Toast.makeText(Menu.this, "No puedes enviar tal cantidad", Toast.LENGTH_SHORT).show();
                        }else{

                            //Puedo mandar el dinero

                            ConexionBDWebService conexion = new ConexionBDWebService(Menu.this);
                            HashMap<String, String> hm = new HashMap<String,String>();
                            hm.put("token", token);
                            hm.put("monedas", dinero);
                            hm.put("usuario", user);

                            conexion.realizarConexion("firebase", hm);


                            ///////OTRA CONEXION A LA BASE DE DATOS REMOTA PARA ENVIAR DINERO ///////

                            conexion = new ConexionBDWebService(Menu.this);
                            hm = new HashMap<String, String>();
                            hm.put("usuario", user);
                            hm.put("token", token);
                            hm.put("monedas", dinero);

                            conexion.realizarConexion("enviarDinero", hm);

                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();

        mostrarPuntosYMonedas();

    }


    public void ejecutarResultadoMostradoPuntosMonedas(String resultado){

        try {
          //  JSONArray jsonarray = new JSONArray(resultado);

            JSONObject jsonpm = new JSONObject(resultado);

            String p = jsonpm.get("PUNTOS").toString();
            String m = jsonpm.get("MONEDAS").toString();

            puntos.setText(p);
            monedas.setText(m);


        } catch (JSONException e) {
            e.printStackTrace();
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
                Intent i = new Intent(Menu.this, MainActivity.class);
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