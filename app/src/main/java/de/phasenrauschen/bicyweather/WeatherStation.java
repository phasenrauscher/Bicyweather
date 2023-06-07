package de.phasenrauschen.bicyweather;

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
