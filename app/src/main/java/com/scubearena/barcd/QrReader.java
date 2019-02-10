package com.scubearena.barcd;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;




public class QrReader extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    String TAG="QRREADER";
    private WebView webView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        /*// Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // call the alert dialog
        Alert(rawResult);*/


        if (rawResult != null) {
            mScannerView.stopCamera();
            String url = rawResult.getText().toString();
                if(!url.contains("http")) {
                    url="https://www.google.co.in/search?q="+url;
                }
                Intent newIntent = new Intent(QrReader.this, WebActivity.class);
                newIntent.putExtra("url", url);
                startActivity(newIntent);

        }


    }

    public void Alert(Result rawResult){
        AlertDialog.Builder builder = new AlertDialog.Builder(QrReader.this);
        builder.setTitle("Qr scan result");
        builder.setMessage("Result :"+rawResult.getText()+"\nType :"+rawResult.getBarcodeFormat().toString())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // back to previous activity
                        finish();

                    }
                })
                .setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
// If you would like to resume scanning, call this method below:
                        mScannerView.resumeCameraPreview(QrReader.this);
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(QrReader.this,MainActivity.class);
        startActivity(intent);
    }
}