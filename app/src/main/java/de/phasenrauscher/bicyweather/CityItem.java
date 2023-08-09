
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