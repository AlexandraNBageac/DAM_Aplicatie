package com.example.colorpalettegenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ColorPaletteExtractor{
    Activity mainActivity;
    private String Document_img1="";
    private String lastCapturedImage = null;
    private ColorPalette colorPalette;
    private ArrayList<Integer> RGBPalette;
    private ArrayList<Integer> RGBtextColor;

    public ColorPaletteExtractor(Activity main){
        this.mainActivity=main;
    }

    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    public ArrayList<Integer> getRGBPalette() {
        return RGBPalette;
    }

    public String getLastCapturedImage() {
        return lastCapturedImage;
    }

    public void selectImageMenu() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Upload Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    PackageManager packageManager = mainActivity.getPackageManager();
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        Calendar calendar = Calendar.getInstance();

                        File imagePath = new File(mainActivity.getFilesDir(), "external_files");
                        imagePath.mkdir();
                        lastCapturedImage = "IMG_" + calendar.getTimeInMillis();
                        File imageFile = new File(imagePath.getPath(), lastCapturedImage);

                        Uri uri = FileProvider.getUriForFile(mainActivity,
                                "com.example.colorpalettegenerator.provider",
                                imageFile);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        try {
                            mainActivity.startActivityForResult(intent, 1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mainActivity.startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public void generateColorPalette(Bitmap bitmap){
        RGBPalette=new ArrayList<>();
        RGBtextColor=new ArrayList<>();
        Palette palette = Palette.from(bitmap).maximumColorCount(4).generate();

        for(Palette.Swatch swatch : palette.getSwatches())
        {
            RGBPalette.add(swatch.getRgb());
            RGBtextColor.add(swatch.getTitleTextColor());
        }
        //
        colorPalette=new ColorPalette();
        colorPalette.setDate(Calendar.getInstance().getTime());
        colorPalette.setRGBPalette(RGBPalette);
        colorPalette.setRGBtextColor(RGBtextColor);
        colorPalette.setPictureURL("");
        //
        TextView color1=mainActivity.findViewById(R.id.Color01);
        TextView color2=mainActivity.findViewById(R.id.Color02);
        TextView color3=mainActivity.findViewById(R.id.Color03);
        TextView color4=mainActivity.findViewById(R.id.Color04);

        TextView textColor1=mainActivity.findViewById(R.id.ColorHex01);
        TextView textColor2=mainActivity.findViewById(R.id.ColorHex02);
        TextView textColor3=mainActivity.findViewById(R.id.ColorHex03);
        TextView textColor4=mainActivity.findViewById(R.id.ColorHex04);


        color1.setBackgroundColor(palette.getSwatches().get(0).getRgb());
        color2.setBackgroundColor(palette.getSwatches().get(1).getRgb());
        color3.setBackgroundColor(palette.getSwatches().get(2).getRgb());
        color4.setBackgroundColor(palette.getSwatches().get(3).getRgb());

        String color = Integer.toHexString(palette.getSwatches().get(0).getRgb());
        textColor1.setText("#"+color.substring(color.length()-6));

        color=Integer.toHexString(palette.getSwatches().get(1).getRgb());
        textColor2.setText("#"+color.substring(color.length()-6));

        color=Integer.toHexString(palette.getSwatches().get(2).getRgb());
        textColor3.setText("#"+color.substring(color.length()-6));

        color=Integer.toHexString(palette.getSwatches().get(3).getRgb());
        textColor4.setText("#"+color.substring(color.length()-6));
        //set copy paste
        CopyPaste copyPaste= new CopyPaste(mainActivity);
        copyPaste.setCopyPasteFunction(color1,textColor1);
        copyPaste.setCopyPasteFunction(color2,textColor2);
        copyPaste.setCopyPasteFunction(color3,textColor3);
        copyPaste.setCopyPasteFunction(color4,textColor4);

    }

}
