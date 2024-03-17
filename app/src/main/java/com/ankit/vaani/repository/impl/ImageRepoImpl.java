package com.ankit.vaani.repository.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ankit.vaani.model.Image;
import com.ankit.vaani.repository.ImageRepo;

import java.util.ArrayList;
import java.util.List;

public class ImageRepoImpl extends SQLiteOpenHelper implements ImageRepo {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "vaani";

    // Table Names
    private static final String DB_TABLE = "images";

    // column names
    private static final String KEY_NAME = "description";
    private static final String KEY_IMAGE = "data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_NAME + " TEXT," +
            KEY_IMAGE + " BLOB);";

    public ImageRepoImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }

    @Override
    public void addImage(Image image) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, image.getDescription());
        cv.put(KEY_IMAGE, image.getData());
        database.insert( DB_TABLE, null, cv );
    }

    @SuppressLint("Range")
    @Override
    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(
                String.format("SELECT * FROM %s", DB_TABLE), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Image image = new Image();
                image.setDescription(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                image.setData(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
                images.add(image);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return images;
    }
}
