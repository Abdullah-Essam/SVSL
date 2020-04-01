package com.example.svslsavemoneysavelife;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class Hme extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "Hme";
    private static final String MY_CAMERA_ID = "my_camera_id";
    static final int REQUEST_IMAGE_CAPTURE = 66;
    private static final int STORAGE_REQUEST_CODE =400;
    private static final int IMAGE_PICK_CAMERA_CODE =1000;
    public  static final int RequestPermissionCode  = 1 ;
    TextView Invoice , Total, textView ;
    TextView date1;
    String cameraper[];
    String storageper[];
    Uri uri;
    ImageButton im;
    ImageView iv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hme);
        Calendar rightNow = Calendar.getInstance();
        date1 = (TextView) findViewById(R.id.date);
        Invoice = (TextView) findViewById(R.id.textView);
        Total = (TextView) findViewById(R.id.tvToatal);
        textView = (TextView) findViewById(R.id.textView2);

        Total.setText("Total");
        Invoice.setText("Invoice");

        im = (ImageButton) findViewById(R.id.imageButton6);
        iv = (ImageView) findViewById(R.id.imageView12);
        mAuth = FirebaseAuth.getInstance();

        EnableRuntimePermission();

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(Hme.this);
                for (int i = 0; i <= 3; i++) {
                    date1 = (TextView) findViewById(R.id.date);
                    Invoice = (TextView) findViewById(R.id.textView);
                    Total = (TextView) findViewById(R.id.tvToatal);
                    Total.setText("Total");
                    Invoice.setText("Invoice");
                    Total.setText("Total" + i);
                    Invoice.setText("Invoice" + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G ");
                    String currentDateandTime = sdf.format(new Date());
                    date1.setText(currentDateandTime);


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.n):
                //startActivity(new Intent(Hme.this, Login.class));
                return true;
            case (R.id.p):
                startActivity(new Intent(Hme.this, predection.class));
                return true;
            case (R.id.a):
                startActivity(new Intent(Hme.this, aboutus.class));
                return true;
            case (R.id.sfg):
                startActivity(new Intent(Hme.this, saveforgoal.class));
                return true;
            case (R.id.s):
                startActivity(new Intent(Hme.this, setspendinglimit.class));
                return true;
            case (R.id.r):
                //startActivity(new Intent(Hme.this, rating.class));
                return true;
            case (R.id.h):
                //startActivity(new Intent(Hme.this, help.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri resultUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, resultUri)) {
                uri = resultUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }else {
                startCrop(resultUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                iv.setImageURI(result.getUri());
                bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
                detectText();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

//        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
////            Bundle extras = data.getExtras();
////            assert extras != null;
////            bitmap = (Bitmap) extras.get("data");
////            iv.setImageBitmap(bitmap);
////            //detectText();
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                iv.setImageBitmap(result.getBitmap());
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
    }

    public static Uri getCaptureImageOutputUri(@NonNull Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    private void startCrop(Uri resultUri) {
        CropImage.activity(resultUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
    private void detectText() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                String s = firebaseVisionText.getText();
                                textView.setText(s);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }
            public void onClick(DialogInterface d , int which){
        if (which==0){
            if(!checkCameraPermission()){
                req();
            }
            else{
                pick();
            }
        }
            }
    private void req() {
        ActivityCompat.requestPermissions(this,storageper,STORAGE_REQUEST_CODE);
    }
    private void pick() {
        ContentValues v = new ContentValues();
        v.put(MediaStore.Images.Media.TITLE, "Nwepic");
        v.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);
        Intent c = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        c.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(c,IMAGE_PICK_CAMERA_CODE);
    }
    private boolean checkCameraPermission() {

        boolean r = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== (PackageManager.PERMISSION_GRANTED);
        boolean r1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);

        return r&&r1;
    }

    public void EnableRuntimePermission () {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Hme.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(Hme.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(Hme.this, new String[]{
                        Manifest.permission.CAMERA}, RequestPermissionCode);
            }
        }
        @Override
        public void onRequestPermissionsResult ( int RC, String per[],int[] PResult){
            switch (RC) {
                case RequestPermissionCode:
                    if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(Hme.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Hme.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            }
        }