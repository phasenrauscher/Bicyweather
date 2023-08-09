
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


package de.phasenrauscher.bicyweather;

//https://stackoverflow.com/questions/39363661/how-to-store-image-using-downloader-manager-downloaded-image-name-in-external-st
//https://stackoverflow.com/questions/39434742/proper-way-to-separate-class-download-manager
//https://github.com/flegare/JAV387_LaboWidget/blob/master/src/com/mobidroid/widgetfact/service/FactService.java
//https://stackoverflow.com/questions/8937817/downloadmanager-action-download-complete-broadcast-receiver-receiving-same-downl
//http://www.java2s.com/example/java-api/android/app/downloadmanager/action_download_complete-2.html
//https://stackoverflow.com/questions/14930908/how-to-delete-all-files-and-folders-in-one-folder-on-android/14930997
//https://stackoverflow.com/questions/4943629/how-to-delete-a-whole-folder-and-content
//https://stackoverflow.com/questions/20986245/android-downloadmanager-broadcastreceiver-called-multiple-times
//LAST 9.1.2021: https://www.big-app.de/broadcast-receiver-in-android/ onResume / onPause methods for dynamic broadcast Receiver added to avoid multiple broadcast triggering
//http://www.gadgetsaint.com/a ndroid/download-manager/
//https://codinginflow.com/tutorials/android/broadcastreceiver/part-8-goasync
//https://www.nextpit.de/forum/621973/hat-jemand-eine-kurze-anleitung-wie-man-ein-image-durch-button-klick-in-einer-imageview-aendern-kann
//AppName: AndroidManifest.xml: android:label="@string/app_name"
//https://stackoverflow.com/questions/4436923/xml-string-parsing-in-android
//https://www.demo2s.com/android/android-documentbuilder-parse-inputsource-is-parse-the-content-of-the.html
//https://stackoverflow.com/questions/44006729/java-using-documentbuilder-xml-parser-in-android-studio
//https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
//https://stackoverflow.com/questions/11340776/passing-a-list-from-one-activity-to-another    <-- data exchange between activities

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.phasenrauscher.bicyweather.R;


public class MainActivity extends AppCompatActivity{
    //public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    private static final String TAG = "MainActivity";
    private static ArrayList<String> test;
    private static float testfloat;
    public static MainActivity mainActivity;

    //SQLiteDatabase checkDB value= null;
    Integer locname;
    String locationName;
    //SQLiteDatabase DB;
    //DBHelper DB;
    String dbIcon_aw, dbTemp_aw, dbhid;
    Boolean d;

    private static final int REQUEST_CODE = 100;
    /* TODO: generate correct csv name according to ID */

    String imageName_csv = "beob.csv";

    //String[] WeatherStationsIDArray = null;
    //String[] WeatherStationsNameArray = null;
    ArrayList<String> WeatherStationsNameArray = new ArrayList<>();
    ArrayList<String> WeatherStationsIDArray = new ArrayList<>();

    String hid = "10776";


    Integer WeatherForecastIconFailure = 0;
    Integer WeatherForecastTemperatureFailure = 0;
    Integer WeatherForecastFileFailure = 0;
    String unzipfilename = "";
    ArrayList<Long> broadcastQueue = new ArrayList<>();
    //static ArrayList<CityItem> cityList = new ArrayList<>();  // changed from List<CityItem> to ArrayList<CityItem>; 16OKT22
    static ArrayList<CityItem> cityList;  // cityList konsistent über mehrere activities ?
    String[] icons_dwd_current_weather = {"empty", "skc", "few", "sct", "ovc", "fg", "fg", "shra", "ra", "ra", "fzra", "fzra", "ip", "ip", "sn", "sn", "sn", "sn", "hi_shwrs", "hi_shwrs", "rasn", "rasn", "rasn", "rasn", "rasn", "rasn", "hi_tsra", "tsra", "tsra", "tsra", "tsra", "wind", "empty"};

    private HashMap<String, WeatherStation> mapweatherstations = new HashMap<>();
    //Map<String, WeatherStation> availableWeatherStations = new HashMap<>();
    //Map<String, String> WeatherStationsNameID = new HashMap<>();
    Map<String, String> sortedMapAsc = new HashMap<>();

    /* TODO: stations will be choosen by >ID number< by user; ID is part of the filename (10776 = Regensburg) that has to be downloaded */
    String imageURL_kmz = "https://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_L/single_stations/10776/kml/MOSMIX_L_LATEST_10776.kmz";
    String imageURL_csv = "https://opendata.dwd.de/weather/weather_reports/poi/10776-BEOB.csv";

    //String imageName_kmz = "MOSMIX_L_LATEST_10776.kmz";
    String imageName_kmz = "mosmix_l_latest.kmz";
    private long enqueue;
    private DownloadManager dm;
    //public int xStationsOnweb;

    ArrayList<String> StationsOnWeb = new ArrayList<>();

    //List<String> StationsOnWeb = new ArrayList<>();
    //ArrayList<String> mainStationsOnWeb = new ArrayList<>();
    // optional init; will be overwritten by database DB1

    //FloatingActionButton fab;

    //
    String[] dbminmaxtemps = {"--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--"};
    String[] dbIcons = {"error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error"};
    String dblocationName = "City";


    /********************************************************************************************************************************************************/
    /* Thread data handler  */
    /********************************************************************************************************************************************************/

    Handler mainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DwdHtml.whatDwdHtml:

                    Log.d(TAG, "HandlerStationsOnWeb: " + msg.getData().getStringArrayList("DwdHtml"));
                    StationsOnWeb = msg.getData().getStringArrayList("DwdHtml");
                    break;


                case Mosmix.whatMosmix:
                    mapweatherstations = (HashMap<String, WeatherStation>) msg.getData().getSerializable("Mosmix");
                    //System.out.println("HandlerMapweatherstations: " + mapweatherstations.keySet().size());
                    Log.d(TAG, "HandlerMapweatherstations: " + mapweatherstations.keySet().size());
                    break;

