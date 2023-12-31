
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

// toplevel class
public class WeatherStation {

    private String weatherStationsName;
    private double weatherStationsLatitude;
    private double weatherStationsLongitude;
    private int weatherStationsClu;
    private int weatherStationsCofx;
    private String weatherStationsICAO;
    private int weatherStationsElev;
    private int weatherStationsHmodH;
    private String weatherStationsType;





/*  TABLE St 99999 99991 gmos 99892 99891 ecmda 99791 gmeda
    clu   CofX  id    ICAO name                 nb.    el.     elev  Hmod-H type
    ===== ----- ===== ---- -------------------- ------ ------- ----- ------ ----
    99803     8 EW002 ---- Beveringen            53.10   12.13    71        LAND
    99804     8 EW003 ---- Calvoerde             52.25   11.19    55        LAND
*/

    //constructor
    
    public WeatherStation(String weatherStationsName, double weatherStationsLatitude, double weatherStationsLongitude, int weatherStationsClu, int weatherStationsCofx, String weatherStationsICAO, int weatherStationsElev, int weatherStationsHmodH, String weatherStationsType) {
        //System.out.println("Constructor WeatherStations executed!");
        this.weatherStationsName = weatherStationsName;
        this.weatherStationsLatitude = weatherStationsLatitude;
        this.weatherStationsLongitude = weatherStationsLongitude;
        this.weatherStationsClu = weatherStationsClu;
        this.weatherStationsCofx = weatherStationsCofx;
        this.weatherStationsICAO = weatherStationsICAO;
        this.weatherStationsElev = weatherStationsElev;
        this.weatherStationsHmodH = weatherStationsHmodH;
        this.weatherStationsType = weatherStationsType;


    }

    // methods 
    
    // read out weatherstations feature set
    
    /*public String ReadWeatherStationsID()
    {
    return weatherStationsID;         
    }
    */
    public double ReadWeatherStationsLatitude()
    {
        return weatherStationsLatitude;
    }

    public double ReadWeatherStationsLongitude()
    {
        return weatherStationsLongitude;
    }

    public int ReadWeatherStationsClu()
    {
        return weatherStationsClu;
    }

    public int ReadWeatherStationsCofx()
    {
        return weatherStationsCofx;
    }

    public String ReadWeatherStationsICAO()
    {
        return weatherStationsICAO;
    }

    public int ReadWeatherStationsHmodH()
    {
        return weatherStationsHmodH;
    }

    public String ReadWeatherStationsType()
    {
        return weatherStationsType;
    }

    public int ReadWeatherStationsElev()
    {
        return weatherStationsElev;
    }

    public String ReadWeatherStationsName()
    {
        return weatherStationsName;
    }

}
