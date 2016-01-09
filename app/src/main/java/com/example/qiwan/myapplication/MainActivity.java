package com.example.qiwan.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE  = 847;
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private Button reset1;
    private Button reset2;
    private Button reset3;
    private Button load;
    private Button save;
    private ImageView imageView;
    private int screenWidth;
    private Matrix matrix;
    private int rotate;
    private Bitmap bitmap;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar1 = (SeekBar)findViewById(R.id.sb_bar1);
        seekBar2 = (SeekBar)findViewById(R.id.sb_bar2);
        seekBar3 = (SeekBar)findViewById(R.id.sb_bar3);
        reset1 = (Button)findViewById(R.id.btn_reset1);
        reset2 = (Button)findViewById(R.id.btn_reset2);
        reset3 = (Button)findViewById(R.id.btn_reset3);
        load = (Button)findViewById(R.id.btn_load);
        save = (Button)findViewById(R.id.btn_save);
        imageView = (ImageView)findViewById(R.id.iv_view);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        seekBar1.setMax(screenWidth);
        seekBar1.setProgress(screenWidth);
        seekBar2.setMax(254);
        seekBar2.setProgress(254);
        seekBar3.setMax(360);
        seekBar3.setProgress(180);
        matrix = new Matrix();
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageView.setLayoutParams(new LinearLayout.LayoutParams(progress, progress * 3 / 4));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageView.setImageAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                matrix.setRotate(progress - 180);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(newBitmap);
                imageView.setImageAlpha(seekBar2.getProgress());
                rotate = progress - 180;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        reset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar1.setProgress(seekBar1.getMax());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(seekBar1.getMax(), seekBar1.getMax() * 3 / 4));
            }
        });

        reset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar2.setProgress(seekBar2.getMax());
                imageView.setImageAlpha(seekBar2.getMax());
            }
        });

        reset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matrix.setRotate(-rotate);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(newBitmap);
                imageView.setImageAlpha(seekBar2.getProgress());
                rotate = 0;
                seekBar3.setProgress(rotate + 180);
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OutputStream fOut = null;
                    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myTestPath";
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File file = new File(fullPath + File.separator + "edit1.jpg");
                    if (!file.exists())
                        file.createNewFile();
                    fOut = new FileOutputStream(file);
                    Bitmap newBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    Toast.makeText(MainActivity.this, "File saved as " + file.getName(), Toast.LENGTH_LONG).show();
                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE)
            if (resultCode == Activity.RESULT_OK){
                selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            }
    }
}
