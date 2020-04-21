package com.example.svslsavemoneysavelife.activities.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.adapters.InvoiceAdapter;
import com.example.svslsavemoneysavelife.callback.UserCallback;
import com.example.svslsavemoneysavelife.controller.UserController;
import com.example.svslsavemoneysavelife.models.Invoice;
import com.example.svslsavemoneysavelife.models.User;
import com.example.svslsavemoneysavelife.utils.SharedData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {

    private static final int RequestPermissionCode = 77;
    private RecyclerView recyclerView;
    private TextView monthTitle;
    private Button insertButton, lang;
    private ImageView imageView;
    private InvoiceAdapter adapter;
    private UserController userController = new UserController();
    private Bitmap bitmap;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat df = new SimpleDateFormat("MMMM");

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        monthTitle = view.findViewById(R.id.title);
        imageView = view.findViewById(R.id.imageView);
        insertButton = view.findViewById(R.id.insert_invoice);
        lang = view.findViewById(R.id.lang);

        init();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        EnableRuntimePermission();
        monthTitle.setText(df.format(SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getMonthStart()) + getString(R.string.month));
        refreshList();
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(Objects.requireNonNull(getContext()), HomeFragment.this);
            }
        });

        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change lang
                Locale current = getCurrentLocale();
                if (current.getLanguage().equals("ar")) {
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("en".toLowerCase()));
                    res.updateConfiguration(conf, dm);

                    Intent i = getActivity().getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().finish();
                    startActivity(i);
                } else if (current.getLanguage().equals("en")) {
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("ar".toLowerCase()));
                    res.updateConfiguration(conf, dm);

                    Intent i = getActivity().getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().finish();
                    startActivity(i);
                }
            }
        });
    }

    private void refreshList() {
        ArrayList<Invoice> invoices = new ArrayList<>();
        if (adapter == null) {
            invoices.addAll(SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getInvoices());
            adapter = new InvoiceAdapter(invoices);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            invoices.addAll(SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getInvoices());
            adapter.updateData(invoices);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri resultUri = CropImage.getPickImageResultUri(Objects.requireNonNull(getContext()), data);
            if (CropImage.isReadExternalStoragePermissionsRequired(getContext(), resultUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCrop(resultUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imageView.setImageURI(result.getUri());
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                detectText();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startCrop(Uri resultUri) {
        CropImage.activity(resultUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(Objects.requireNonNull(getContext()), HomeFragment.this);
    }

    private void detectText() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.detecting));
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                double amount = 0.0;
                                for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
                                    for (FirebaseVisionText.Line line : textBlock.getLines()) {
                                        for (FirebaseVisionText.Element element : line.getElements()) {
                                            try {
                                                String s = element.getText().replaceAll("[^\\d.]", "");
                                                double a = Double.parseDouble(s);
                                                if (a > amount) {
                                                    amount = a;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                if (amount > 0) {
                                    ArrayList<Invoice> oldList = SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getInvoices();
                                    Invoice invoice = new Invoice();
                                    invoice.setKey(String.valueOf(oldList.size() + 1));
                                    invoice.setAmount(amount);
                                    invoice.setDate(Calendar.getInstance().getTime());
                                    SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getInvoices().add(invoice);
                                    double oldTotal = SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).getTotalExpanse();
                                    SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).setTotalExpanse(oldTotal + amount);
                                    progressDialog.setTitle(getString(R.string.total_is) + String.format("%.2f", amount) + getString(R.string.saving));
                                    userController.save(SharedData.currentUser, new UserCallback() {
                                        @Override
                                        public void onSuccess(ArrayList<User> users) {
                                            SharedData.currentUser = users.get(0);
                                            progressDialog.dismiss();
                                            refreshList();
                                        }

                                        @Override
                                        public void onFail(String error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), R.string.detect_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), R.string.detect_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void EnableRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    RequestPermissionCode);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, @NonNull String[] per, @NonNull int[] PResult) {
        if (RC == RequestPermissionCode) {
            if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getContext(), "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), R.string.permission_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return this.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return this.getResources().getConfiguration().locale;
        }
    }
}
