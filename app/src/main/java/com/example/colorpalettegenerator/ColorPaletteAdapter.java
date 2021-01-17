package com.example.colorpalettegenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ColorPaletteAdapter extends RecyclerView.Adapter<ColorPaletteAdapter.ColorPaletteViewHolder>{

    private ArrayList<ColorPalette> colorPalettes;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private Context context;
    private View itemView;




    public ColorPaletteAdapter(boolean filterRequiered){
      //  FirebaseUtil.openFbReference("colorpalette",);
        firebaseDatabase= FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        colorPalettes= FirebaseUtil.colorPalettes;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(filterRequiered==true){
                    ColorPalette colorPalette= snapshot.getValue(ColorPalette.class);
                    colorPalette.setId(snapshot.getKey());
                    if(colorPalette.isMarked()){
                        colorPalettes.add(colorPalette);
                        notifyItemInserted(colorPalettes.size()-1);}}
                else{
                    ColorPalette colorPalette= snapshot.getValue(ColorPalette.class);
                    colorPalette.setId(snapshot.getKey());
                    colorPalettes.add(colorPalette);
                    notifyItemInserted(colorPalettes.size()-1);}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }
    @NonNull
    @Override
    public ColorPaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
         itemView = LayoutInflater.from(context)
                .inflate(R.layout.palette_list_item,parent,false);

        return new ColorPaletteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPaletteViewHolder holder, int position) {

        ColorPalette palette = colorPalettes.get(position);
        holder.bind(palette);
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.palette_menu);
                Menu popupMenu = popup.getMenu();
                if(palette.isMarked()){
                    MenuItem item = popupMenu.findItem(R.id.mark_item);
                    item.setVisible(false);
                }
                else{
                    MenuItem item = popupMenu.findItem(R.id.unmark_item);
                    item.setVisible(false);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_item:
                                databaseReference.child(palette.getId()).removeValue();
                                Toast.makeText(context, "Palette deleted successfully", Toast.LENGTH_LONG).show();
                                if(palette.getPictureName().isEmpty()==false && palette.getPictureName()!=null){
                                    StorageReference picRef = FirebaseUtil.firebaseStorage.getReference().child(palette.getPictureName());
                                    picRef.delete();
                                }
                                backToList();
                                break;
                            case R.id.mark_item:
                                databaseReference.child(palette.getId()).child("marked").setValue(true);
                                backToList();
                                break;
                            case R.id.unmark_item:
                                databaseReference.child(palette.getId()).child("marked").setValue(false);
                                backToList();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

    }
    private void backToList()
    {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return colorPalettes.size();
    }

    public class ColorPaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView color1;
        TextView color2;
        TextView color3;
        TextView color4;
        TextView date;
        TextView buttonViewOption;
        ImageView marked;

        public ColorPaletteViewHolder(View itemView){
            super(itemView);
            color1=(TextView) itemView.findViewById(R.id.Color1);
            color2=(TextView) itemView.findViewById(R.id.Color2);
            color3=(TextView) itemView.findViewById(R.id.Color3);
            color4=(TextView) itemView.findViewById(R.id.Color4);
            date = (TextView) itemView.findViewById(R.id.date);
            buttonViewOption=(TextView) itemView.findViewById(R.id.textViewOptions);
            marked=(ImageView) itemView.findViewById(R.id.mark);
            itemView.setOnClickListener(this);

        }
        public void bind(ColorPalette palette) {
            String colorText = Integer.toHexString(palette.getRGBPalette().get(0));

            color1.setText("#" + colorText.substring(colorText.length() - 6));
            color1.setBackgroundColor(palette.getRGBPalette().get(0));
            color1.setTextColor(palette.getRGBtextColor().get(0));

            colorText = Integer.toHexString(palette.getRGBPalette().get(1));
            color2.setText("#" + colorText.substring(colorText.length() - 6));
            color2.setBackgroundColor(palette.getRGBPalette().get(1));
            color2.setTextColor(palette.getRGBtextColor().get(1));

            colorText = Integer.toHexString(palette.getRGBPalette().get(2));
            color3.setText("#" + colorText.substring(colorText.length() - 6));
            color3.setBackgroundColor(palette.getRGBPalette().get(2));
            color3.setTextColor(palette.getRGBtextColor().get(2));

            colorText = Integer.toHexString(palette.getRGBPalette().get(3));
            color4.setText("#" + colorText.substring(colorText.length() - 6));
            color4.setBackgroundColor(palette.getRGBPalette().get(3));
            color4.setTextColor(palette.getRGBtextColor().get(3));
            String dateInfo = new SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault()).format(palette.getDate());
            date.setText(dateInfo);
            if(palette.isMarked()) {
                marked.setImageResource(R.drawable.star);
            }



            //copy in clipboard
            setCopyPasteFunction(color1);
            setCopyPasteFunction(color2);
            setCopyPasteFunction(color3);
            setCopyPasteFunction(color4);

        }
        private void setCopyPasteFunction(TextView color){
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(color.getText(), color.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ColorPalette colorPalette = colorPalettes.get(position);
            Intent intent = new Intent(v.getContext(),ColorPaletteActivity.class);
            intent.putExtra("ColorPalette",colorPalette);
            v.getContext().startActivity(intent);
        }
    }

}
