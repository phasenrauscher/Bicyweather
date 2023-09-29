
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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.phasenrauscher.bicyweather.R;

public class Setting extends AppCompatActivity {


    EditText number;
    String[] country = { "India", "USA", "China", "Japan", "Other", "Nigeria"};
    String cnt;
    int spinner_city;
    EditText location, city;
    Button insert, update, delete, view;
    //DBHelper DB;
    Cursor c;

    //OLD: Getting the instance of Spinner and applying OnItemSelectedListener on it
    //Spinner spin = (Spinner) findViewById(R.id.spinner);


    //private List<CityItem> cityList;  // uncomment for testing with Array cityList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //Toast.makeText(getApplicationContext(), "Activity created",Toast.LENGTH_LONG).show();


        /************************************************************************************************************/


        //TextView tvResult = (TextView)findViewById(R.id.textViewResult);

        /*
        fillCityList(); // uncomment for testing with Array cityList
        cityList.forEach(
                cl  -> System.out.format("%s, %s \n", cl.getCityId(), cl.getCityName())
        );
        AutoCompleteTextView editTextactv = findViewById(R.id.actv);
        AutoCompleteCityAdapter adapter = new AutoCompleteCityAdapter(this, cityList); // for testing: uncomment list below
        editTextactv.setAdapter(adapter);
        */


        // OK button for debugging
        //Button button = (Button) findViewById(R.id.button);

        //NEW 16OKT22 fill Autocomplete Adapter with Spinnerdata (ArrayList) by mainactivity
        Bundle bundle = getIntent().getExtras();
        ArrayList<CityItem> arraylist = bundle.getParcelableArrayList("Spinnerdata");
        AutoCompleteTextView editTextactv = findViewById(R.id.actv);
        AutoCompleteCityAdapter adapter = new AutoCompleteCityAdapter(this, arraylist);
        editTextactv.setAdapter(adapter);

        /****************************************************************************************************************/
        // Debugging only:  Display the choosen City in a Toast message when OK button pressed, no write to DB; we do not need this button
        // when button "button" (OK) pressed, setOnClickListener and show ID in the textfield that has to be created
        // info: database (CityID) will be updated in AutoCompleteCityAdapter.convertResultToString()  !!
        /****************************************************************************************************************/
/*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase DB;
                DB = openOrCreateDatabase("Userdata.db", Context.MODE_PRIVATE, null);
                Cursor c = DB.rawQuery("Select * from Userdetails", null);
                StringBuffer buffer = new StringBuffer();
                while (c.moveToNext()) {
                    //buffer.append("Name: " + c.getString(0) + "\n");
                    //buffer.append("Number: " + c.getString(1) + "\n");
                    buffer.append(c.getString(1));
                    //Toast.makeText(Setting.this, "DB value: " + buffer.toString(), Toast.LENGTH_SHORT).show();

                }
                DB.close();

            }
        });
*/




/*  // older Spinner solution
        DB = new DBHelper(this);
        Spinner spnLocale = (Spinner)findViewById(R.id.spinner);

        spnLocale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view,int i, long l) {
                spinner_city = i;


                Toast.makeText(getApplicationContext(), "Data base value i: "+ String.valueOf(i), Toast.LENGTH_LONG).show(); // commented 26DEZ21
                // Your code here
                // TODO: avoid multiple lists of same data inside the spinner, but why? 7 AUG 22

                Cursor res = DB.getdata();
                if (res.getCount() == 0) {
                    Boolean checkinsertdata = DB.insertuserdata("location", String.valueOf(i));}
                    else {
                    Boolean checkupdatedata = DB.updateuserdata("location", String.valueOf(i));
                    }


            }


            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
*/
/*

        //Creating the ArrayAdapter instance holding the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnLocale.setAdapter(aa);
*/
/*
---->>        //Creating the ArrayAdapter instance holding the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,getIntent().getExtras().getIntegerArrayList("Spinnerdata"));
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnLocale.setAdapter(aa);
*/

        /************************************************************************************************************/



        //location = findViewById(R.id.location);
        //city = findViewById(R.id.city);


        //insert = findViewById(R.id.btnInsert);
        //update = findViewById(R.id.btnUpdate);
        //delete = findViewById(R.id.btnDelete);
        //view = findViewById(R.id.btnView);

        // DB = new DBHelper(this);
/*

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationTXT = location.getText().toString();
                String cityTXT = city.getText().toString();

                Boolean checkinsertdata = DB.insertuserdata(locationTXT, cityTXT);
                if (checkinsertdata == true)
                    Toast.makeText(Setting.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Setting.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationTXT = location.getText().toString();
                String cityTXT = city.getText().toString();

                Boolean checkupdatedata = DB.updateuserdata(locationTXT, cityTXT);
                if (checkupdatedata == true)
                    Toast.makeText(Setting.this, "Entry Updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Setting.this, "New Entry Not Updated", Toast.LENGTH_SHORT).show();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationTXT = location.getText().toString();
                Boolean checkudeletedata = DB.deletedata(locationTXT);
                if (checkudeletedata == true)
                    Toast.makeText(Setting.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Setting.this, "Entry Not Deleted", Toast.LENGTH_SHORT).show();
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if (res.getCount() == 0) {
                    Toast.makeText(Setting.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("location :" + res.getString(0) + "\n");
                    buffer.append("city :" + res.getString(1) + "\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setCancelable(true);
                builder.setTitle("User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });
*/





    }





    public void back(View v) {
        Intent i_back=new Intent(Setting.this,MainActivity.class);
        startActivity(i_back);

    }

    /*  // uncomment for testing with Array cityList
    private void fillCityList() {
        cityList = new ArrayList<>();
        cityList.add(new CityItem("Regensburg", "10776"));
        cityList.add(new CityItem("Anklam", "B488"));
        cityList.add(new CityItem("Rijeka", "14317"));
        cityList.add(new CityItem("Weiden", "10688"));
        cityList.add(new CityItem("Walter", "M520"));
    }*/

}
