package com.example.colorpalettegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ColorPaletteActivity extends AppCompatActivity {

    ColorPalette colorPalette;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_palette);

        Intent intent = getIntent();
        colorPalette= (ColorPalette) intent.getSerializableExtra("ColorPalette");
        setActivityItems();
    }

    private void setActivityItems(){
        TextView color1=findViewById(R.id.mColor1);
        TextView color2=findViewById(R.id.mColor2);
        TextView color3=findViewById(R.id.mColor3);
        TextView color4=findViewById(R.id.mColor4);
        TextView time=findViewById(R.id.mTime);
        TextView date=findViewById(R.id.mDate);
        TextView marked=findViewById(R.id.mMarked);
        ImageView picture= findViewById(R.id.mItem_image);
        showImage(colorPalette.getPictureURL(),picture);

        String colorText = Integer.toHexString(colorPalette.getRGBPalette().get(0));

        color1.setText("#" + colorText.substring(colorText.length() - 6));
        color1.setBackgroundColor(colorPalette.getRGBPalette().get(0));
        color1.setTextColor(colorPalette.getRGBtextColor().get(0));

        colorText = Integer.toHexString(colorPalette.getRGBPalette().get(1));
        color2.setText("#" + colorText.substring(colorText.length() - 6));
        color2.setBackgroundColor(colorPalette.getRGBPalette().get(1));
        color2.setTextColor(colorPalette.getRGBtextColor().get(1));

        colorText = Integer.toHexString(colorPalette.getRGBPalette().get(2));
        color3.setText("#" + colorText.substring(colorText.length() - 6));
        color3.setBackgroundColor(colorPalette.getRGBPalette().get(2));
        color3.setTextColor(colorPalette.getRGBtextColor().get(2));

        colorText = Integer.toHexString(colorPalette.getRGBPalette().get(3));
        color4.setText("#" + colorText.substring(colorText.length() - 6));
        color4.setBackgroundColor(colorPalette.getRGBPalette().get(3));
        color4.setTextColor(colorPalette.getRGBtextColor().get(3));
        String dateInfo = new SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault()).format(colorPalette.getDate());
        date.setText("Date: "+dateInfo);
        String timeInfo = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(colorPalette.getDate());
        time.setText("Time: "+timeInfo);

        if(colorPalette.isMarked()) {
            marked.setText("Marked: true");
        }
        else{
            marked.setText("Marked: false");
        }
        setCopyPasteFunction(color1);
        setCopyPasteFunction(color2);
        setCopyPasteFunction(color3);
        setCopyPasteFunction(color4);

    }
    private void setCopyPasteFunction(TextView color){
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(color.getText(), color.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ColorPaletteActivity.this,"Copied",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showImage(String url, ImageView picture){
        if(url != null && url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(picture);



        }
    }
}