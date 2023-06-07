package de.phasenrauschen.bicyweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;

public class Map extends AppCompatActivity {

    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // show map of all DWD stations with pdf viewer by "https://github.com/barteksc/AndroidPdfViewer"
        // you can follow YT video by SARTHI Technologies : "https://www.youtube.com/watch?v=UmawUM7Af3g"
        pdfView = findViewById(R.id.pdfView);
        //pdfView.fromAsset("messnetzkarte_boden.pdf").load();
        pdfView.fromAsset("messnetz_kl.pdf").load(); // it better fits to available stations, but not 100%

    }



    public void back_map(View v) {
        Intent map_back=new Intent(Map.this,MainActivity.class);
        startActivity(map_back);

    }




}