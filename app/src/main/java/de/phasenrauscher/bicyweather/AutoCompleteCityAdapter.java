
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.phasenrauscher.bicyweather.R;


public class AutoCompleteCityAdapter extends ArrayAdapter<CityItem> {
    private List<CityItem> cityListFull;
    DBHelper DB = new DBHelper(getContext());

    public AutoCompleteCityAdapter(@NonNull Context context, @NonNull List<CityItem> cityList) {
        super(context, 0, cityList);
        cityListFull = new ArrayList<>(cityList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return cityFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.city_autocomplete_row, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        TextView textViewID = convertView.findViewById(R.id.text_view_ID);

        CityItem cityItem = getItem(position);

        if (cityItem != null) {
            textViewName.setText(cityItem.getCityName());
            textViewID.setText(cityItem.getCityId());
        }

        return convertView;
    }

    private Filter cityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CityItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(cityListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CityItem item : cityListFull) {
                    if (item.getCityName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }



        @Override
        @SuppressWarnings("unchecked")
        public CharSequence convertResultToString(Object resultValue) {

            // fill database DB with CityID

            Boolean checkupdatedata = DB.updateuserdata("location", ((CityItem) resultValue).getCityId(),"weatherdata");
            if(checkupdatedata==true){
                //Toast.makeText(getContext(), "AutoComplete update OK!", Toast.LENGTH_SHORT).show();
            }
            else{
                Boolean checkinsertdata = DB.insertuserdata("location", ((CityItem) resultValue).getCityId(),"weatherdata");
                //Toast.makeText(getContext(), "AutoComplete insert OK!", Toast.LENGTH_SHORT).show();
            }



            return (((CityItem) resultValue).getCityName() +  ((CityItem) resultValue).getCityId());  // return value to autocomplete field
        }
    };
}