package de.phasenrauschen.bicyweather;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


    /***************************************************** Thread DwdHtml  **************************************************/
    /************************************************************************************************************************/
        /*
        Input:URL "https://opendata.dwd.de/weather/weather_reports/poi"
        Output: ArrayList<String> eStationsOnWeb
        What: extracts location ID's from URL and stores in eStationsOnWeb
         */
    /************************************************************************************************************************/


        public class DwdHtml implements Runnable {
        private static final String TAG = "DwdHtmlThread";
        private MainActivity mainActivity;
        //Handler mainHandler;
        public static final int whatDwdHtml = 2;


        public DwdHtml(Context context) {
            //mainHandler = new Handler(context.getMainLooper());
            mainActivity = MainActivity.mainActivity;
        }



        @Override
        public void run() {

/*
            // Test
            ArrayList<String> alcountry = new ArrayList<>();
            alcountry.add("India");
            alcountry.add("USA");
            alcountry.add("China");
            alcountry.add("Nigeria");

            Bundle bundle = new Bundle();
            bundle.putStringArrayList ("alCountry", alcountry);
            bundle.putFloat("Float", 1.2f);
            Message msg = mainActivity.mainHandler.obtainMessage();
            msg.what = 33;
            //msg.arg1 = 222;
            msg.setData(bundle);
            msg.sendToTarget();
            // Test end
*/
            // final ArrayList<String> urls=new ArrayList<String>();  // removed 2dec21
            String htmlStatID;
            BufferedReader in = null;
            ArrayList<String> eStationsOnWeb = new ArrayList<>();
            //List<String> StationsOnWeb = new ArrayList<>();

            try {



                            /* HTML page source code looks like this:

                            <html>
                            <head><title>Index of /weather/weather_reports/poi/</title></head>
                            <body>
                            <h1>Index of /weather/weather_reports/poi/</h1><hr><pre><a href="../">../</a>
                                    <a href="01008-BEOB.csv">01008-BEOB.csv</a>                                     31-Mar-2021 20:43                7302
                                    <a href="01025-BEOB.csv">01025-BEOB.csv</a>                                     31-Mar-2021 20:43                7347
                                    <a href="01028-BEOB.csv">01028-BEOB.csv</a>                                     31-Mar-2021 20:43                7292
                                    <a href="01049-BEOB.csv">01049-BEOB.csv</a>                                     31-Mar-2021 20:43                7370

                                    <a href="P265_-BEOB.csv">P265_-BEOB.csv</a>                                     26-Nov-2021 22:38                7347
                                    <a href="P280_-BEOB.csv">P280_-BEOB.csv</a>                                     26-Nov-2021 22:38                7292
                                    <a href="P305_-BEOB.csv">P305_-BEOB.csv</a>                                     26-Nov-2021 22:38                7352
                                    <a href="P308_-BEOB.csv">P308_-BEOB.csv</a>                                     26-Nov-2021 22:38                7372
                                    <a href="P319_-BEOB.csv">P319_-BEOB.csv</a>                                     26-Nov-2021 22:38                7312

                                    ...
                            */

                // Create a URL for the desired page
                URL url = new URL("https://opendata.dwd.de/weather/weather_reports/poi"); //My text file location
                //First open the connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000); // timing out 10 sec

                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String str;
                while ((str = in.readLine()) != null) {
                    //urls.add(str); // removed 2dec21

                    if (str.contains("-BEOB")) {
                        //System.out.println(str.substring(str.indexOf('"') + 1, str.indexOf('.')));
                        if ((str.substring(str.indexOf('"') + 1, str.indexOf('-'))).endsWith("_")) {
                            htmlStatID = (str.substring(str.indexOf('"') + 1, str.indexOf('-'))).replace("_", "");

                        } else {

                            htmlStatID = (str.substring(str.indexOf('"') + 1, str.indexOf('-')));

                        }

                        // all available stations (list of (String) ID) from website; mosmix catalog contains the data of about 5000 entries, but about 1000 available on website (what we are reading here) data OK!!
                        eStationsOnWeb.add(htmlStatID);
                    }
                }

                Log.d(TAG, String.valueOf(eStationsOnWeb));
                // move ArrayList "StationsOnWeb" to MainActivity

                Bundle bundle = new Bundle();
                bundle.putStringArrayList ("DwdHtml", eStationsOnWeb);
                Message msg = mainActivity.mainHandler.obtainMessage();
                msg.what = whatDwdHtml;
                msg.setData(bundle);
                msg.sendToTarget();


            } catch (IOException e) {
                Log.d(TAG + "Exception", e.toString());
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.d(TAG + "IOException", e.toString()); // added 31.AUG2022
                }

            }

        }
    }
