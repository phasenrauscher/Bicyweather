package de.phasenrauschen.bicyweather;

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