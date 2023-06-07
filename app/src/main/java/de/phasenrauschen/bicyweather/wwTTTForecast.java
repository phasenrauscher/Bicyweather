package de.phasenrauschen.bicyweather;

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
