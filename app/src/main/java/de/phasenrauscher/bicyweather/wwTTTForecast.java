
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

public class wwTTTForecast {

    private String Temp2m;
    private String WeatherCode;


    public wwTTTForecast(String Temp2m, String WeatherCode){
        
        this.Temp2m = Temp2m;
        this.WeatherCode = WeatherCode;
    }


    public String ReadTemp2m()
    {
        return Temp2m;
    }

    public String ReadWeatherCode(){
        
        return WeatherCode;
    }
    
}
