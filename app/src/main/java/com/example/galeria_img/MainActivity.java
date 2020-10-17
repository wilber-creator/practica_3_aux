package com.example.galeria_img;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static GridView galleria;
    Button btn1;

    List<String> imagesEncodedList;
    private Adapterimg galleryAdapter;

    static final int CODE_PERMISSION=100;
    static final int CODE_GALERIA=200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponents();
    }

    private void loadComponents() {
        galleria = (GridView)findViewById(R.id.tablegrid);
        btn1 = findViewById(R.id.btnseleccionar);

        if(reviewPermissions()){
            btn1.setVisibility(View.VISIBLE);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), CODE_GALERIA);
            }
        });

    }

    private boolean reviewPermissions() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if(this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String [] {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE_PERMISSION);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == CODE_GALERIA && resultCode == RESULT_OK) {
                imagesEncodedList = new ArrayList<String>();
                if(data.getClipData()!=null){
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);

                        imagesEncodedList.add(getRealPath(this,uri));
                        //Toast.makeText(this,""+getRealPath(this,uri),Toast.LENGTH_LONG).show();

                        galleryAdapter = new Adapterimg(getApplicationContext(),mArrayUri);
                        galleria.setAdapter(galleryAdapter);
                        galleria.setVerticalSpacing(galleria.getHorizontalSpacing());
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) galleria
                                .getLayoutParams();
                        mlp.setMargins(0, galleria.getHorizontalSpacing(), 0, 0);

                    }
                    //Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                } else {
                    Uri mImageUri=data.getData();

                    imagesEncodedList.add(getRealPath(this,mImageUri));

                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new Adapterimg(getApplicationContext(),mArrayUri);
                    galleria.setAdapter(galleryAdapter);
                    galleria.setVerticalSpacing(galleria.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) galleria
                            .getLayoutParams();
                    mlp.setMargins(0, galleria.getHorizontalSpacing(), 0, 0);
                }
            } else {
                Toast.makeText(this, "pickea una imagen",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private String getRealPath(Context context, Uri mImageUri) {
        String path=null;
        Cursor cursor=context.getContentResolver().query(mImageUri,
                null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
            int i=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path=cursor.getString(i);
            cursor.close();
        }
        return path;
    }
}