
/*
 * Bicyweather - easy weather forecast using data from DWD Germany
 * Copyright (C) 2023 Phasenrauscher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// https://stackoverflow.com/questions/2537238/how-can-i-get-zoom-functionality-for-images?rq=3

package de.phasenrauscher.bicyweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

//import com.github.barteksc.pdfviewer.PDFView;
import com.ortiz.touchview.TouchImageView;
//import de.phasenrauscher.bicyweather.R;

public class Map extends AppCompatActivity {

    //PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // you can follow YT video by SARTHI Technologies : "https://www.youtube.com/watch?v=UmawUM7Af3g"
/*
        pdfView = findViewById(R.id.pdfView);

        pdfView.enableAntialiasing(true);
        //pdfView.setMidZoom(12);
        //pdfView.setMaxZoom(30);
        pdfView.setMidZoom(5);
        pdfView.setMaxZoom(20);
        pdfView.fromAsset("stationsmap_de.pdf").load();

*/
        TouchImageView mapView = (TouchImageView) findViewById(R.id.stationsmap);
        mapView.setDoubleTapScale(5);
        mapView.setMaxZoom(30);
        mapView.setImageResource(R.drawable.stationsmap_de);
    }



    public void back_map(View v) {
        Intent map_back=new Intent(Map.this,MainActivity.class);
        startActivity(map_back);

    }




}