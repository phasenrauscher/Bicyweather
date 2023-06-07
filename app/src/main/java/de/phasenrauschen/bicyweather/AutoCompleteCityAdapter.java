package de.phasenrauschen.bicyweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }



        @Override
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