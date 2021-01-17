package com.example.colorpalettegenerator;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CopyPaste {
    private Context context;

    public CopyPaste(Context context) {
        this.context=context;
    }
    public  void setCopyPasteFunction(TextView color, TextView text){
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text.getText(), text.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text.getText(), text.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
