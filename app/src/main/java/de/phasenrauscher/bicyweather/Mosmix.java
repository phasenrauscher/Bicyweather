
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

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


/***************************************************** Thread2 Mosmix ***************************************************/
    /************************************************************************************************************************/
            /*
            Input:  assets file "mosmix_stationskatalog.cfg"
            Output: Map<String, WeatherStation> mapweatherstations
            What:   extracts data from catalog of all available DWD stations "mosmix_stationskatalog.cfg"
             */
    /************************************************************************************************************************/



    public class Mosmix implements Runnable {
        private static final String TAG = "MosmixThread";
        public static final int whatMosmix = 1;
        private MainActivity mainActivity;
        HashMap<String, WeatherStation> emapweatherstations = new HashMap<>();

    public  Mosmix(Context context) {
        mainActivity = MainActivity.mainActivity;
    }




        @Override
        public void run() {
            //String text = "kk";
            BufferedReader reader = null;

            try {


                reader = new BufferedReader(new InputStreamReader(MainActivity.mainActivity.getAssets().open("mosmix_stationskatalog.cfg"), "ISO8859-1"));
                String wsLine;

                //Log.e("Reader Stuff", reader.readLine());

                while ((wsLine = reader.readLine()) != null) {


                    // System.out.println("received row: " + wsLine);
                    /*ID => key  || Name, Longitude, etc. => value;  */

                    /*  TABLE St 99999 99991 gmos 99892 99891 ecmda 99791 gmeda
                    clu   CofX  id    ICAO name                 nb.    el.     elev  Hmod-H type
                    ===== ----- ===== ---- -------------------- ------ ------- ----- ------ ----
                    0-5   6-11  12-17 18-22 23-43               44-51   51-58   59-64 65-71 72-76
                    99803     8 EW002 ---- Beveringen            53.10   12.13    71        LAND
                    99804     8 EW003 ---- Calvoerde             52.25   11.19    55        LAND
                    */


                    Integer ss_Clu;
                    Integer ss_CofX;
                    String ss_Id;   /* this is the key of the hasmap */
                    String ss_ICAO;
                    String ss_Name; /* this is NOT the KEY of the hashmap */
                    double ss_Latitude;
                    double ss_Longitude;
                    Integer ss_Elev;
                    Integer ss_HmodH;
                    String ss_Type;

                    /* read line by line, separate information and store them to hashmap mapweatherstations
                    /* recognize head of table and empty lines and ignore them */
                    if (!(wsLine.contains("TABLE St 99999") || (wsLine.contains("===== -----")) || (wsLine.contains("clu   CofX")) || (wsLine.isEmpty()))) {

                        try {
                            /* throw exceptions for all inputs that not fit and go ahead */

                            if ((wsLine.substring(0, 5).isEmpty()) || (wsLine.substring(0, 5).contains("     ")))
                                ss_Clu = 0;
                            else
                                ss_Clu = Integer.valueOf(wsLine.substring(0, 5).replace(" ", ""));

                            if ((wsLine.substring(6, 11).isEmpty()) || (wsLine.substring(6, 11).contains("     ")))
                                ss_CofX = 0;
                            else
                                ss_CofX = Integer.valueOf(wsLine.substring(6, 11).replace(" ", ""));

                            ss_Id = wsLine.substring(12, 17).trim(); //OKT 2020 added trim() m.s.
                            ss_ICAO = wsLine.substring(18, 22);
                            /* TODO show german umlauts instead of "/D6": 19 SEPT 2022  done by charsetName = "ISO8859-1" */
                            ss_Name = wsLine.substring(23, 43); /* Changed!! -> WRONG: name is the KEY of the hashmap; not any more; TRUE: ID is the key ! */

                            if ((wsLine.substring(44, 51).isEmpty()) || (wsLine.substring(44, 51).contains("     ")) || (wsLine.substring(44, 51).contains("------")))
                                ss_Latitude = 0;
                            else ss_Latitude = Double.valueOf(wsLine.substring(44, 51));

                            if ((wsLine.substring(51, 58).isEmpty()) || (wsLine.substring(51, 58).contains("     ")) || (wsLine.substring(51, 58).contains("-------")))
                                ss_Longitude = 0;
                            else
                                ss_Longitude = Double.valueOf(wsLine.substring(51, 58));

                            if ((wsLine.substring(59, 64).isEmpty()) || (wsLine.substring(59, 64).contains("     ")))
                                ss_Elev = 0;
                            else
                                ss_Elev = Integer.parseInt(wsLine.substring(59, 64).replace(" ", ""));

                            if ((wsLine.substring(65, 71).isEmpty()) || (wsLine.substring(65, 71).contains("      ")))
                                ss_HmodH = 0;
                            else
                                ss_HmodH = Integer.parseInt(wsLine.substring(65, 71).replace(" ", ""));

                            ss_Type = wsLine.substring(72, 76);

                            emapweatherstations.put(ss_Id, new WeatherStation(ss_Name, ss_Latitude, ss_Longitude, ss_Clu, ss_CofX, ss_ICAO, ss_Elev, ss_HmodH, ss_Type));
                            //text = ss_Name;
                            //Log.d(TAG, emapweatherstations.keySet().toString());

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    }

                }

                //Log.d("AUS", emapweatherstations.keySet().toString());
                //System.out.println(TAG + ":MOSMIX: " + emapweatherstations.keySet().size()); // How many stations we found in MOSMIX file (5690 rows - 16 headers = 5674)
                Log.d(TAG, String.valueOf(emapweatherstations.keySet().size()));
                // move ArrayList "StationsOnWeb" to MainActivity

                Bundle bundle = new Bundle();
                //bundle.putStringArrayList ("Mosmix", emapweatherstations);
                bundle.putSerializable("Mosmix", emapweatherstations);
                Message msg = mainActivity.mainHandler.obtainMessage();
                msg.what = whatMosmix;
                msg.setData(bundle);
                msg.sendToTarget();



            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
            }
        }
    }

