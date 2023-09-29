
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String COL_1 = "ID";
    public static final String COL_2 = "dbtype";
    public static final String COL_3 = "dbkey";
    public static final String COL_4 = "dbval";

    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userdetails(ID INTEGER PRIMARY KEY AUTOINCREMENT, dbtype TEXT, dbkey TEXT, dbval TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userdetails");
    }

    public Boolean insertuserdata(String dbkey, String dbval, String dbtype)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, dbtype);
        contentValues.put(COL_3, dbkey);
        contentValues.put(COL_4, dbval);
        long result=DB.insert("Userdetails", null, contentValues);
        DB.close();
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }


    public Boolean updateuserdata(String dbkey, String dbval, String dbtype) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, dbtype);
        contentValues.put(COL_3, dbkey);
        contentValues.put(COL_4, dbval);
        Cursor cursor = DB.rawQuery("Select * from Userdetails where dbkey=?", new String[]{dbkey});
        if (cursor.getCount() > 0) {
            long result = DB.update("Userdetails", contentValues, "dbkey=?", new String[]{dbkey});
            cursor.close();
            DB.close();
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }


    public Boolean deletedata (String dbkey)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where dbkey = ?", new String[]{dbkey});
        if (cursor.getCount() > 0) {
            long result = DB.delete("Userdetails", "dbkey=?", new String[]{dbkey});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails", null);
        return cursor;

    }

    @Override
    public synchronized void close() {
        super.close();
    }

}
