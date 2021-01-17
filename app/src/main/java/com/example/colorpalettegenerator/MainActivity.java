package com.example.colorpalettegenerator;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button uploadButton;
    Button historyButton;
    private ColorPaletteExtractor extractor;
    private CopyPaste copyPaste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        extractor= new ColorPaletteExtractor(MainActivity.this);
        FirebaseUtil.openFbReference("colorpalette",this);

        imageView = (ImageView)findViewById(R.id.image_frame);
        uploadButton = (Button)findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractor.selectImageMenu();

            }
        });

        historyButton= findViewById(R.id.to_history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        setCopyPasteOnDefault();
    }

    private void setCopyPasteOnDefault() {
        TextView color1=findViewById(R.id.Color01);
        TextView color2=findViewById(R.id.Color02);
        TextView color3=findViewById(R.id.Color03);
        TextView color4=findViewById(R.id.Color04);

        TextView textColor1=findViewById(R.id.ColorHex01);
        TextView textColor2=findViewById(R.id.ColorHex02);
        TextView textColor3=findViewById(R.id.ColorHex03);
        TextView textColor4=findViewById(R.id.ColorHex04);

        //set copy paste
        copyPaste = new CopyPaste(MainActivity.this);
        copyPaste.setCopyPasteFunction(color1,textColor1);
        copyPaste.setCopyPasteFunction(color2,textColor2);
        copyPaste.setCopyPasteFunction(color3,textColor3);
        copyPaste.setCopyPasteFunction(color4,textColor4);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case 1: {
                    File imagePath = new File(getFilesDir(), "external_files");
                    File imageFile = new File(imagePath.getPath(), extractor.getLastCapturedImage());
                    try {
                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmap = null;
                        while (bitmap == null) {
                            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bitmapOptions);
                        }
                        bitmap = extractor.getResizedBitmap(bitmap, 300);
                        imageView.setImageBitmap(bitmap);
                        extractor.generateColorPalette(bitmap);

                        //save palette if user is logged in
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(userId!=null) {
                            ColorPalette palette = extractor.getColorPalette();

                            Uri imageUri = Uri.fromFile(imageFile);
                            StorageReference ref = FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
                            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = uri.toString();
                                            String name = taskSnapshot.getStorage().getPath();
                                            palette.setPictureURL(url);
                                            palette.setPictureName(name);
                                            FirebaseUtil.databaseReference.push().setValue(palette);
                                        }
                                    });

                                }
                            });
                        }


                        extractor.BitMapToString(bitmap);
                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "GlazeStripGallery" + File.separator + "default";
                        imageFile.delete();
                        OutputStream outFile = null;
                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                        try {
                            outFile = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                            outFile.flush();
                            outFile.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    extractor.generateColorPalette(bitmap);
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(userId!=null){
                        ColorPalette palette = extractor.getColorPalette();
                        StorageReference ref = FirebaseUtil.storageReference.child(selectedImage.getLastPathSegment());
                        ref.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        String name = taskSnapshot.getStorage().getPath();
                                        palette.setPictureURL(url);
                                        palette.setPictureName(name);
                                        FirebaseUtil.databaseReference.push().setValue(palette);
                                    }
                                });

                            }
                        });}
                    break;
                }


            }
        }
    }
}