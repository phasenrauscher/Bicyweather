
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
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.phasenrauscher.bicyweather.R;


public class AutoCompleteCountryAdapter extends ArrayAdapter<CountryItem> {
    private List<CountryItem> countryListFull;
    DBHelper DB = new DBHelper(getContext());

    public AutoCompleteCountryAdapter(@NonNull Context context, @NonNull List<CountryItem> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.country_autocomplete_row, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        TextView textViewID = convertView.findViewById(R.id.text_view_ID);

        CountryItem countryItem = getItem(position);

        if (countryItem != null) {
            textViewName.setText(countryItem.getCountryName());
            textViewID.setText(countryItem.getCountryID());
        }

        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CountryItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CountryItem item : countryListFull) {
                    if (item.getCountryName().toLowerCase().contains(filterPattern)) {
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
        public CharSequence convertResultToString(Object resultValue) {


            Cursor res = DB.getdata();


            if (res.getCount() == 0) {
                Boolean checkinsertdata = DB.insertuserdata("location", ((CountryItem) resultValue).getCountryID(),"weatherdata");}
            else {
                Boolean checkupdatedata = DB.updateuserdata("location", ((CountryItem) resultValue).getCountryID(),"weatherdata");
            }

            return (((CountryItem) resultValue).getCountryName() +  ((CountryItem) resultValue).getCountryID());  // return value to autocomplete field
        }
    };
}