                default: break;
            }
        }
    };


    /********************************************************************************************************************************************************/
    /********************************************************************************************************************************************************/

    final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor c = null;
            try {

                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.i("Download ID", "Download in progress: " + downloadId);
                    broadcastQueue.remove(downloadId);
                    //Toast.makeText(context, "ID:" + downloadId, Toast.LENGTH_SHORT).show();


                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    c = dm.query(query);


                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            //String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //Toast.makeText(context, "Broadcast: File has been downloaded successfully." + enqueue, Toast.LENGTH_SHORT).show();

                            /*TODO: action when download ready*/

                        } else if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex)) {
                            int columnReason = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                            int reason = c.getInt(columnReason);
                            switch (reason) {

                                case DownloadManager.ERROR_FILE_ERROR:
                                    Toast.makeText(context, "Download Failed.File is corrupt.", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                    Toast.makeText(context, "Download Failed.Http Error Found.", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                    Toast.makeText(context, "Download Failed due to insufficient space in internal storage", Toast.LENGTH_LONG).show();
                                    break;

                                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                    Toast.makeText(context, "Download Failed. Http Code Error Found.", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_UNKNOWN:
                                    Toast.makeText(context, "Download Failed.", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_CANNOT_RESUME:
                                    Toast.makeText(context, "ERROR_CANNOT_RESUME", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    Toast.makeText(context, "ERROR_TOO_MANY_REDIRECTS", Toast.LENGTH_LONG).show();
                                    break;
                                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                    Toast.makeText(context, "ERROR_DEVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
                                    break;

                            }
                        }
                    }


                }
                if (broadcastQueue.isEmpty()) {
                    //Toast.makeText(context, "All files has been downloaded successfully.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, hid, Toast.LENGTH_SHORT).show(); // moved from behind downloadImage(imageURL_csv, imageName_csv, getApplicationContext()); to here  25.12.21

                    //c.close();
                    parse2Files();
                }

            } // end try
            catch (Exception e) {
                Toast.makeText(context, "Error in BroadcastReceiver/OnReceive!", Toast.LENGTH_SHORT).show();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } // end OnReceive
    }; // end broadcast receiver

    /********************************************************************************************************************************************************/

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


        @Override
        protected void onStart() {
            super.onStart();
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        @Override
        protected void onStop() {
            super.onStop();
            unregisterReceiver(receiver);
        }
    */

        @Override
        protected void onResume() {
            super.onResume();
            //Receiver- und Intent-Filter-Objekt an "registerReceiver"-Methode übergeben
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        @Override
        protected void onPause() {
            super.onPause();
            unregisterReceiver(receiver);
        }

    /*
        @Override
        protected void onRestart() {
            super.onRestart();
            //Receiver- und Intent-Filter-Objekt an "registerReceiver"-Methode übergeben
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    */
    /********************************************************************************************************************************************************/
    /********************************************************************************************************************************************************/
    /********************************************************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;


        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // always in portrait mode - > landscape/portrait OK, as data are restored from Database
        // Init Broadcast Receiver (Intentfilter, Receiver) commented on  13.12.21
        // registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); // removed 25.12.21, new combi: OnCreate/Destroy 26.12.21

        // activate if you want more debugging information about "A resource failed to call close."
        /*
        try {
            Class.forName("dalvik.system.CloseGuard")
                    .getMethod("setEnabled", boolean.class)
                    .invoke(null, true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        */

        // storage runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }


        // if data base does not exits, create one with default values
        String dbname = "Userdata.db";
        File dbFile = this.getDatabasePath(dbname);
        d = dbFile.exists();

        if(!d) {

        // if no data base exists, create db with defaults
        DB_default();

        /****** store cityList to database - not needed  **********/
        /*
        DBHelper DB = new DBHelper(this);
            cityList.forEach(al -> {
                Boolean checkupdatedata = DB.updateuserdata(al.getCityId(), al.getCityName(), "weatherstations");
                if (checkupdatedata == true) {
                } else {
                    Boolean checkinsertdata = DB.insertuserdata(al.getCityId(), al.getCityName(), "weatherstations");
                }
            });
        DB.close();
        */
        /*********************************************************/
        }


        //TODO: write locationName to DB or transfer cityList to settings and generate Name there 4 MAY 2023

        dbFile = this.getDatabasePath(dbname);
        d = dbFile.exists();  // now we created a new database and d is now valid




        /* read default locations from  class Location.java having cityList available after first start or if no data base exists yet at the very first start */
        /* will be overwitten with actual data later, once the first actualization was done */
        /* no need to store data to database 4 MAY23 */
        Location ncityList = new Location();
        cityList = ncityList.getLocationArray();
        //cityList.forEach (al -> System.out.format("DefaultLocation: %s, %s)\n", al.getCityId(), al.getCityName()));


        /************************************************************************************************************************/
        /* Mainactivity: read data base and update GUI */
        /* MARK1 */
        /************************************************************************************************************************/

        GUI_update_with_database_content();


    }  // end OnCreate

    /*######################################################################################################################*/


    @Override
    protected void onStart() {
        super.onStart();
        /***************************************************** floating action button *******************************************/
        /************************************************************************************************************************/
        /* create floating action button */
        /************************************************************************************************************************/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View view) {

        // ArrayList<String> StationsOnWeb = new ArrayList<>();


        /***************************************************** Thread 1 and 2 Sync  *********************************************/
        /************************************************************************************************************************/
        /*
        What:  thread 1 and thread 2 started; after they have done their job both th1 and th2, we can continue
         */
        /************************************************************************************************************************/

            Thread th1 = new Thread(new DwdHtml(MainActivity.this), "DwdHtml");
            Thread th2 = new Thread(new Mosmix(MainActivity.this), "Mosmix");

            th1.start();
            th2.start();

            try {
                th1.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                System.out.println("th1 exception");
            }
            try {
                th2.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                System.out.println("th2 exception");
            }


            /************  REMOVED generate the ArrayList<CityItem> cityList for the AutoCompleteAdapter *********************************/
            /************  REMOVED cityList generation removed from here as Handler from Th1/th2 provides data very late *****************/

            //System.out.println("PUPS" + mapweatherstations.keySet().toString());

            /*cityList:
                            ...
                            10954, ALTENSTADT
                            O510,  ALTGERINGSWALDE
                            03044, ALTNAHARRA
                            P629,  ALTOMUENSTER-MAISBRU
                            K685,  ALZEY
                            ...
             */

            //cityList = GenerateCityList(mapweatherstations, StationsOnWeb);

            /************  REMOVED read hid from DataBase, find out the matching locationName with help of cityList  ********************/

            /*
            List<String> retValue = null;
            retValue =GetLocationNamefromDB (d, cityList);
            hid = retValue.get(0);
            locationName = retValue.get(1);
            */

            /***************************************************************************************************************************/

            // read back locationID from database: location selected in settings.java and stored by AutoCompleteCityAdapter MARK2
            SQLiteDatabase DB;
            DB = openOrCreateDatabase("Userdata.db", Context.MODE_PRIVATE, null);
            Cursor c = DB.rawQuery("Select * from Userdetails", null);
            StringBuffer buffer = new StringBuffer();

            while (c.moveToNext()) {

               if (c.getString(1).equals("weatherdata")) {
                 if ((c.getString(2)).equals("location")) {
                    buffer.append(c.getString(3));
                 }
                }
            }
            DB.close();

            hid = buffer.toString();





                /*****************************************************  Download  *******************************************************/

            PrepaireAndStartDownload(hid);

            /************************************************************************************************************************/

         } // end OnClick
        }); // end OnClickListener
    } // end OnStart
    /*######################################################################################################################*/








    /***************************************************** Functions ********************************************************/
    /************************************************************************************************************************/

    public void PrepaireAndStartDownload(String fhid) {

        // DownloadManager will be triggered;
        // result will be reported via BroadcastReceiver; follows GetLocationfromDB()

        imageURL_kmz = "https://opendata.dwd.de/weather/local" +
                "_forecasts/mos/MOSMIX_L/single_stations/" + fhid + "/kml/MOSMIX_L_LATEST_" + fhid + ".kmz";

        // add a "_" at the end of the name is required for all stations that have less than 5 signs (look at DWD site)
        if (hid.toString().length() < 5) {
            imageURL_csv = "https://opendata.dwd.de/weather/weather_reports/poi/" + fhid + "_" + "-BEOB.csv";
        } else {
            imageURL_csv = "https://opendata.dwd.de/weather/weather_reports/poi/" + fhid + "-BEOB.csv";
        }

        //imageURL_csv = "https://opendata.dwd.de/weather/weather_reports/poi/" + fhid + "-BEOB.csv";
        System.out.println("csv:" + imageURL_csv);
        System.out.println("kmz:" + imageURL_kmz);


        final File deldir = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        deleteRecursive(deldir);

        downloadImage(imageURL_kmz, imageName_kmz, getApplicationContext());
        downloadImage(imageURL_csv, imageName_csv, getApplicationContext());
        //Toast.makeText(MainActivity.this, hid, Toast.LENGTH_SHORT).show();
    }



    /************************************************************************************************************************/
    /*
        Input:  String[] StationsOnWeb,
                HashMap<String, WeatherStation> mapweatherstations
        Output: Map<String, WeatherStation> availableWeatherStations (info about the weatherstations, actually no need),
                Arraylist<CityItem> cityList
        What:   for all "StationsOnWeb" (stations available on DWD html site) we get the data from hashmap "mapweatherstations" (all stations from Mosmix file)
                which contains all data for all weatherstation

        availableWeatherStations:   Map<String, WeatherStation> with all stations data (key and value) that are available on DWD server
        StationsOnWeb:              arraylist [strings] with all stations ID that are available on DWD server
        cityList:                   fills the AutocompleteAdapter with city names
        */
    /************************************************************************************************************************/

    public ArrayList<CityItem> GenerateCityList(HashMap<String, WeatherStation> GCLmapweatherstations, ArrayList<String> GCLStationsOnWeb) {

        // follows thread Html and Mosmix; important to update AutoCompleteAdapter

        Map<String, WeatherStation> GCLavailableWeatherStations = new HashMap<>();
        Map<String, String> GCLWeatherStationsNameID = new HashMap<>();
        ArrayList<CityItem> GCLcityList = new ArrayList<>();

        try {
        int nine_seventy = 0;

        Log.d(TAG, "GCLStationsOnWeb: " + GCLStationsOnWeb); // wird erst geupdated, wenn man ins setup gesprungen war!, ne wenn man den update Knopf das zweite mal gedrückt hat!
        Log.d(TAG, "GCLStationsOnWebSize: " + GCLStationsOnWeb.size());
        //System.out.println("GCLStationsOnWebSize: " + GCLStationsOnWeb.size());
        //xStationsOnweb = StationsOnWeb.size();

        for (Map.Entry<String, WeatherStation> stations : GCLmapweatherstations.entrySet()) {
            // extract stations that fit to WeatherStationsID from html site
            for (nine_seventy = 0; nine_seventy < GCLStationsOnWeb.size(); nine_seventy++) {
                if (stations.getKey().equals(GCLStationsOnWeb.get(nine_seventy))) {
                    //19SEPT22
                    //System.out.format("Name: %s \n", stations.getValue().ReadWeatherStationsName() + stations.getKey());
                    GCLavailableWeatherStations.put(stations.getKey(), stations.getValue());
                }
            }
        }


        for (Map.Entry<String, WeatherStation> stations : GCLavailableWeatherStations.entrySet()) {
            //System.out.format("ID: %s  Name: %s GPS: %.2f  %.2f Elevation: %2d \n", stations.getKey(), stations.getValue().ReadWeatherStationsName(), stations.getValue().ReadWeatherStationsLatitude(), stations.getValue().ReadWeatherStationsLongitude(), stations.getValue().ReadWeatherStationsElev() );
            //TODO: create new hashmap WeatherStationsNameID<String ID,String Name> then sort by station names 22SEPT22
            GCLWeatherStationsNameID.put(stations.getKey(), stations.getValue().ReadWeatherStationsName());
        }

        // sort alphabetically with sortByComparator function ->  Map<String, Integer> sortedMapAsc = sortByComparator(unsortedhashmap<String, Integer>);
        Map<String, String> sortedMapAsc = sortByComparator(GCLWeatherStationsNameID);

        // Test and output
        //for (Map.Entry<String, String> stations : sortedMapAsc.entrySet()) {
        //System.out.println("NAME_ID**Name:" + stations.getValue().toString() + "  ID:  " + stations.getKey());
        //}

        // deactivate following for-loop when "Autocomplete" code is ready developed
                   /*
                    for (Map.Entry<String, String> stations : sortedMapAsc.entrySet()) {
                        WeatherStationsIDArray.add(stations.getKey());
                        WeatherStationsNameArray.add(stations.getValue());
                    }
                    */

        // create alphabetical sorted "Arraylist<CityItem> cityList"; handover to autocomplete in settings activity
        for (Map.Entry<String, String> stations : sortedMapAsc.entrySet()) {
            GCLcityList.add(new CityItem(stations.getValue(), stations.getKey()));
        }
        //GCLcityList.forEach(cl -> System.out.format("cityList.add(new CityItem(   \"%s\",    \"%s\"));\n", cl.getCityName(), cl.getCityId()));

        } catch (NumberFormatException e) {
        e.printStackTrace();
        }
        return(GCLcityList);
    }


    /************************************************************************************************************************/
    /***************************************************** Autocomplete adapter data ****************************************/
    /************************************************************************************************************************/
        /*
        What:  read ID from DataBase to "hid" and generate data for AutoCompleteAdapter ArrayList<CityItem> cityList
        */
    /************************************************************************************************************************/

    /***************************************************** check data base **************************************************/


    public List<String> GetLocationNamefromDB (Boolean fd, ArrayList<CityItem> fcityList) {      // important when return from settings

        String flocationName = null,
        fhid = null;
        List<String> returnV = null;

        /* retrieve <String>cityId from data base DB, stored by the settings_activity*/
        try
         {

        if (!fd) {

            Toast.makeText(MainActivity.this, "Data Base not exists! ", Toast.LENGTH_SHORT).show();

        } else {

            // read back location from database: location selected in settings.java and stored by AutoCompleteCityAdapter MARK2
            SQLiteDatabase DB;
            DB = openOrCreateDatabase("Userdata.db", Context.MODE_PRIVATE, null);
            Cursor c = DB.rawQuery("Select * from Userdetails", null);
            StringBuffer buffer = new StringBuffer();

            //c.moveToPosition(0); // second Row (1), first row (0)
            //buffer.append(c.getString(2)); //third column (2)


            while (c.moveToNext()) {
                if (c.getString(1).equals("weatherdata")) {
                if ((c.getString(2)).equals("location")) {
                    buffer.append(c.getString(3));
                }
              }
            }
            DB.close();

            fhid = buffer.toString();
            System.out.println("HID: " + fhid);

            //fcityList.forEach(cl -> System.out.format("cityList %s, %s \n", cl.getCityId(), cl.getCityName()));

            //find out the locationName(hid) from cityList for the hid
            for (CityItem ci : fcityList) {
                if (ci.getCityId().equals(fhid)) {
                    flocationName = ci.getCityName();
                }
            }
            System.out.println("GetLocationNamefromDB:locationName:" + flocationName);
            // return this values
            returnV =Arrays.asList(fhid, flocationName);
            //ArrayList<CityItem> arraylist = new ArrayList<CityItem>();   // 16OKT22; skipped 29JAN23

            //c.close();
            DB.close();
        }

        } catch(
        Throwable ex)
        {
        Toast.makeText(MainActivity.this, "Database exception: No location defined yet!", Toast.LENGTH_SHORT).show();
        }
        return(returnV);
    }


    /************************************************************************************************************************/

    public void downloadImage(String url, String outputFileName, Context context) {

        try {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(outputFileName);
            request.setDescription("Downloading " + outputFileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


            //request.allowScanningByMediaScanner(); //  depricated since Q 10.0

            // Download folder (not accessible in phone beginning with android 11 !!):
            // /sdcard/Android/data/de.phasenrauscher.csvreader/files/data/dwdbeobRegensburg.csv
            request.setDestinationInExternalFilesDir(this, Environment.getDataDirectory().getAbsolutePath(), outputFileName);

            //Toast.makeText(this, Environment.getDataDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

            /* TODO there was a null pointer exception here -> OK root cause: "dm" was declared a second time in downloadImage() ! */
            // final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE); // please no declaration of dm here, do not use this code


            dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            enqueue = dm.enqueue(request);
            broadcastQueue.add(enqueue);
            //registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //removed 25DEZ21
        } catch (Exception e) {
            Toast.makeText(this, "DownloadImage Error!", Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(this, outputFileName + " ->  N°:  " + enqueue, Toast.LENGTH_SHORT).show();

    }

    /*******************************************************************************************************************************************************/

    public void parse2Files() {

        Map<String, wwTTTForecast> fw = null;
        List<String> aw = null;
        String Icon_aw = "empty";
        //String Temp_aw = "0.0";

        try {
            aw = readFileAw(imageName_csv);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DUDU", "No data file available!");
            Toast.makeText(this, "no aw file !", Toast.LENGTH_SHORT).show();
        }


        if ((!((aw).get(35).contains("---"))) && (!(aw.get(35).isEmpty()))) {
            Icon_aw = icons_dwd_current_weather[Integer.valueOf(aw.get(35))];
        } else {
            Icon_aw = "empty";
        }

        String Temp_aw = aw.toArray ()[9].toString();


        try {

            // Parser of weather forecast
            /* input: kml file, output: fw */
            /* kml file name info from unzip info */
            /* read back the unzipped .kml file (XML format) and returns fw, which is containing one temperature (ww) and one weather code (TTT) per hour for the next 10 days, beginning by today. */
            /* ICONS in "Icons" and the temperatures " temperatures" will be extracted for MEZ 9:00, 12:00, 15:00, 18:00 for the next 7 days */
            unzipFile(imageName_kmz);
            fw = ReadMosmixFile(unzipfilename.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DUDU", "Format failure in kml file !");
            Toast.makeText(this, "KML / FW Error!", Toast.LENGTH_SHORT).show();
            WeatherForecastFileFailure = 1;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DUDU", "Unknown error while parsing .kml file!");
            Toast.makeText(this, "Unknown Kml / fw Error!", Toast.LENGTH_SHORT).show();
            WeatherForecastFileFailure = 1;

        }

        // in case of error: kml file corrupt or missing: use default values, otherwise they will be overwritten by regular values
        //WeatherForecastFileFailure = 1; // for testing only
        //String[] minmaxtemps = new String[30];
        String[] minmaxtemps = {"--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--"};
        String[] Icons = {"error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error", "error"};

        if (WeatherForecastFileFailure != 1) {

            // output the hashmap for check
            int num = 0;

                        /*
                        for (Map.Entry<String, wwTTTForecast> WeatherForecastHours: fw.entrySet()) {
                            num++;
                            Log.d("DUDU", "Time" + WeatherForecastHours.getKey() + "Temp" + WeatherForecastHours.getValue().ReadTemp2m() + "Weathercode" + WeatherForecastHours.getValue().ReadWeatherCode());
                        } */


            /* parse fw map */
            // input: fw file, output: Icons, temperatures */

            /* TODO: string of "Icons" is containing the [string] code for the icons in an array */

            try {
                Icons = Parse_forecast_weather.ParseKMLIcon(fw);
            } catch (Exception e) {
                WeatherForecastIconFailure = 1;
                Log.d("DUDU", "Error while parsing Icons!");
                Toast.makeText(this, "Icons Error!", Toast.LENGTH_SHORT).show();
            }

            /* TODO: list of "temperatures" is storing min/max values for all 7 days in sequence*/
            //List<Double> temperatures = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0); //Usage of API documented as @since 1.9+
            //This Init works in Android 9 (API28)
            List<Double> temperatures = new ArrayList<Double>();
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);
            temperatures.add (0.0);temperatures.add (0.0);


            try {
                temperatures = Parse_forecast_weather.ParseKMLTemperature(fw);
            } catch (Exception e) {
                WeatherForecastTemperatureFailure = 1;
                Log.d("DUDU", "Error while calculating min/max temperatures!");
                Toast.makeText(this, "Min / Max Temp Error!", Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(context, temperatures.get(13).toString(), Toast.LENGTH_SHORT).show();


            /************************************************************************************************************************/

            /* TODO: Toast or printf of all final weather data to check if they are correct */


            int i = 0;
            for (Double nn : (temperatures)) {

                //Log.d("SHOW_TEMP", String.valueOf(Math.round(nn)));
                minmaxtemps[i] = String.valueOf(Math.round(nn));
                i++;

            }
            //Toast.makeText(this, "TEMP / ICON: " + retValue.toArray()[9] + "°C" + " / " + icons_dwd_current_weather[Integer.valueOf(retValue.get(35))], Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, Arrays.toString(temperatures.toArray()), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, Arrays.toString(Icons), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "DwdHtml: " +  String.valueOf(xStationsOnweb) + "   " + "MOSMIX: " + mapweatherstations.keySet().size() + "\n\n" + aw.toArray()[9] + "°C" + " / " + icons_dwd_current_weather[Integer.valueOf(aw.get(35))] + "\n\n" + Arrays.toString(minmaxtemps) + "\n\n" + Arrays.toString(Icons), Toast.LENGTH_SHORT).show();



        } else {
            // TODO: if .kml-file failed, use default icons[] and minmaxtemps[] (declaration in this function head in line 734,735)
            Toast.makeText(this, "WeatherForecastFileFailure!", Toast.LENGTH_SHORT).show();
        }


        /************  read hid from DataBase, find out the matching locationName with help of cityList  ***********************/

            // Handler data are available in the meanwhile  14APR 2023
            cityList = GenerateCityList(mapweatherstations, StationsOnWeb);

            List<String> retValue = null;
            retValue =GetLocationNamefromDB (d, cityList);
            hid = retValue.get(0);
            locationName = retValue.get(1);

        /************************************************************************************************************************/
        /* update data base */
        /* MARK3 */
        /************************************************************************************************************************/


        /* TODO: check if data base exists!! 13MAY23  / 13 DEC 22  MARK3*/
        /* updateSQLiteDB(); */


        DBHelper DB = new DBHelper(this);


        //Log.d("TAG" + TAG,  locationName);
        //Toast.makeText(this, locationName, Toast.LENGTH_SHORT).show();
        Boolean checkupdatedata = DB.updateuserdata("location", hid,"weatherdata");   if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("location", hid,"weatherdata"); }
                checkupdatedata = DB.updateuserdata("locationName", locationName,"weatherdata");   if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("locationName", locationName,"weatherdata"); }
                checkupdatedata = DB.updateuserdata("awIcon", Icon_aw,"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("awIcon", Icon_aw,"weatherdata");}
                checkupdatedata = DB.updateuserdata("awTemp", Temp_aw,"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("awTemp", Temp_aw,"weatherdata");}

                checkupdatedata = DB.updateuserdata("today9Icon",  Icons[0],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("today9Icon",  Icons[0],"weatherdata");}
                checkupdatedata = DB.updateuserdata("today12Icon", Icons[1],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("today12Icon", Icons[1],"weatherdata");}
                checkupdatedata = DB.updateuserdata("today15Icon", Icons[2],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("today15Icon", Icons[2],"weatherdata");}
                checkupdatedata = DB.updateuserdata("today18Icon", Icons[3],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("today18Icon", Icons[3],"weatherdata");}

                checkupdatedata = DB.updateuserdata("tomorrow9Icon",  Icons[4],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrow9Icon",  Icons[4],"weatherdata");}
                checkupdatedata = DB.updateuserdata("tomorrow12Icon", Icons[5],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrow12Icon", Icons[5],"weatherdata");}
                checkupdatedata = DB.updateuserdata("tomorrow15Icon", Icons[6],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrow15Icon", Icons[6],"weatherdata");}
                checkupdatedata = DB.updateuserdata("tomorrow18Icon", Icons[7],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrow18Icon", Icons[7],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day39Icon",  Icons[8],"weatherdata");  if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day39Icon",  Icons[8],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day312Icon", Icons[9],"weatherdata");  if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day312Icon", Icons[9],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day315Icon", Icons[10],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day315Icon", Icons[10],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day318Icon", Icons[11],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day318Icon", Icons[11],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day49Icon",  Icons[12],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day49Icon",  Icons[12],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day412Icon", Icons[13],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day412Icon", Icons[13],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day415Icon", Icons[14],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day415Icon", Icons[14],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day418Icon", Icons[15],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day418Icon", Icons[15],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day59Icon",  Icons[16],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day59Icon",  Icons[16],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day512Icon", Icons[17],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day512Icon", Icons[17],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day515Icon", Icons[18],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day515Icon", Icons[18],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day518Icon", Icons[19],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day518Icon", Icons[19],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day69Icon",  Icons[20],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day69Icon",  Icons[20],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day612Icon", Icons[21],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day612Icon", Icons[21],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day615Icon", Icons[22],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day615Icon", Icons[22],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day618Icon", Icons[23],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day618Icon", Icons[23],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day79Icon",  Icons[24],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day79Icon",  Icons[24],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day712Icon", Icons[25],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day712Icon", Icons[25],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day715Icon", Icons[26],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day715Icon", Icons[26],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day718Icon", Icons[27],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day718Icon", Icons[27],"weatherdata");}



                checkupdatedata = DB.updateuserdata("todaymaxtemp", minmaxtemps[0],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("todaymaxtemp", minmaxtemps[0],"weatherdata");}
                checkupdatedata = DB.updateuserdata("todaymintemp", minmaxtemps[1],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("todaymintemp", minmaxtemps[1],"weatherdata");}

                checkupdatedata = DB.updateuserdata("tomorrowmaxtemp", minmaxtemps[2],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrowmaxtemp", minmaxtemps[2],"weatherdata");}
                checkupdatedata = DB.updateuserdata("tomorrowmintemp", minmaxtemps[3],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("tomorrowmintemp", minmaxtemps[3],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day3maxtemp", minmaxtemps[4],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day3maxtemp", minmaxtemps[4],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day3mintemp", minmaxtemps[5],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day3mintemp", minmaxtemps[5],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day4maxtemp", minmaxtemps[6],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day4maxtemp", minmaxtemps[6],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day4mintemp", minmaxtemps[7],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day4mintemp", minmaxtemps[7],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day5maxtemp", minmaxtemps[8],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day5maxtemp", minmaxtemps[8],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day5mintemp", minmaxtemps[9],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day5mintemp", minmaxtemps[9],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day6maxtemp", minmaxtemps[10],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day6maxtemp", minmaxtemps[10],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day6mintemp", minmaxtemps[11],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day6mintemp", minmaxtemps[11],"weatherdata");}

                checkupdatedata = DB.updateuserdata("day7maxtemp", minmaxtemps[12],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day7maxtemp", minmaxtemps[12],"weatherdata");}
                checkupdatedata = DB.updateuserdata("day7mintemp", minmaxtemps[13],"weatherdata"); if(checkupdatedata==true){} else{  Boolean checkinsertdata = DB.insertuserdata("day7mintemp", minmaxtemps[13],"weatherdata");}


        DB.close();
        updateGUI(locationName, Icons, minmaxtemps, Icon_aw, Temp_aw ); // added (uncommented) 7 APRIL23 after separation of DB and GUI functions

    }  // end parse2Files

    /***************************************************** Functions ********************************************************/
    /************************************************************************************************************************/
    /* functions */
    /* MARK5 */
    /************************************************************************************************************************/

    private void updateGUI(String location_Name, String[] Icons, String[] minmaxtemps, String awIcon, String awTemp){
    // update location text field again

    LocalDate DateNow = LocalDate.now();

        try

    {
// TODO
        TextView db111 = (TextView) findViewById(R.id.mainLOCATION);
        //db111.setText(WeatherStationsNameArray.get(locname));
        db111.setText(location_Name);

        // day of week
    }
        catch(
    Exception e)

    {
        Log.d("TextView Exception:", e.toString());
        Toast.makeText(this, "GUI spinner Error!", Toast.LENGTH_SHORT).show();
    }

        try

    {


        // DayOfWeek dayOfWeek = now.getDayOfWeek();
        // System.out.println( dayOfWeek.plus( 100 ) ); // z.B. SATURDAY

        DayOfWeek dow = LocalDate.now().getDayOfWeek().plus(0);

        //GUI: user to choose language
        //System.out.println( dow.getDisplayName( TextStyle.FULL, Locale.GERMANY ) );


        TextView dow1 = (TextView) findViewById(R.id.today);
        dow1.setText("heute");


        TextView dow2 = (TextView) findViewById(R.id.tomorrow);
        dow2.setText(dow.plus(1).getDisplayName(TextStyle.FULL, Locale.GERMANY));


        TextView dow3 = (TextView) findViewById(R.id.day3);
        dow3.setText(dow.plus(2).getDisplayName(TextStyle.FULL, Locale.GERMANY));


        TextView dow4 = (TextView) findViewById(R.id.day4);
        dow4.setText(dow.plus(3).getDisplayName(TextStyle.FULL, Locale.GERMANY));


        TextView dow5 = (TextView) findViewById(R.id.day5);
        dow5.setText(dow.plus(4).getDisplayName(TextStyle.FULL, Locale.GERMANY));


        TextView dow6 = (TextView) findViewById(R.id.day6);
        dow6.setText(dow.plus(5).getDisplayName(TextStyle.FULL, Locale.GERMANY));


        TextView dow7 = (TextView) findViewById(R.id.day7);
        dow7.setText(dow.plus(6).getDisplayName(TextStyle.FULL, Locale.GERMANY));
    }
            catch(
    Exception e)

    {
        Log.d("TextView Exception:", e.toString());
        Toast.makeText(this, "GUI name or date Error!", Toast.LENGTH_SHORT).show();
    }

        try

    {
        // actual weather now

        // failure treatment if no symbol available
        String sIcon29 = awIcon;
        int res29 = getResources().getIdentifier(sIcon29, "drawable", this.getPackageName());
        ImageView imageview29 = (ImageView) findViewById(R.id.imageView29);
        imageview29.setImageResource(res29);

        TextView acttemptoday = (TextView) findViewById(R.id.textView26);
        //acttemptoday.setText(aw.toArray()[9].toString() + " °C");
        acttemptoday.setText(awTemp + " °C");

        //TextView actlocation = (TextView) findViewById(R.id.spinner);
        //actlocation.setText("Regensburg");

        TextView actdate = (TextView) findViewById(R.id.fctdate);
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        actdate.setText(dateformatter.format(DateNow).toString());
    }
        catch(
    Exception e)

    {
        Log.d("TextView Exception:", e.toString());
        Toast.makeText(this, "GUI actual weather error 'NullPointer->aw.get(35)'!", Toast.LENGTH_SHORT).show();
    }

        try

    {

        //forecast today


        String sIcon3 = Icons[0];
        int res3 = getResources().getIdentifier(sIcon3, "drawable", this.getPackageName());
        ImageView imageview3 = (ImageView) findViewById(R.id.imageView3);
        imageview3.setImageResource(res3);


        String sIcon10 = Icons[1];
        int res10 = getResources().getIdentifier(sIcon10, "drawable", this.getPackageName());
        ImageView imageview10 = (ImageView) findViewById(R.id.imageView10);
        imageview10.setImageResource(res10);


        String sIcon11 = Icons[2];
        int res11 = getResources().getIdentifier(sIcon11, "drawable", this.getPackageName());
        ImageView imageview11 = (ImageView) findViewById(R.id.imageView11);
        imageview11.setImageResource(res11);


        String sIcon12 = Icons[3];
        int res12 = getResources().getIdentifier(sIcon12, "drawable", this.getPackageName());
        ImageView imageview12 = (ImageView) findViewById(R.id.imageView12);
        imageview12.setImageResource(res12);


        TextView mintemptoday = (TextView) findViewById(R.id.textView38);
        mintemptoday.setText(minmaxtemps[0] + "°");


        TextView maxtemptoday = (TextView) findViewById(R.id.textView36);
        maxtemptoday.setText(minmaxtemps[1] + "°");


        //forecast tomorrow


        String sIcon4 = Icons[4];
        int res4 = getResources().getIdentifier(sIcon4, "drawable", this.getPackageName());
        ImageView imageview4 = (ImageView) findViewById(R.id.imageView4);
        imageview4.setImageResource(res4);


        String sIcon = Icons[5];
        int res = getResources().getIdentifier(sIcon, "drawable", this.getPackageName());
        ImageView imageview = (ImageView) findViewById(R.id.imageView);
        imageview.setImageResource(res);


        String sIcon2 = Icons[6];
        int res2 = getResources().getIdentifier(sIcon2, "drawable", this.getPackageName());
        ImageView imageview2 = (ImageView) findViewById(R.id.imageView2);
        imageview2.setImageResource(res2);

        String sIcon13 = Icons[7];
        int res13 = getResources().getIdentifier(sIcon13, "drawable", this.getPackageName());
        ImageView imageview13 = (ImageView) findViewById(R.id.imageView13);
        imageview13.setImageResource(res13);

        TextView mintemptomorrow = (TextView) findViewById(R.id.textView4);
        mintemptomorrow.setText(minmaxtemps[2] + "°");


        TextView maxtemptomorrow = (TextView) findViewById(R.id.textView2);
        maxtemptomorrow.setText(minmaxtemps[3] + "°");


        //forecast day3


        String sIcon5 = Icons[8];
        int res5 = getResources().getIdentifier(sIcon5, "drawable", this.getPackageName());
        ImageView imageview5 = (ImageView) findViewById(R.id.imageView5);
        imageview5.setImageResource(res5);


        String sIcon14 = Icons[9];
        int res14 = getResources().getIdentifier(sIcon14, "drawable", this.getPackageName());
        ImageView imageview14 = (ImageView) findViewById(R.id.imageView14);
        imageview14.setImageResource(res14);


        String sIcon15 = Icons[10];
        int res15 = getResources().getIdentifier(sIcon15, "drawable", this.getPackageName());
        ImageView imageview15 = (ImageView) findViewById(R.id.imageView15);
        imageview15.setImageResource(res15);

        String sIcon16 = Icons[11];
        int res16 = getResources().getIdentifier(sIcon16, "drawable", this.getPackageName());
        ImageView imageview16 = (ImageView) findViewById(R.id.imageView16);
        imageview16.setImageResource(res16);

        TextView mintempday3 = (TextView) findViewById(R.id.textView8);
        mintempday3.setText(minmaxtemps[4] + "°");


        TextView maxtempday3 = (TextView) findViewById(R.id.textView6);
        maxtempday3.setText(minmaxtemps[5] + "°");


        //forecast day4


        String sIcon6 = Icons[12];
        int res6 = getResources().getIdentifier(sIcon6, "drawable", this.getPackageName());
        ImageView imageview6 = (ImageView) findViewById(R.id.imageView6);
        imageview6.setImageResource(res6);


        String sIcon17 = Icons[13];
        int res17 = getResources().getIdentifier(sIcon17, "drawable", this.getPackageName());
        ImageView imageview17 = (ImageView) findViewById(R.id.imageView17);
        imageview17.setImageResource(res17);


        String sIcon18 = Icons[14];
        int res18 = getResources().getIdentifier(sIcon18, "drawable", this.getPackageName());
        ImageView imageview18 = (ImageView) findViewById(R.id.imageView18);
        imageview18.setImageResource(res18);

        String sIcon19 = Icons[15];
        int res19 = getResources().getIdentifier(sIcon19, "drawable", this.getPackageName());
        ImageView imageview19 = (ImageView) findViewById(R.id.imageView19);
        imageview19.setImageResource(res19);

        TextView mintempday4 = (TextView) findViewById(R.id.textView12);
        mintempday4.setText(minmaxtemps[6] + "°");


        TextView maxtempday4 = (TextView) findViewById(R.id.textView10);
        maxtempday4.setText(minmaxtemps[7] + "°");


        //forecast day5


        String sIcon7 = Icons[16];
        int res7 = getResources().getIdentifier(sIcon7, "drawable", this.getPackageName());
        ImageView imageview7 = (ImageView) findViewById(R.id.imageView7);
        imageview7.setImageResource(res7);


        String sIcon20 = Icons[17];
        int res20 = getResources().getIdentifier(sIcon20, "drawable", this.getPackageName());
        ImageView imageview20 = (ImageView) findViewById(R.id.imageView20);
        imageview20.setImageResource(res20);


        String sIcon21 = Icons[18];
        int res21 = getResources().getIdentifier(sIcon21, "drawable", this.getPackageName());
        ImageView imageview21 = (ImageView) findViewById(R.id.imageView21);
        imageview21.setImageResource(res21);

        String sIcon22 = Icons[19];
        int res22 = getResources().getIdentifier(sIcon22, "drawable", this.getPackageName());
        ImageView imageview22 = (ImageView) findViewById(R.id.imageView22);
        imageview22.setImageResource(res22);

        TextView mintempday5 = (TextView) findViewById(R.id.textView16);
        mintempday5.setText(minmaxtemps[8] + "°");


        TextView maxtempday5 = (TextView) findViewById(R.id.textView14);
        maxtempday5.setText(minmaxtemps[9] + "°");


        //forecast day6


        String sIcon8 = Icons[20];
        int res8 = getResources().getIdentifier(sIcon8, "drawable", this.getPackageName());
        ImageView imageview8 = (ImageView) findViewById(R.id.imageView8);
        imageview8.setImageResource(res8);


        String sIcon23 = Icons[21];
        int res23 = getResources().getIdentifier(sIcon23, "drawable", this.getPackageName());
        ImageView imageview23 = (ImageView) findViewById(R.id.imageView23);
        imageview23.setImageResource(res23);


        String sIcon24 = Icons[22];
        int res24 = getResources().getIdentifier(sIcon24, "drawable", this.getPackageName());
        ImageView imageview24 = (ImageView) findViewById(R.id.imageView24);
        imageview24.setImageResource(res24);

        String sIcon25 = Icons[23];
        int res25 = getResources().getIdentifier(sIcon25, "drawable", this.getPackageName());
        ImageView imageview25 = (ImageView) findViewById(R.id.imageView25);
        imageview25.setImageResource(res25);

        TextView mintempday6 = (TextView) findViewById(R.id.textView20);
        mintempday6.setText(minmaxtemps[10] + "°");


        TextView maxtempday6 = (TextView) findViewById(R.id.textView18);
        maxtempday6.setText(minmaxtemps[11] + "°");


        //forecast day7


        String sIcon9 = Icons[24];
        int res9 = getResources().getIdentifier(sIcon9, "drawable", this.getPackageName());
        ImageView imageview9 = (ImageView) findViewById(R.id.imageView9);
        imageview9.setImageResource(res9);


        String sIcon26 = Icons[25];
        int res26 = getResources().getIdentifier(sIcon26, "drawable", this.getPackageName());
        ImageView imageview26 = (ImageView) findViewById(R.id.imageView26);
        imageview26.setImageResource(res26);


        String sIcon27 = Icons[26];
        int res27 = getResources().getIdentifier(sIcon27, "drawable", this.getPackageName());
        ImageView imageview27 = (ImageView) findViewById(R.id.imageView27);
        imageview27.setImageResource(res27);

        String sIcon28 = Icons[27];
        int res28 = getResources().getIdentifier(sIcon28, "drawable", this.getPackageName());
        ImageView imageview28 = (ImageView) findViewById(R.id.imageView28);
        imageview28.setImageResource(res28);

        TextView mintempday7 = (TextView) findViewById(R.id.textView24);
        mintempday7.setText(minmaxtemps[12] + "°");


        TextView maxtempday7 = (TextView) findViewById(R.id.textView22);
        maxtempday7.setText(minmaxtemps[13] + "°");
    }
    catch(
    Exception e)

    {
        Log.d("TextView Exception:", e.toString());
        Toast.makeText(this, "GUI forecast Error!", Toast.LENGTH_SHORT).show();
    }

}

    /******************************************************************************************************************************************************/
    /***************************************************************** sorting ****************************************************************************/


    private static Map<String, String> sortByComparator(Map<String, String> unsortMap)
    {
        List<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, String>>()
        {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /*******************************************************************************************************************************************************/

    public List<String> readFileAw(String iName) {

        String awrow = null;
        List<String> retValue = null;
        /* https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/poi_present_weather_zuordnung_pdf.pdf?__blob=publicationFile&v=2  */

        try {

            /* TODO check if file exists, update old JAVA code below*/
            /* if (!fil.canRead() || !fil.isFile()) throw new IOException("Actual Weather File corrupt or not existing!"); */

            /* /sdcard/Android/data/de.phasenrauscher.csvreader/files/data/dwdbeobRegensburg.csv  */
            File textFile = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), iName);
            //Toast.makeText(this, getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), Toast.LENGTH_SHORT).show();

            FileInputStream fis = new FileInputStream(textFile);

            if (fis != null) {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buff = new BufferedReader(isr);

                int rowcount = 0;

                while ((awrow = buff.readLine()) != null) {

                    //TODO extract the third line containing latest weather data
                    // read the latest weather data ONLY which is located in line N° 3, line 0...2 are the header information

                    if (rowcount == 3) {
                        retValue = Arrays.asList(awrow.split(";"));

                        System.out.println("TEST9:  " + retValue.toArray()[9]);  // actual temperature -> "J"
                        System.out.println("TEST35: " + retValue.toArray()[35]); // actual weather code -> "AJ"

                    }
                    rowcount++;
                }
                fis.close();
            }
            //Toast.makeText(this, "TEMP / ICON: " + retValue.toArray()[9] + "°C" + " / " + icons_dwd_current_weather[Integer.valueOf(retValue.get(35))], Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Fail to close aw:",e.toString());
        }

        return retValue;
    }


    /********************************************************************************************************************************************************/

    public String deleteRecursive(File deldir) {

    try {
        if (deldir.isDirectory())
            for (File child : Objects.requireNonNull(deldir.listFiles()))
                deleteRecursive(child);

        deldir.delete();
    }
    catch (Exception e) {
        e.printStackTrace();
        return "Fail to delete files!";
    }

    return "All files deleted!";
    }


    public String unzipFile(String iName) {
        //StringBuilder sb = new StringBuilder();
        InputStream is;
        ZipInputStream zis;
        File textFile;


//        while  (status == DownloadManager.STATUS_SUCCESSFUL);
        try {


            /* /sdcard/Android/data/de.phasenrauscher.kmzunzip/files/data/MOSMIX_L_LATEST_10776.kmz  */
            textFile = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), iName);
            //Toast.makeText(this, getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), Toast.LENGTH_SHORT).show();

            is = new FileInputStream(textFile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[8192];
            int count;

            while ((ze = zis.getNextEntry()) != null) {


                unzipfilename = ze.getName();
                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/" + unzipfilename);
                    fmd.mkdirs();
                    continue;


                }

                FileOutputStream fout = new FileOutputStream(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/" + unzipfilename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();

            }


            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Unzip Failure!";
        }

        return unzipfilename;
    }


    /*******************************************************************************************************************************************************/




    public Map<String, wwTTTForecast> ReadMosmixFile(String mosmixFile) throws Exception{
        // this hashmap holds the key <time> and the value <Forecast> for 10 days
        Map<String, wwTTTForecast> hashForecast = new HashMap<>();
        String[] xmlTTT = null;
        String[] xmlTTTCopy = null;
        String[] xmlww = null;
        String[] xmlwwCopy = null;
        String[] xmlTime = null;

        //File inputFile = new File(mosmixFile); // not in android
        File mmFile = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), mosmixFile);
        //Toast.makeText(this, getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), Toast.LENGTH_SHORT).show();


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(mmFile);

        // for testing only: mosmix forcast file from assets folder; attention: data of the kml file must contain actual data from the day! otherwise nothing will be shown because data outdated !
        //InputSource is = new InputSource(new InputStreamReader(getAssets().open("mosmix_error.kml")));
        //Document doc = dBuilder.parse(is);

        doc.getDocumentElement().normalize();
        //System.out.println("Root element: " + '"' + doc.getDocumentElement().getNodeName() + '"');

        /*--------------------------------------------------------------------------------------------------------*/
        // ww and TTT
        //NodeList nList = doc.getElementsByTagName("dwd:Forecast");
        NodeList nList = doc.getElementsByTagName("dwd:Forecast");
        //System.out.println("----------------------");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            //System.out.println("\nCurrent Element :" + nNode.getNodeName());






            // ww
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("dwd:elementName").equals("ww")) {
                    //System.out.println("Element: " + eElement.getAttribute("dwd:elementName"));
                    //System.out.println("Value: " + eElement.getElementsByTagName("dwd:value").item(0).getTextContent());
                    xmlww = eElement.getElementsByTagName("dwd:value").item(0).getTextContent().split("\\s+"); // regex: variable count of space characters

                    xmlwwCopy = Arrays.copyOfRange(xmlww, 1, xmlww.length);
                        /*
                        for(int iww=0; iww < xmlwwCopy.length; iww++) {
                            System.out.println("ArraywwCopy[" + iww + "] = " + xmlwwCopy[iww]); // first element

                        }*/

                }







                // TTT
                if (eElement.getAttribute("dwd:elementName").equals("TTT")) {
                    //System.out.println("Element: " + eElement.getAttribute("dwd:elementName"));
                    //System.out.println("Value: " + eElement.getElementsByTagName("dwd:value").item(0).getTextContent());


                    //xmlTTT = new String[eElement.getElementsByTagName("dwd:value").item(0).getTextContent().split("\\s+").length];
                    xmlTTT =            eElement.getElementsByTagName("dwd:value").item(0).getTextContent().split("\\s+"); // regex: variable count of space characters

                    /* Unfortunately, array is beginning with ZERO element which will lead to wrong array lenght later on
                    ArrayTTT[0] =
                    ArrayTTT[1] = 283.15
                    ArrayTTT[2] = 283.95

                    it should be:
                    ArrayTTTCopy[0] = 283.15
                    ArrayTTTCopy[1] = 283.95
                    ArrayTTTCopy[2] = 284.85

                    */

                    xmlTTTCopy = Arrays.copyOfRange(xmlTTT, 1, xmlTTT.length);

                    /*
                    for (int ittt = 0; ittt < xmlTTTCopy.length; ittt++) {
                        System.out.println("ArrayTTTCopy[" + ittt + "] = " + xmlTTTCopy[ittt]); // first element

                    }
                    */
                }
            }

        }
        /*--------------------------------------------------------------------------------------------------------*/

        // Time steps from XML file
        //NodeList nList = doc.getElementsByTagName("dwd:ForecastTimeSteps");
        NodeList oList = doc.getElementsByTagName("dwd:ForecastTimeSteps");
        //System.out.println("----------------------");

        for (int temp = 0; temp < oList.getLength(); temp++) {
            Node oNode = oList.item(temp);
            //System.out.println("\nCurrent Element:  " + oNode.getNodeName());

            if (oNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fElement = (Element) oNode;

                //System.out.println("Value: " + eElement.getElementsByTagName("dwd:TimeStep").item(0).getTextContent());
                xmlTime = new String[fElement.getElementsByTagName("dwd:TimeStep").getLength()];
                for (int iitem=0; iitem < fElement.getElementsByTagName("dwd:TimeStep").getLength(); iitem++) {
                    //System.out.println("Value[" + iitem + "] = " + fElement.getElementsByTagName("dwd:TimeStep").item(iitem).getTextContent());
                    xmlTime[iitem] = fElement.getElementsByTagName("dwd:TimeStep").item(iitem).getTextContent();

                }

            }
        }
            /*for (int timeItem = 0;  timeItem < xmlTime.length; timeItem++){
                System.out.println("Time: [" + timeItem + "] =" + xmlTime[timeItem]);

            }*/
        /*--------------------------------------------------------------------------------------------------------*/

        //assemble DATE/TIME with ww and TTT


        if (Objects.requireNonNull(xmlwwCopy).length == Objects.requireNonNull(xmlTime).length && Objects.requireNonNull(xmlTTTCopy).length == xmlTime.length) {
            //write hashmap key (time) and value (wwTTTForecast) in parallel, hereafter the order of elements is gone

            for (int hm=0; hm < xmlTime.length; hm++) {
                hashForecast.put(xmlTime[hm], new wwTTTForecast(xmlTTTCopy[hm], xmlwwCopy[hm]));
            }

            // output the hashmap for check (fw)
              //  int num = 0;
             //for (Map.Entry<String, wwTTTForecast> WeatherForecastHours: hashForecast.entrySet()) {
             //    num++;
              //   System.out.format("N°°: %d Time: %s  Temperature: %s WeatherCode: %s \n", num, WeatherForecastHours.getKey(), WeatherForecastHours.getValue().ReadTemp2m(), WeatherForecastHours.getValue().ReadWeatherCode());
             //}

        }
        else {
            //TODO throw exception
            Log.d("DUDU", "Error in XML file - no equal amount of elements for TTT , ww, Time!" + Objects.requireNonNull(xmlTTT).length + xmlww.length + xmlTime.length);
        }

        /*--------------------------------------------------------------------------------------------------------*/

        return hashForecast;
    }

    /* @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    hid=WeatherStationsIDArray.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    */

    private void GUI_update_with_database_content () {

        // TODO: read data base and show in GUI (useful if no update button was pressed); database will only be updated when FloatingActionButton pressed # MARK1


        SQLiteDatabase DB1;
        DB1 = openOrCreateDatabase("Userdata.db", Context.MODE_PRIVATE, null);
        Cursor res = DB1.rawQuery("Select * from Userdetails", null);


        if (res.getCount() == 0) {
            Toast.makeText(this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            //return;
        }


        while (res.moveToNext()) {

              if (res.getString(1).equals("weatherdata")) {


                  if ((res.getString(2)).equals("location")) {
                      dbhid = res.getString(3);
                  }
                  if ((res.getString(2)).equals("locationName")) {
                      dblocationName = res.getString(3);
                  }
                  if ((res.getString(2)).equals("awIcon")) {
                      dbIcon_aw = res.getString(3);
                  }
                  if ((res.getString(2)).equals("awTemp")) {
                      dbTemp_aw = res.getString(3);
                  }

                  if ((res.getString(2)).equals("today9Icon")) {
                      dbIcons[0] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("today12Icon")) {
                      dbIcons[1] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("today15Icon")) {
                      dbIcons[2] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("today18Icon")) {
                      dbIcons[3] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("tomorrow9Icon")) {
                      dbIcons[4] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("tomorrow12Icon")) {
                      dbIcons[5] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("tomorrow15Icon")) {
                      dbIcons[6] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("tomorrow18Icon")) {
                      dbIcons[7] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day39Icon")) {
                      dbIcons[8] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day312Icon")) {
                      dbIcons[9] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day315Icon")) {
                      dbIcons[10] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day318Icon")) {
                      dbIcons[11] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day49Icon")) {
                      dbIcons[12] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day412Icon")) {
                      dbIcons[13] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day415Icon")) {
                      dbIcons[14] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day418Icon")) {
                      dbIcons[15] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day59Icon")) {
                      dbIcons[16] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day512Icon")) {
                      dbIcons[17] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day515Icon")) {
                      dbIcons[18] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day518Icon")) {
                      dbIcons[19] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day69Icon")) {
                      dbIcons[20] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day612Icon")) {
                      dbIcons[21] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day615Icon")) {
                      dbIcons[22] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day618Icon")) {
                      dbIcons[23] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day79Icon")) {
                      dbIcons[24] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day712Icon")) {
                      dbIcons[25] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day715Icon")) {
                      dbIcons[26] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day718Icon")) {
                      dbIcons[27] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("todaymaxtemp")) {
                      dbminmaxtemps[0] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("todaymintemp")) {
                      dbminmaxtemps[1] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("tomorrowmaxtemp")) {
                      dbminmaxtemps[2] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("tomorrowmintemp")) {
                      dbminmaxtemps[3] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day3maxtemp")) {
                      dbminmaxtemps[4] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day3mintemp")) {
                      dbminmaxtemps[5] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day4maxtemp")) {
                      dbminmaxtemps[6] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day4mintemp")) {
                      dbminmaxtemps[7] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day5maxtemp")) {
                      dbminmaxtemps[8] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day5mintemp")) {
                      dbminmaxtemps[9] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day6maxtemp")) {
                      dbminmaxtemps[10] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day6mintemp")) {
                      dbminmaxtemps[11] = res.getString(3);
                  }

                  if ((res.getString(2)).equals("day7maxtemp")) {
                      dbminmaxtemps[12] = res.getString(3);
                  }
                  if ((res.getString(2)).equals("day7mintemp")) {
                      dbminmaxtemps[13] = res.getString(3);
                  }

            }
    }

        updateGUI(dblocationName, dbIcons, dbminmaxtemps, dbIcon_aw, dbTemp_aw);

        DB1.close();
    }

    private void DB_default () {

    /***************************************************** load or create data base *****************************************/
    /************************************************************************************************************************/
    /* load data base with default values, create new one if no database exists  */
    /* -> do not read existing data base here !  */
    /* -> do not update GUI ! will be done at the end of OnCreate() */
    /* MARK4 */
    /************************************************************************************************************************/

    //  -> do not update GUI here
    //  -> do not read existing data base here


    // Create defaults if data base does not exits:
    String location_default = "10776";
    String locationName_default = "Stadt";
    String[] Icons_default = {"empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty"};
    String[] minmaxtemps_default = {"--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--"};




    /*********************************************************************************************************************************************************************************************/

        //if (!d) {

        DBHelper DB = new DBHelper(this);


        Boolean checkupdatedata = DB.updateuserdata("location", location_default,"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("location", location_default,"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("locationName", locationName_default,"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("locationName", locationName_default,"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("awIcon", Icons_default[0],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("awIcon", Icons_default[0],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("awTemp", minmaxtemps_default[0],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("awTemp", minmaxtemps_default[0],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("today9Icon", Icons_default[0],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("today9Icon", Icons_default[0],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("today12Icon", Icons_default[1],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("today12Icon", Icons_default[1],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("today15Icon", Icons_default[2],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("today15Icon", Icons_default[2],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("today18Icon", Icons_default[3],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("today18Icon", Icons_default[3],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("tomorrow9Icon", Icons_default[4],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrow9Icon", Icons_default[4],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("tomorrow12Icon", Icons_default[5],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrow12Icon", Icons_default[5],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("tomorrow15Icon", Icons_default[6],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrow15Icon", Icons_default[6],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("tomorrow18Icon", Icons_default[7],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrow18Icon", Icons_default[7],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day39Icon", Icons_default[8],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day39Icon", Icons_default[8],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day312Icon", Icons_default[9],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day312Icon", Icons_default[9],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day315Icon", Icons_default[10],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day315Icon", Icons_default[10],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day318Icon", Icons_default[11],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day318Icon", Icons_default[11],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day49Icon", Icons_default[12],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day49Icon", Icons_default[12],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day412Icon", Icons_default[13],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day412Icon", Icons_default[13],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day415Icon", Icons_default[14],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day415Icon", Icons_default[14],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day418Icon", Icons_default[15],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day418Icon", Icons_default[15],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day59Icon", Icons_default[16],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day59Icon", Icons_default[16],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day512Icon", Icons_default[17],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day512Icon", Icons_default[17],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day515Icon", Icons_default[18],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day515Icon", Icons_default[18],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day518Icon", Icons_default[19],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day518Icon", Icons_default[19],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day69Icon", Icons_default[20],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day69Icon", Icons_default[20],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day612Icon", Icons_default[21],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day612Icon", Icons_default[21],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day615Icon", Icons_default[22],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day615Icon", Icons_default[22],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day618Icon", Icons_default[23],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day618Icon", Icons_default[23],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day79Icon", Icons_default[24],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day79Icon", Icons_default[24],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day712Icon", Icons_default[25],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day712Icon", Icons_default[25],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day715Icon", Icons_default[26],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day715Icon", Icons_default[26],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day718Icon", Icons_default[27],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day718Icon", Icons_default[27],"weatherdata");
        }


        checkupdatedata = DB.updateuserdata("todaymaxtemp", minmaxtemps_default[0],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("todaymaxtemp", minmaxtemps_default[0],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("todaymintemp", minmaxtemps_default[1],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("todaymintemp", minmaxtemps_default[1],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("tomorrowmaxtemp", minmaxtemps_default[2],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrowmaxtemp", minmaxtemps_default[2],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("tomorrowmintemp", minmaxtemps_default[3],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("tomorrowmintemp", minmaxtemps_default[3],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day3maxtemp", minmaxtemps_default[4],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day3maxtemp", minmaxtemps_default[4],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day3mintemp", minmaxtemps_default[5],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day3mintemp", minmaxtemps_default[5],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day4maxtemp", minmaxtemps_default[6],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day4maxtemp", minmaxtemps_default[6],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day4mintemp", minmaxtemps_default[7],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day4mintemp", minmaxtemps_default[7],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day5maxtemp", minmaxtemps_default[8],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day5maxtemp", minmaxtemps_default[8],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day5mintemp", minmaxtemps_default[9],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day5mintemp", minmaxtemps_default[9],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day6maxtemp", minmaxtemps_default[10],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day6maxtemp", minmaxtemps_default[10],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day6mintemp", minmaxtemps_default[11],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day6mintemp", minmaxtemps_default[11],"weatherdata");
        }

        checkupdatedata = DB.updateuserdata("day7maxtemp", minmaxtemps_default[12],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day7maxtemp", minmaxtemps_default[12],"weatherdata");
        }
        checkupdatedata = DB.updateuserdata("day7mintemp", minmaxtemps_default[13],"weatherdata");
        if (checkupdatedata == true) {
        } else {
            Boolean checkinsertdata = DB.insertuserdata("day7mintemp", minmaxtemps_default[13],"weatherdata");
        }


        DB.close();
    //}
}

    /*******************************************************************************************************************************************************/
    /*TODO*/
    public void register (View v){
        Intent i_register = new Intent(MainActivity.this, Setting.class);
        /*WeatherStationsNameArray übergeben an settings activity*/
        //i_register.putExtra("Spinnerdata", WeatherStationsNameArray);
        //i_register.putExtra("Spinnerdata", cityList);0
        //i_register.putParcelableArrayListExtra ("Spinnerdata", cityList);
        //16OKT22
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Spinnerdata", cityList);
        i_register.putExtras(bundle);
        startActivity(i_register);
        //this.startActivity(intent);
    }

    public void register_map (View v){
        Intent map_register = new Intent(MainActivity.this, de.phasenrauscher.bicyweather.Map.class);
        startActivity(map_register);
    }



}