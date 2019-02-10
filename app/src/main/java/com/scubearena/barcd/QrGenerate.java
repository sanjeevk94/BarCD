package com.scubearena.barcd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QrGenerate extends AppCompatActivity {
    private static final String TAG =null ;
    ImageView imageView;
    Bitmap bitmap = null;
    String category;
    String qrCodeData;
    EditText NormalText,Name,Mobile,Email,PrNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);
        NormalText = findViewById(R.id.NormalText);
        Name = findViewById(R.id.Name);
        Mobile =  findViewById(R.id.Mobile);
        Email =  findViewById(R.id.Email);
        PrNumber = findViewById(R.id.PrNumber);
        NormalText.setVisibility(View.INVISIBLE);
        Name.setVisibility(View.INVISIBLE);
        Mobile.setVisibility(View.INVISIBLE);
        Email.setVisibility(View.INVISIBLE);
        PrNumber.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        if("Normal Text".equalsIgnoreCase(category))
        {
        NormalText.setVisibility(View.VISIBLE);
        }
        else if("Contact".equalsIgnoreCase(category))
        {
        Name.setVisibility(View.VISIBLE);
        Mobile.setVisibility(View.VISIBLE);
        Email.setVisibility(View.VISIBLE);
        }
        else if("Product".equalsIgnoreCase(category))
        {
        Name.setVisibility(View.VISIBLE);
        PrNumber.setVisibility(View.VISIBLE);
        }

        Button btn = findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrGenerator(v);
            }
        });
    }
    public void qrGenerator(View v){
        try {

            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallestDimension = width < height ? width : height;

            String  normalText,name,mobile,email,prNumber;
            if("Normal Text".equalsIgnoreCase(category))
            {
            normalText = NormalText.getText().toString();
            qrCodeData = normalText;
            NormalText.setText("");
            }
            else if("Contact".equalsIgnoreCase(category))
            {
            name = Name.getText().toString();
            mobile = Mobile.getText().toString();
            email = Email.getText().toString();
            qrCodeData = name+" "+mobile+" "+email;
            Name.setText("");
            Mobile.setText("");
            Email.setText("");
            }
            else if("Product".equalsIgnoreCase(category))
            {
            name = Name.getText().toString();
            prNumber = PrNumber.getText().toString();
            qrCodeData = name+" "+prNumber;
            Name.setText("");
            PrNumber.setText("");
            }
            //setting parameters for qr code
            String charset = "UTF-8"; // or "ISO-8859-1"
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            createQRCode(qrCodeData, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
            Log.e("QrGenerate",ex.getMessage());
        }
    }

    public  void createQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){

        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view
            final ImageView myImage = (ImageView) findViewById(R.id.imageView1);
            myImage.setImageBitmap(bitmap);
            myImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    shareImage(bitmap);
                    return true;
                }
            });
        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
    }

    private void shareImage(Bitmap bitmap){
        // save bitmap to cache directory
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }


}
