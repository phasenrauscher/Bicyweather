
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


//import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Parse_forecast_weather {


    /* <dwd:TimeStep>2019-11-05T10:00:00.000Z</dwd:TimeStep> */

    // 22 Aug. 22 commented; since we saw program aborts after change from one day to another -> moved into fkt. ParseKMLTemperature(..)
    //public static LocalDateTime nowtime = LocalDateTime.now();

    //public static LocalDateTime today9 = nowtime.withHour(9).withMinute(0).withSecond(0).withNano(0);


    //DateTimeFormatter kmlFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    //System.out.println(kmlFormat.format(nu)+".000Z");
    //System.out.println(kmlFormat.format(today9));

    
    // method to find all temperatures of one day and extract max/min values of one day
    // Generic Map filterbykey, with predicate
    
    
    public static <K, V > Map < K, V > filterByKey(Map< K, V > map, Predicate< K > predicate) {
        return map.entrySet()
                .stream()
                .filter(x -> predicate.test(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    

    
public static String[] ParseKMLIcon(Map<String, wwTTTForecast> TTTFc) {

    LocalDateTime today9 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0); // 22 Aug. 22 declaration moved to here
    // parse weather forecast XML file
    String[] ArrayIcon = new String[101];

    /* forecast ICONS */
    /* store ICONs of today + 6 additional day from kml file "ww" into array ArrayIconoftheWeek[4.0]; 4.0 is a String ! will be replaced by String of icon codes (icons_dwd_forecast_weather) later on*/

    //int wicon = 0;

    for (Map.Entry<String, wwTTTForecast> WeatherForecastHours : TTTFc.entrySet()) {
        try{if (WeatherForecastHours.getKey().contains(today9.toString()))              {ArrayIcon[0] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[0] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusHours(3).toString())) {ArrayIcon[1] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[1] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusHours(6).toString())) {ArrayIcon[2] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[2] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusHours(9).toString())) {ArrayIcon[3] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[3] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(1).toString()))              {ArrayIcon[4] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[4] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(1).plusHours(3).toString())) {ArrayIcon[5] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[5] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(1).plusHours(6).toString())) {ArrayIcon[6] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[6] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(1).plusHours(9).toString())) {ArrayIcon[7] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[7] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(2).toString()))              {ArrayIcon[8] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[8] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(2).plusHours(3).toString())) {ArrayIcon[9] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[9] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(2).plusHours(6).toString())) {ArrayIcon[10] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[10] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(2).plusHours(9).toString())) {ArrayIcon[11] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[11] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(3).toString()))              {ArrayIcon[12] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[12] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(3).plusHours(3).toString())) {ArrayIcon[13] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[13] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(3).plusHours(6).toString())) {ArrayIcon[14] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[14] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(3).plusHours(9).toString())) {ArrayIcon[15] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[15] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(4).toString()))              {ArrayIcon[16] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[16] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(4).plusHours(3).toString())) {ArrayIcon[17] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[17] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(4).plusHours(6).toString())) {ArrayIcon[18] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[18] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(4).plusHours(9).toString())) {ArrayIcon[19] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[19] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(5).toString()))              {ArrayIcon[20] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[20] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(5).plusHours(3).toString())) {ArrayIcon[21] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[21] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(5).plusHours(6).toString())) {ArrayIcon[22] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[22] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(5).plusHours(9).toString())) {ArrayIcon[23] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[23] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(6).toString()))              {ArrayIcon[24] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[24] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(6).plusHours(3).toString())) {ArrayIcon[25] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[25] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(6).plusHours(6).toString())) {ArrayIcon[26] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[26] = "empty";}
        try{if (WeatherForecastHours.getKey().contains(today9.plusDays(6).plusHours(9).toString())) {ArrayIcon[27] = WeatherForecastHours.getValue().ReadWeatherCode(); }}catch (Exception e){ ArrayIcon[27] = "empty";}


        //System.out.format("Time: %s  Temperature: %s WeatherCode: %s \n", WeatherForecastHours.getKey(), WeatherForecastHours.getValue().ReadTemp2m(), WeatherForecastHours.getValue().ReadWeatherCode());
        
    }

    /*##############################################################################################################*/

    /* icon numbers will be replace here by String of icon codes (icons_dwd_forecast_weather) in the same array 'ArrayIconoftheWeek[]'*/

    /* SYNOP ?*/

    String[] icons_dwd_forecast_weather = {"skc", "few", "sct", "bkn", "fu", "du", "du", "du", "du", "du", "du", "fg", "fg", "tsra", "ra", "ra", "ra", "tsra", "wind", "wind", "shra", "ra", "sn", "raip", "fsra", "hi_shwrs", "sn", "raip", "fg", "tsra", "wind", "wind", "wind", "wind", "wind", "wind", "sn", "sn", "sn", "sn", "fg", "fg", "fg", "fg", "fg", "fg", "fg", "fg", "fg", "fg", "hi_shwrs", "shra", "hi_shwrs", "shra", "ra", "ra", "fzra", "fzra", "shra", "ra", "hi_shwrs", "shra", "ra", "ra", "ra", "ra", "fzra", "fzra", "rasn", "rasn", "sn", "sn", "sn", "sn", "sn", "sn", "sn", "sn", "sn", "sn", "shra", "ra", "ra", "rasn", "rasn", "rasn", "rasn", "raip", "raip", "raip", "raip", "tsra", "tsra", "tsra", "tsra", "tsra", "tsra", "tsra", "tsra", "tsra", "empty", "error"};


    // GUI: ArrayIconoftheWeek[0...3] -> today's icons for 9am, 12am, 3pm, 6pm; ArrayIconoftheWeek[4...7] -> tomorrow's, etc....


    for (int wiconloop = 0; wiconloop < 40; wiconloop++) {
        //System.out.format("N°: %d WeatherCode: %s \n", wiconloop, icons_dwd_forecast_weather[ Integer.valueOf(ArrayIconoftheWeek[wiconloop]) ]);


        // replace items in ArrayIconoftheWeek that were not supplied by information with "empty" string 
        // replace f.e. "2.00" by valid icon code, 
        // take care of wrong input like "-"

        if ((ArrayIcon[wiconloop] == null) || (ArrayIcon[wiconloop].contains("-"))) {
            ArrayIcon[wiconloop] = "empty" + "";
            //System.out.format("N°: %d WeatherCode: %s \n", wiconloop, ArrayIconoftheWeek[wiconloop]);
        } else {

            try {
                ArrayIcon[wiconloop] = icons_dwd_forecast_weather[(int) Math.round(Double.parseDouble(ArrayIcon[wiconloop]))];
                //System.out.format("N°: %d WeatherCode_try: %s \n", wiconloop, ArrayIconoftheWeek[wiconloop]);


            } catch (Exception e) {
                ArrayIcon[wiconloop] = icons_dwd_forecast_weather[100];
                //System.out.format("N°: %d WeatherCode_catch: %s \n", wiconloop, ArrayIconoftheWeek[wiconloop]);



            }


        }
        //Log.d("ICONS", "N°:  " + wiconloop + "  " + ArrayIcon[wiconloop]);

    }
    return (ArrayIcon);
    }
        /***********************************************************************************************************************/


    public static List<Double> ParseKMLTemperature(Map<String, wwTTTForecast> TTTFc) {

        LocalDateTime nowtime = LocalDateTime.now(); // 22 Aug. 22 declaration moved to here
        // parse min / max temperatures of one day with help of filterByKey method, icons will be extracted directly by their time

        List<Double> minmaxtemp;

        /* forecast TEMPERATURES */
        /* find all temperatures of one day and their min max values, use 'predicate' interface */
        /* input: fw output: minmaxtempoftheday */

        DateTimeFormatter dayFormat=DateTimeFormatter.ISO_LOCAL_DATE;
        System.out.println(dayFormat.format(nowtime));
        minmaxtemp=new ArrayList<>();

        for(int weekday=0;weekday<7;weekday++){
        int daynum=weekday;

        //filter temperatures of one day in new hashmap
        Map<String, wwTTTForecast> filteredMap=filterByKey(TTTFc, x->x.contains(dayFormat.format(nowtime.plusDays(daynum))));


        //copy filteredMap to ArrayList and convert values to double -273.15K -> °C
        List<Double> tempoftheday=new ArrayList<>();

        for(wwTTTForecast b:filteredMap.values()){
        //System.out.format("%s\n", b.ReadTemp2m());

        //TODO: tempoftheday.add(Double.valueOf(b.ReadTemp2m())-273.15); changed from Double.valueOf()(->returns Double) to Double.parseDouble() (-> returns double)
        tempoftheday.add(Double.parseDouble(b.ReadTemp2m())-273.15);

        }

            
            for (Double ttb : tempoftheday) {
                //System.out.format("all temperatures of the day: %.2f \n", ttb);
            }
            //System.out.println("elements counter:" + tempoftheday.toArray().length);

        //System.out.format("Min: %.2f\n", Collections.min(tempoftheday));
        //System.out.format("Max: %.2f\n", Collections.max(tempoftheday));


        minmaxtemp.add(Collections.min(tempoftheday));
        minmaxtemp.add(Collections.max(tempoftheday));


        }
        // GUI: ListArray of min/max temperatures of 7 days, beginning of today; (1.element: min_today, 2.element: max_today, 3.element: min_tomorrow, ...)
        for(Double mm:minmaxtemp){

            //Toast.makeText(this, mm, Toast.LENGTH_SHORT).show();
            //Log.d("TEMP",String.valueOf(Math.round(mm)));
        }

        return(minmaxtemp);
        }

}