package de.phasenrauschen.bicyweather;

public class CountryItem {
    private String countryName;
    private String countryID;

    public CountryItem(String countryName, String countryID) {
        this.countryName = countryName;
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryID() {
        return countryID;
    }
}