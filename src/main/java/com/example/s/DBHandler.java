package com.example.s;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String ID_COL = "id";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, "manual.db", null, 1);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table manual(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,title text,amount text,day text,month text,year text,category text,transaction_type text,date date)");
    }

    // this method is use to add new course to our sqlite database.
    public int insert(Context con, CourseModal courseModal) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(courseModal.getDay().trim().length()==1){
           courseModal.setDay("0"+courseModal.getDay());
        }
        ContentValues values = new ContentValues();
        values.put("title", courseModal.getTitle());
        values.put("amount", courseModal.getAmout());
        values.put("day", courseModal.getDay());
        values.put("month", courseModal.getMonth());
        values.put("year", courseModal.getYear());
        values.put("category", courseModal.getCategory());
        values.put("transaction_type", courseModal.getTransactionType());
        values.put("date",courseModal.getYear()+"/"+courseModal.getMonth()+"/"+courseModal.getDay());
        // after adding all values we are passing
        // content values to our table.

        long x = db.insert("manual", null, values);
        if (x > 0) {
            Toast.makeText(con, "Sucessfully Inserted" + courseModal.getTransactionType(), Toast.LENGTH_SHORT).show();
            String sql="SELECT * FROM manual ORDER BY id DESC LIMIT 1";
            Cursor cur =db.rawQuery(sql,null);
            cur.moveToNext();
            return cur.getInt(cur.getColumnIndex("id"));
        }
        else
            Toast.makeText(con, "Failed to insert", Toast.LENGTH_SHORT).show();
        // at last we are closing our
        // database after adding database.
        db.close();
        return 0;


    }

    // we have created a new method for reading all the courses.
    public ArrayList<CourseModal> read(String month,String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CourseModal> lst = new ArrayList<>();
        String sql="select * from manual where month=? and year=? order by day";
        Cursor cur =db.rawQuery(sql,new String[]{month,year});
        while (cur.moveToNext()) {
            CourseModal ref = new CourseModal();
            //title text,amount text,day text,month text,year text,category text,transaction_type text
            ref.setId(cur.getInt(cur.getColumnIndex("id")));
            ref.setTitle(cur.getString(cur.getColumnIndex("title")));
            ref.setAmout(cur.getString(cur.getColumnIndex("amount")));
            ref.setDay(cur.getString(cur.getColumnIndex("day")));
            ref.setMonth(cur.getString(cur.getColumnIndex("month")));
            ref.setYear(cur.getString(cur.getColumnIndex("year")));
            ref.setCategory(cur.getString(cur.getColumnIndex("category")));
            ref.setTransactionType(cur.getString(cur.getColumnIndex("transaction_type")));
            lst.add(ref);
        }
        return lst;
    }
//
//    // below is the method for updating our courses
//    public void updateCourse(String originalCourseName, String courseName, String courseDescription,
//                             String courseTracks, String courseDuration) {
//
//        // calling a method to get writable database.
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        // on below line we are passing all values
//        // along with its key and value pair.
//        values.put(NAME_COL, courseName);
//        values.put(DURATION_COL, courseDuration);
//        values.put(DESCRIPTION_COL, courseDescription);
//        values.put(TRACKS_COL, courseTracks);
//
//        // on below line we are calling a update method to update our database and passing our values.
//        // and we are comparing it with name of our course which is stored in original name variable.
//        db.update(TABLE_NAME, values, "name=?", new String[]{originalCourseName});
//        db.close();
//    }
//
//    // below is the method for deleting our course.
//    public void deleteCourse(String courseName) {
//
//        // on below line we are creating
//        // a variable to write our database.
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // on below line we are calling a method to delete our
//        // course and we are comparing it with our course name.
//        db.delete(TABLE_NAME, "name=?", new String[]{courseName});
//        db.close();
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + "manual");
        onCreate(db);
    }

    public double gettotalofmonth(String month, String year) {
        double amt=0;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CourseModal> lst = new ArrayList<>();
        String sql="select * from manual where month=? and year=? order by day";
        Cursor cur =db.rawQuery(sql,new String[]{month,year});
        while (cur.moveToNext()) {
            CourseModal ref = new CourseModal();
            //title text,amount text,day text,month text,year text,category text,transaction_type text
            ref.setAmout(cur.getString(cur.getColumnIndex("amount")));
            ref.setTransactionType(cur.getString(cur.getColumnIndex("transaction_type")));
            if(cur.getString(cur.getColumnIndex("transaction_type")).equals("Expense")){
                amt=amt+Double.parseDouble(cur.getString(cur.getColumnIndex("amount")));
            }
            lst.add(ref);
        }
        return amt;
    }

    public boolean delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int x = db.delete("manual","id = ?",new String[]{id+""});

        if(x > 0)
            return true;
        else
            return false;

    }
}

