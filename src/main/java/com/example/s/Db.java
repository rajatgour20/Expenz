package com.example.s;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Db extends SQLiteOpenHelper {

    Context context;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageinbyte;
    //private byte[] imageinbyte = new byte[10000];

    public Db(@Nullable Context context) {
        super(context, "db.db", null, 1);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table db (imagename text,image LONGBLOB)";
        db.execSQL(sql);
        Toast.makeText(context, "table created successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeimage(Modelclass obj) {
        SQLiteDatabase o = getWritableDatabase();
        Bitmap imagetostore = obj.getImage();
        byteArrayOutputStream = new ByteArrayOutputStream();
        imagetostore.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageinbyte = byteArrayOutputStream.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imagename", obj.getImagename());
        contentValues.put("image", imageinbyte);
        long x = o.insert("db", null, contentValues);
        if (x >0) {
            Toast.makeText(context, "inserted successfully", Toast.LENGTH_LONG).show();
            o.close();
        } else {
            Toast.makeText(context, "inserted not successfully", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<Modelclass> getall() {
        SQLiteDatabase o=this.getReadableDatabase();
        ArrayList<Modelclass> arrayList=new ArrayList<>();
        Cursor cursor=o.rawQuery("select *from db",null);
        if(cursor.getCount()!=0)
        {
            while (cursor.moveToNext())
            {
                String name=cursor.getString(0);
                byte[] image=cursor.getBlob(1);

                Bitmap bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
                arrayList.add(new Modelclass(name,bitmap));
            }
            return arrayList;
        }
        else
        {
            Toast.makeText(context, "no value", Toast.LENGTH_SHORT).show();
            return null;
        }

    }
}