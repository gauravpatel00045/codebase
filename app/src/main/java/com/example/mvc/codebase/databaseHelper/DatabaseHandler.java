package com.example.mvc.codebase.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mvc.codebase.models.RecordModel;
import com.example.mvc.codebase.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO stub is generated but developer or programmer need to add code as required.
 * This class use to access SQLite database for <strong>CRUD (Create, Read, Update and Delete) operation.</strong>
 * <br>Table column name, Model class name and Method name ({@link RecordModel}) are defined
 * to illustrate as an example. Use or Modify it in your stub with your own requirement.</br>
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // variable declaration

    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "RecordDetail";

    // table name
    private static final String TABLE_NAME = "EmployeeRecord";

    // table column name
    private static final String KEY_ID = "ID";
    private static final String KEY_IMAGE_PATH = "IMAGE_PATH";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_PASSWORD = "PASSWORD";
    private static final String KEY_NICKNAME = "NICK_NAME";
    private static final String KEY_FAMILY_NAME = "FAMILY_NAME";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_PHONE_NO = "PHONE";
    private static final String KEY_DESIGNATION = "DESIGNATION";
    private static final String KEY_BIRTHDAY = "BIRTHDAY_IN_MILLIS";
    private static final String KEY_GENDER = "GENDER";


    // class object declaration
    private static DatabaseHandler dbInstance;


    // constructor
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * To get a DatabaseHandler class instance
     *
     * @param context : context
     * @return dbInstance (DatabaseHandler) : it return DatabaseHandler instance
     */
    public static synchronized DatabaseHandler getInstance(Context context) {
        if (dbInstance == null)
            dbInstance = new DatabaseHandler(context);
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE  TABLE
        String CREATE_RECORD_DETAIL = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_FAMILY_NAME + " TEXT,"
                + KEY_NICKNAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_PHONE_NO + " INTEGER,"
                + KEY_DESIGNATION + " TEXT,"
                + KEY_BIRTHDAY + " TEXT,"
                + KEY_GENDER + " INTEGER "
                + ")";

        db.execSQL(CREATE_RECORD_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        //creating table again
        onCreate(db);
    }


    /**
     * To insert a record in sqLite database
     *
     * @param recordModel (RecordModel): value that to be passed to store in database
     */
    public void addRecord(RecordModel recordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_PATH, recordModel.getImagePath());
        values.put(KEY_NAME, recordModel.getName());
        values.put(KEY_FAMILY_NAME, recordModel.getFamilyName());
        values.put(KEY_NICKNAME, recordModel.getNickName());
        values.put(KEY_EMAIL, recordModel.getEmail());
        values.put(KEY_PASSWORD, recordModel.getPassword());
        values.put(KEY_PHONE_NO, recordModel.getPhoneNumber());
        values.put(KEY_DESIGNATION, recordModel.getDesignation());
        values.put(KEY_BIRTHDAY, recordModel.getBirthdayInMills());
        values.put(KEY_GENDER, recordModel.getGender());
        // Inserting row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * To update value in sqLite database
     *
     * @param recordModel (RecordModel) : value that to be update in sqLite database
     */
    public void updateRecord(RecordModel recordModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_PATH, recordModel.getImagePath());
        values.put(KEY_NAME, recordModel.getName());
        values.put(KEY_FAMILY_NAME, recordModel.getFamilyName());
        values.put(KEY_NICKNAME, recordModel.getNickName());
        values.put(KEY_EMAIL, recordModel.getEmail());
        values.put(KEY_PASSWORD, recordModel.getPassword());
        values.put(KEY_PHONE_NO, recordModel.getPhoneNumber());
        values.put(KEY_DESIGNATION, recordModel.getDesignation());
        values.put(KEY_BIRTHDAY, recordModel.getBirthdayInMills());
        values.put(KEY_GENDER, recordModel.getGender());

        // updating row
        db.update(TABLE_NAME, values, KEY_ID + " = " + recordModel.getId(), null);
        db.close();
    }

    /**
     * To remove data from sqLite database.
     * <br> Can also use deleteQuery to remove data like below</br>
     * <pre {@code
     * String deleteQuery = " DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + recordModel.getId();
     * db.execSQL(deleteQuery);
     * OR
     * db.delete(TABLE_NAME, KEY_ID + " = " + recordModel.getId(), null);
     * }
     *
     * @param recordModel (RecordModel) : value that to be remove from sqLite database
     */
    public void deleteRecord(RecordModel recordModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = " DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + recordModel.getId();

        // delete row
        db.delete(TABLE_NAME, KEY_ID + " = " + recordModel.getId(), null);
        db.close();
    }

    /**
     * To get all record from sqLite database
     *
     * @return List<RecordModel> : it return list of record from sqLite database
     */
    public List<RecordModel> getAllRecord() {
        List<RecordModel> recordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RecordModel addRecord = new RecordModel();

                addRecord.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                addRecord.setImagePath(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)));
                addRecord.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                addRecord.setFamilyName(cursor.getString(cursor.getColumnIndex(KEY_FAMILY_NAME)));
                addRecord.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
                addRecord.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                addRecord.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NO)));
                addRecord.setDesignation(cursor.getString(cursor.getColumnIndex(KEY_DESIGNATION)));
                addRecord.setBirthdayInMills(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_BIRTHDAY))));
                addRecord.setGender(cursor.getInt(cursor.getColumnIndex(KEY_GENDER)));

                recordList.add(addRecord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordList;
    }

    /**
     * To get all sorted record from sqLite database.
     *
     * @return List<RecordModel> : it return record list from sqLite database
     */
    public List<RecordModel> getAllSortedRecord() {
        List<RecordModel> recordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + KEY_NAME + " COLLATE NOCASE ASC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RecordModel addRecord = new RecordModel();

                addRecord.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                addRecord.setImagePath(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)));
                addRecord.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                addRecord.setFamilyName(cursor.getString(cursor.getColumnIndex(KEY_FAMILY_NAME)));
                addRecord.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
                addRecord.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                addRecord.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NO)));
                addRecord.setDesignation(cursor.getString(cursor.getColumnIndex(KEY_DESIGNATION)));
                addRecord.setBirthdayInMills(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_BIRTHDAY))));
                addRecord.setGender(cursor.getInt(cursor.getColumnIndex(KEY_GENDER)));

                recordList.add(addRecord);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordList;
    }

    /**
     * To check that new inserting record is already exist or not.
     *
     * @param emailId (String) : email id that to be match with database record
     * @return boolean : it return true if new record match found in database else it return false
     */
    public boolean isRecordAlreadyExist(String emailId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_EMAIL + " = " + "'" + emailId + "'";

        Cursor mCursor = db.rawQuery(query, null);
        mCursor.close();
        db.close();
        return mCursor.getCount() > 0;
    }

    /**
     * To check when existing record modify the value is already exist or not. When existing user modify the data
     * than except to check current user's data and compare with other.
     *
     * @param recordModel (RecordModel) : object value that to be check in sqLite database
     * @param emailId     (String) : email id that to be match with database record
     * @return boolean : it return true if record match found in database else it return false
     */
    public boolean isRecordAlreadyExist(RecordModel recordModel, String emailId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + KEY_EMAIL + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " NOT IN " + "(" + recordModel.getId() + ") " +
                " AND " + KEY_EMAIL + " = " + "'" + emailId + "'";

        Cursor mCursor = db.rawQuery(query, null);
        mCursor.close();
        db.close();
        return mCursor.getCount() > 0;
    }


    /**
     * To check that any record found in sqLite database or not.
     *
     * @return boolean : it return true if realm database count greater than 0 else return false
     */
    public boolean isRecordFound() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = " SELECT  * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        return cursor.getCount() > 0;

    }

    /**
     * To delete all record from sqLite database.
     */
    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /**
     * TODO stub is generated but developer or programmer need to add code as required.
     * To validate credentials with database record.
     *
     * @param userId   : user name that to be match with SQLite database record
     * @param password : password that to be match with SQLite database record
     */
    public RecordModel validateWithDatabaseRecord(String userId, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = " SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_EMAIL + " = " + "'" + userId + "'" + " AND " + KEY_PASSWORD + " = " + "'" + password + "'";

        RecordModel validRecord = new RecordModel();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            validRecord.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            validRecord.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            validRecord.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            validRecord.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));

            cursor.close();
            db.close();
            return validRecord;
        } else {
            validRecord.setEmail(Constants.DEFAULT_BLANK_STRING);
            validRecord.setPassword(Constants.DEFAULT_BLANK_STRING);

            cursor.close();
            db.close();
            return validRecord;

        }


    }

}
