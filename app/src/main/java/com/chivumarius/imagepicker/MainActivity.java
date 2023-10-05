package com.chivumarius.imagepicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    // ▼ "DECLARATION" OF "WIDGETS IDS" ▼
    ImageView imageView;
    Button galleryBtn;
    Button cameraBtn;


    // ▼ "DECLARATION" OF "VARIABLE"
    Uri image_uri;




    // ▼ "GALLERY ACTIVITY RESULT LAUNCHER" OBJECT ("DECLARATION" & "INITIALIZATION")
    //      → TO "GET" THE "IMAGE" FROM "GALLERY" AND "DISPLAY IT" ▼
    ActivityResultLauncher<Intent> galleryActivityResultLauncher =
            registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    // ▬▬ "ON ACTIVITY RESULT()" METHOD
                    //      → TO "GET" THE "SELECTED DATA" (THE "IMAGE")
                    //      → FROM "GALLERY" AND "DISPLAY IT" ▬▬
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // ▼ WE "MAKE SURE" THAT THE "USER SELECTED" AN "IMAGE" ▼
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // ▼ GET "SELECTED DATA" FROM "GALLERY" ▼
                            //Uri image_uri = result.getData().getData();
                            image_uri = result.getData().getData();

                            // ▼ DISPLAY "SELECTED DATA" IN "IMAGE VIEW" ▼
                            //imageView.setImageURI(image_uri);


                            // ▼ CONVERTING "SELECTED DATA" TO "BITMAP" ▼
                            // ▼ GET "SELECTED DATA" FROM "CAMERA" ▼
                            Bitmap inputImage = uriToBitmap(image_uri);

                            // ▼ "REMOVING" THE "ROTATION" ▼
                            Bitmap rotated = rotateBitmap(inputImage);

                            // ▼ "DISPLAYING" THE "CAPTURED IMAGE" ▼
                            imageView.setImageBitmap(rotated);
                        }
                    }
                }
            );




    // ▬▬ "ON CREATE()" METHOD ▬▬
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ▼ "INITIALIZATION" OF "WIDGETS IDS" ▼
        imageView = findViewById(R.id.imageView);
        galleryBtn = findViewById(R.id.button);
        cameraBtn = findViewById(R.id.button2);



        // ▼ "ASKING" FOR "PERMISSION" OF "CAMERA" UPON "FIRST LAUNCH" OF "APP"  ▼
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 112);
            }
        }


        // ▼ "SET ON CLICK LISTENERS" → FOR "GALLERY BUTTON"
        //      → FOR "CHOOSING IMAGE" FROM "GALLERY" ▼
        galleryBtn.setOnClickListener(new View.OnClickListener() {

            // ▬▬ "ON CLICK()" METHOD ▬▬
            @Override
            public void onClick(View v) {
                // ▼ OPEN "GALLERY"
                //      → BY SPECIFYING "ACTION PICK"
                //      → AND SPECIFYING "MEDIA" TYPE,
                //      → WHICH WE WANT TO OPEN ▼
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // ▼ CALL "LAUNCHER" FOR "GALLERY" ▼
                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });





        // ▼ "SET ON CLICK LISTENERS" → FOR "CAMERA BUTTON"
        //      → FOR "CAPTURE IMAGE" USING "CAMERA" ▼
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            // ▬▬ "ON CLICK()" METHOD ▬▬
            @Override
            public void onClick(View view) {

                // ▼ "CHECKING" FOR "PERMISSION" OF "CAMERA" UPON "FIRST LAUNCH" OF "APP" ▼
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    // ▼ "ASKING" FOR "PERMISSION" OF "CAMERA" UPON "FIRST LAUNCH" OF "APP"
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permission = {
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        };
                        requestPermissions(permission, 112);

                    } else {
                        // ▼ "CALLING" THE "METHOD" ▼
                        openCamera();
                    }
                }
                else {
                    // ▼ "CALLING" THE "METHOD" ▼
                    openCamera();
                }
            }
        });

    }






    // ▬▬ "OPEN CAMERA()" METHOD
    //      → SO THAT THE USER CAN CAPTURE IMAGE USING "CAMERA" ▬▬
    private void openCamera() {

        // ▼ CREATING A "CONTENT VALUES" OBJECT ▼
        ContentValues values = new ContentValues();

        // ▼ ADDING A "TITLE" AND "DESCRIPTION" TO THE "CONTENT VALUES" OBJECT ▼
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");

        // STORING THE "CAPTURED IMAGE" IN "IMAGE_URI" VARIABLE ▼
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);



        // ▼ THE "ACTION" OF "CAPTURE IMAGE" USING "CAMERA" ▼
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // ▼ STORING "IMAGE" IN "IMAGE_URI" VARIABLE ▼
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        // ▼ CALLING THE "CAMERA ACTIVITY RESULT LAUNCHER" ▼
        cameraActivityResultLauncher.launch(cameraIntent);
    }




    // ▼ "CAMERA ACTIVITY RESULT LAUNCHER" OBJECT ("DECLARATION" & "INITIALIZATION")
    //      → TO "GETTING" THE "IMAGE"
    //      → BY USING "CAMERA" AND "DISPLAY IT" ▼
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                // ▬▬ "ON ACTIVITY RESULT()" METHOD
                @Override
                public void onActivityResult(ActivityResult result) {

                    // ▼ IF "USER CAPTURED" THE "IMAGE"
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // ▼ GET "SELECTED DATA" FROM "CAMERA" ▼
                        Bitmap inputImage = uriToBitmap(image_uri);

                        // ▼ ROTATING THE "CAPTURED IMAGE" ▼
                        Bitmap rotated = rotateBitmap(inputImage);

                        // ▼ DISPLAYING THE "CAPTURED IMAGE" ▼
                        imageView.setImageBitmap(rotated);
                    }
                }
            });





    // ▬▬ "URI TO BITMAP()" METHOD
    //      → TO "TAKES URI" OF THE "IMAGE" AND "RETURNS BITMAP" ▬▬
    private Bitmap uriToBitmap(Uri selectedFileUri) {

        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }






    // ▬▬ "ROTATE BITMAP()" METHOD
    //      → FOR "ROTATING" THE "CAPTURED IMAGE" IN A "PORTRAIT" MODE,
    //      → IF "IMAGE" IS "CAPTURED" ON "SAMSUNG DEVICES"
    //      → ( MOST "PHONES CAMERAS" ARE "LANDSCAPE",
    //      →   "MEANING" IF YOU "TAKE" THE "PHOTO" IN "PORTRAIT",
    //      →   THE RESULTING PHOTOS WILL BE ROTATED 90 DEGREES) ▬▬
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input){
        String[] orientationColumn = { MediaStore.Images.Media.ORIENTATION };
        Cursor cur = getContentResolver().query(image_uri, orientationColumn, null, null, null);
        int orientation = -1;

        // ▼ IF THERE IS AN "IMAGE" ▼
        if (cur != null && cur.moveToFirst()) {
            // ▼ GETTING THE "ORIENTATION" OF THE "IMAGE" ▼
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }

        Log.d("tryOrientation",orientation+"");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input,0,0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

}