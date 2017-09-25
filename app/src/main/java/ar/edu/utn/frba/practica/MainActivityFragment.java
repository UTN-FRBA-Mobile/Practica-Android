package ar.edu.utn.frba.practica;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MainActivityFragment extends Fragment {

    //Códigos de los Request de Intents
    private static final int REQUEST_IMAGE_CAPTURE = 911;
    private static final int REQUEST_GALLERY_PICTURE = 912;
    //Código del Request de Permisos de External Storage
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 913;

    private ImageView profilePicture;

    private String mCurrentPhotoPath;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        profilePicture = rootView.findViewById(R.id.profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOrGalleryDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Tomamos el Path del archivo que habíamos creado para guardar la foto y obtenemos la uri del mismo, para después setearle la foto a la ImageView
            profilePicture.setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));
        }

        if (requestCode == REQUEST_GALLERY_PICTURE && resultCode == RESULT_OK) {
            //Leemos desde la uri que nos devuelve el Intent el stream de la imagen para después setearlo a la ImageView
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePicture.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ocurrió un error seleccionando la foto de la galería", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                //REEMPLAZAR POR CÓDIGO: if (meDioPermisos()) {
                    launchCameraIntent();
                //} else {
                //    Toast.makeText(getContext(), "Lo sentimos, sin acceso al almacenamiento no podemos cambiar la foto de perfil.", Toast.LENGTH_LONG).show();
                //}
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showCameraOrGalleryDialog() {
        //Creamos un cuadro de dialogo donde se pregunte de dónde obtener la imagen: De la galeria o de la cámara
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar foto");
        builder.setCancelable(true);
        builder.setMessage("¿De donde querés seleccionar la foto?");
        builder.setNeutralButton("Cámara", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchCamera();
            }
        });
        builder.setPositiveButton("Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchGalleryIntent();
            }
        });
        builder.show();
    }

    private void launchGalleryIntent() {
        //Creamos y lanzamos un intent de tipo "ACTION_PICK": Todas las apps que tengan Activities que sepan responder a este tipo de Intents serán mostradas por el sistema operativo, y el usuario será libre de seleccionar la que prefiera
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //Verificamos que el sistema operativo tenga al menos una App q pueda responder a este tipo de Intent. Sino mostramos un error.
        if (photoPickerIntent.resolveActivity(getContext().getPackageManager()) != null) {
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_GALLERY_PICTURE);
        } else {
            Toast.makeText(getContext(), "Al parecer tu dispositivo no cuenta con ninguna aplicación para tomar fotos.", Toast.LENGTH_LONG).show();
        }
    }

    private void launchCamera() {
//REEMPLAZAR POR CÓDIGO: if(TengoPermisosPara(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//REEMPLAZAR POR CÓDIGO: if (DeberíaMostrarExplicaciónSobreElPorqueNecesitoEsteAcceso(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                showStoragePermissionExplanation();
//            } else {
//                dispatchStoragePermissionRequest();
//            }
//        } else {
            launchCameraIntent();
//        }
    }

    private void showStoragePermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Necesitamos tu permiso");
        builder.setCancelable(true);
        builder.setMessage("Necesitamos acceso al almacenamiento para poder guardar la foto que tomes.");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dispatchStoragePermissionRequest();
            }
        });
        builder.show();
    }

    private void dispatchStoragePermissionRequest() {
        //REEMPLAZAR POR CÓDIGO: PedirPermisos();
    }

    private void launchCameraIntent() {
        //Creamos y lanzamos un intent de tipo "ACTION_IMAGE_CAPTURE": Todas las apps que tengan Activities que sepan responder a este tipo de Intents serán mostradas por el sistema operativo, y el usuario será libre de seleccionar la que prefiera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Verificamos que el sistema operativo tenga al menos una App q pueda responder a este tipo de Intent. Sino mostramos un error.
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            //Debemos crear un archivo donde el Intent va a guardar la foto para después leerla desde ahí
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(getContext(), "Ha ocurrido un error tomando la foto.", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                //Para poder obtener la uri del archivo que creamos debemos hacerlo mediante un file provider que declaramos en el manifest
                Uri photoURI = FileProvider.getUriForFile(getContext(), "ar.edu.utn.frba.practica.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(getContext(), "Ha ocurrido un error tomando la foto.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Al parecer tu dispositivo no cuenta con ninguna aplicación para tomar fotos.", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        //Crea un nombre en base al timestamp para que no colisioné con otros nombres
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Creamos el archivo en el External Storage compartido del dispositivo: Esto requiere que la app tenga permisos para escribir en dicho storage.
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
