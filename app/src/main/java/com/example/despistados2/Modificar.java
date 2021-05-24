package com.example.despistados2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Modificar extends AppCompatActivity {


    Button btnSacarFoto, btnAtrasModificar, btnGuardarModificar;
    ImageView imageView;
    EditText txtNombre, txtApellidos, txtContrasena;

    String usuario;

    File file;
    Uri uri;


    String currentPhotoPath;

    StorageReference storageReference;

    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int CAMERA_PERM_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }


        btnSacarFoto = (Button) findViewById(R.id.btnSacarFoto);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnAtrasModificar = (Button) findViewById(R.id.btnAtrasModificar);
        btnGuardarModificar = (Button) findViewById(R.id.btnGuardarModificar);
        txtNombre = (EditText) findViewById(R.id.nombreModificar);
        txtApellidos = (EditText) findViewById(R.id.apellidosModificar);
        txtContrasena = (EditText) findViewById(R.id.contrasenaModificar);


        //Obtenemos la referencia al storage de Firebase
        storageReference = FirebaseStorage.getInstance().getReference();


        //Conexión a la BD remota para obtener todos los datos que queremos que se muestren en esta actividad
        //(nombre, apellidos, contraseña y ruta de imagen de firebase)
        ConexionBDWebService conexion = new ConexionBDWebService(this);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("usuario", usuario);
        conexion.realizarConexion("mostrardatosusuario", hm);


        btnAtrasModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Modificar.this, Menu.class);
                i.putExtra("usuario", usuario);
                startActivity(i);
                finish();
            }
        });


        btnSacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Pedimos permiso a la camara
                askCameraPermissions();




            }
        });


        btnGuardarModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Si alguno de los campos se queda vacío, se muestra un Toast indicando que el usuario debe escribir en los 3
                if (txtContrasena.getText().toString().equals("") || txtNombre.getText().toString().equals("")
                        || txtApellidos.getText().toString().equals("")) {
                    Toast.makeText(Modificar.this, "NO puedes dejar ningún campo vacío", Toast.LENGTH_SHORT).show();
                } else {

                    //Cuando el usuario pulsa "Guardar" y se comprueba que hay algo escrito en los EditTexts, se sube la imagen
                    // a Firebase y se realiza una conexión a la BD remota para actualizar los datos del usuario
                   // uploadImageToFirebase(file.getName(), uri);
                    uploadImageToFirebase(file.getName(), uri);

                    ConexionBDWebService conexion = new ConexionBDWebService(Modificar.this);
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("usuario", usuario);
                    hm.put("contrasena", txtContrasena.getText().toString());
                    hm.put("nombre", txtNombre.getText().toString());
                    hm.put("apellidos", txtApellidos.getText().toString());
                    hm.put("imagen", file.getName());

                    conexion.realizarConexion("actualizarDatosUsuario", hm);

                    Toast.makeText(Modificar.this, "Cambios actualizados", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }


    public void ejecutarResultadoMostradoDatos(String resultado) {

        try {

            //Obtenemos el nombre, apellidos, contraseña y ruta de imagen en firebase
            JSONObject json = new JSONObject(resultado);

            String nombre = (String) json.get("NOMBRE");
            String apellidos = (String) json.get("APELLIDOS");
            String contrasena = (String) json.get("CONTRASENA");
            String imagen = (String) json.get("IMAGEN");


            //Si existe una imagen almacenada en la BD (también habría en Firebase) se muestra un Toast
            //indicando que se está cargando, ya que tarda
            if (!imagen.equals("")) {
                Toast.makeText(Modificar.this, "Se está cargando la imagen", Toast.LENGTH_SHORT).show();
            }


            //Escribimos en los textos el nombre obtenido de la BD, los apellidos, la contraseña
            //y en el imageView ponemos la imagen de Firebase
            txtNombre.setText(nombre);
            txtApellidos.setText(apellidos);
            txtContrasena.setText(contrasena);

            StorageReference pathReference = storageReference.child("pictures/" + imagen);
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(imageView);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

       /*
            Basado en el código extraído de Github
    https://github.com/bikashthapa01/basic-camera-app-android/blob/master/app/src/main/java/net/smallacademy/cameraandgallery/MainActivity.java
            Autor: https://github.com/bikashthapa01
            Modificado por Jon Miguel para adaptar las funcionalidades y mensajes deseados
        */


    //Se pide permiso para abrir la cámara si es que ese permiso no está ya dado
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 1000);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap miniatura = (Bitmap) extras.get("data");
            imageView.setImageBitmap(miniatura);
            File eldirectorio = this.getFilesDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File fichero = new File(eldirectorio, usuario + ".jpg");
            OutputStream os;
            try {
                os = new FileOutputStream(fichero);
                miniatura.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {

            }

            uri = Uri.fromFile(fichero);
            file = fichero;

        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {

        //Subimos la imagen a Firebase en la carpeta /pictures/
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

                Toast.makeText(Modificar.this, "La imagen se ha subido correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Modificar.this, "La imagen no se ha subido correctamente", Toast.LENGTH_SHORT).show();
            }
        });

    }




}