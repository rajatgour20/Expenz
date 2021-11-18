package com.example.s;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;


public class BillActivity extends AppCompatActivity {
    //old
    Button btnGallery, btnCamera, btnNewApp;
    TextView tv1,tv2;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //new
    ImageView iv;
    EditText et;
    Db o;
    final static private int p = 100;
    private Uri image;
    private Bitmap imagetostore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_billactivity);
        o = new Db(this);
        //old
        //btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGal);
        tv1 = findViewById(R.id.text);
        et = findViewById(R.id.finalAmt);
        //new
        iv = findViewById(R.id.imageView);



//        btnCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (ActivityCompat.checkSelfPermission(MainActivity.this,
////                        Manifest.permission.CAMERA) !=
////                        PermissionChecker.PERMISSION_GRANTED) {
////                    ActivityCompat.requestPermissions(MainActivity.this, new
////                            String[]{Manifest.permission.CAMERA}, 11);
////                }
////                else {
//                Intent in =
//                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(in, 113);
//                if (in.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(in, REQUEST_IMAGE_CAPTURE);
//                }
//            }
//            //}
//        });
//
//        btnGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                restore();
//            }
//        });


    }

    public void chooseimage(View objview) {
        Intent objectIntent = new Intent();
        objectIntent.setType("image/*");

        objectIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(objectIntent, p);
    }

//    public void restore() {
//        et.setText("");
//    }


    //old
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) { //(int requestCode, int resultCode, @Nullable Intent data) if old one doesn't works
        super.onActivityResult(requestCode, resultCode, data);

        //new
        //ye gallery ka krega
        if (requestCode == p && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image = data.getData();
//            try {
//                imagetostore = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
//                iv.setImageBitmap(imagetostore);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        //old
//        if (requestCode == 112 && data != null) {
//            //Uri uri = data.getData();
            //image = data.getData();
            try {
                //Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imagetostore = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imagetostore);
                iv.setImageBitmap(imagetostore);
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        textRecognizer.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {


                                        String lowerCa = firebaseVisionText.getText();
                                        String str = "";
                                        String newtotalAmt = "";
                                        String totalAmt = "";
                                        String amount = "";
                                        String perfamt[] = {};
                                        try {
                                            str = lowerCa.toLowerCase();
                                            if (str.contains("total") || str.contains("toral")) {
                                                //starting me 2 x hona must
                                                String amt[] = str.split("total", 2);
                                                for (int i = 0; i < amt[1].length(); i++) {
                                                    if (amt[1].charAt(i) >= 48 && amt[1].charAt(i) <= 57 || amt[1].charAt(i) == '.' || amt[1].charAt(i) == ',') {
                                                        totalAmt = totalAmt + amt[1].charAt(i);
                                                        if (amt[1].charAt(i) == ' ') {
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (totalAmt.contains(".")) {
                                                    perfamt = totalAmt.split("\\.+");
                                                    ;
                                                    for (int i = 0; i <= 1; i++) {
                                                        if (perfamt[1].charAt(i) >= 48 && perfamt[1].charAt(i) <= 57) {
                                                            newtotalAmt = newtotalAmt + perfamt[1].charAt(i);
                                                        }
                                                    }
                                                    //System.out.println(perfamt[0]);
                                                    System.out.println(perfamt[0] + "." + newtotalAmt);
                                                }
                                            }
                                            //tv1.setText(firebaseVisionText.getText());
                                            amount = amount + perfamt[0] + "." + newtotalAmt;
                                            et.setText(amount);
                                            double finalAmt = Double.parseDouble(amount);
                                        }
                                        catch (ArrayIndexOutOfBoundsException e) {
                                            amount = String.valueOf(et.getText());
                                            et.setText(amount+"₹");
                                        }
                                        //ye print krne ka wala
                                        //tv2.setText(finalAmt);
                                        Toast.makeText(BillActivity.this, ""+firebaseVisionText.getText(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //process failure
                                            }
                                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
            //iv.setImageURI(uri);
            iv.setImageURI(image);
        }

        //ye camera se read krega
        if (requestCode == 113 && data != null) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bmp);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    textRecognizer.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                    String lowerCa = firebaseVisionText.getText();
                                    String str="";
                                    String newtotalAmt="";
                                    String totalAmt = "";
                                    String amount ="";
                                    String perfamt[]={};
                                    try {
                                        str = lowerCa.toLowerCase();
                                        if (str.contains("total") || str.contains("toral")) {
                                            //starting me 2 x hona must
                                            String amt[] = str.split("total", 2);
                                            for (int i = 0; i < amt[1].length(); i++) {
                                                if (amt[1].charAt(i) >= 48 && amt[1].charAt(i) <= 57 || amt[1].charAt(i) == '.' || amt[1].charAt(i) == ',') {
                                                    totalAmt = totalAmt + amt[1].charAt(i);
                                                    if (amt[1].charAt(i) == ' ') {
                                                        break;
                                                    }
                                                }
                                            }
                                            if (totalAmt.contains(".")) {
                                                perfamt = totalAmt.split("\\.+");
                                                ;
                                                for (int i = 0; i <= 1; i++) {
                                                    if (perfamt[1].charAt(i) >= 48 && perfamt[1].charAt(i) <= 57) {
                                                        newtotalAmt = newtotalAmt + perfamt[1].charAt(i);
                                                    }
                                                }
                                                //System.out.println(perfamt[0]);
                                                System.out.println(perfamt[0] + "." + newtotalAmt);
                                            }
                                        }
                                        //tv1.setText(firebaseVisionText.getText());
                                        amount = "Amount is : " + amount + perfamt[0] + "." + newtotalAmt;
                                        et.setText(amount);
                                        double finalAmt = Double.parseDouble(amount);
                                    }
                                    catch (ArrayIndexOutOfBoundsException e) {
                                        amount = String.valueOf(et.getText());
                                        et.setText(amount);
                                    }
                                    //ye print krne ka wala
                                    //tv2.setText(finalAmt);
                                    Toast.makeText(BillActivity.this, ""+firebaseVisionText.getText(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //process failure
                                        }
                                    });
        }
    }

    //new
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == p && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            image = data.getData();
//            try {
//                imagetostore = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
//                iv.setImageBitmap(imagetostore);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

    //new
    public void storeimage(View view) {
        if (!et.getText().toString().isEmpty() && iv.getDrawable() != null && imagetostore != null) {
            o.storeimage(new Modelclass(et.getText().toString(), imagetostore));
            et.setText("");
            iv.setImageResource(R.drawable.add_picture1);
        } else {
            Toast.makeText(this, "ccccccccccc", Toast.LENGTH_SHORT).show();
        }
    }


    public void m(View view) {
        Intent in=new Intent(BillActivity.this,Showimagesactivity.class);
        startActivity(in);
    }
}