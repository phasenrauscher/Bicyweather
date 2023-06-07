package de.phasenrauschen.bicyweather;

import android.os.Parcel;
import android.os.Parcelable;

public class CityItem implements Parcelable {
        String cityName;
        String cityId;

        public CityItem(String cityName, String cityId) {
            this.cityName = cityName;
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public String getCityId() {
            return cityId;
        }



    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub
        dest.writeString(cityId);
        dest.writeString(cityName);
    }

    public CityItem(Parcel in) {
        cityId = in.readString();
        cityName = in.readString();
    }

    public static final Parcelable.Creator<CityItem> CREATOR = new Parcelable.Creator<CityItem>() {
        public CityItem createFromParcel(Parcel in) {
            return new CityItem(in);
        }

        public CityItem[] newArray(int size) {
            return new CityItem[size];
        }
    };
}