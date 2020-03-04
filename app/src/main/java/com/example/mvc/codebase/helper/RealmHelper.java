package com.example.mvc.codebase.helper;

import android.app.Application;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.models.RecordModel;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * TODO Before start access this class you must follow below steps to setup in your project.
 * <p><strong>1.</strong> Import latest realm gradle</p>
 * <p><strong>(1.1) </strong> Add the following class path dependency to the project level build.gradle file.</p>
 * <a href="https://realm.io/docs/java/latest/#installation">Realm Installation</a>
 * <p><strong>(1.2) </strong> Apply the realm-android plugin to the top of application level build.gradle file.</p>
 * <a href="https://realm.io/docs/java/latest/#installation">Realm Installation</a>
 * <p><strong>(1.3) </strong> Initialize realm sdk in your project in application class</p>
 * <code>Realm.init(applicationContext)</code>
 */

public class RealmHelper {

    // variable declaration
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NICKNAME = "nickName";
    private static final String KEY_FAMILY_NAME = "familyName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE_NO = "phoneNumber";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_BIRTHDAY = "birthdayInMills";

    // class object declaration
    private RealmResults<RecordModel> realmResults;
    private static RealmHelper mRealmHelper;
    private static Realm realm;

    //constructor
    private RealmHelper(Application application) {
        realm = Realm.getDefaultInstance();
    }

    /**
     * This method return instance of RealmHelper class
     *
     * @return mRealmHelper (RealmHelper) : it return realm instance
     */
    public static RealmHelper getInstance() {
        if (mRealmHelper == null) {
            mRealmHelper = new RealmHelper(MyApplication.getInstance());
        }
        return mRealmHelper;
    }

    /**
     * This method return instance of realm class
     *
     * @return realm (Realm) : it return realm instance
     */
    public static Realm getRealm() {
        return realm;
    }

    /**
     * To get all record from database
     *
     * @return RealmResults<RecordModel> : it return list of record from database
     */
    public RealmResults<RecordModel> getAllRecord() {
        return realm.where(RecordModel.class).findAll();
    }

    /**
     * To get all sorted record from database
     *
     * @return List<RecordModel> : it return list of record from database
     */
    public RealmResults<RecordModel> getAllSortedRecord() {
        return realm.where(RecordModel.class).findAllSorted(KEY_NAME);
    }

    /**
     * To get record from database
     *
     * @param id (long) : id
     */
    public RecordModel getRecord(long id) {
        return realm.where(RecordModel.class).equalTo(KEY_ID, id).findFirst();
    }

    /**
     * @return boolean : it return <code>true</code> if file exist in realm database else return <code>false</code>
     */
    public boolean isRealmDatabaseExist() {
        return realm.isEmpty();
    }

    /**
     * To check that any record found in realm or not.
     *
     * @return boolean : it return <code>true</code> if realm database count greater than 0 else return <code>false</code>
     */
    public boolean isRecordFound() {
        return realm.where(RecordModel.class).count() > 0;
    }

    /**
     * To set unique id in database because realm is not provided auto increment functionality
     *
     * @return nextUId (long) : it return increment value if record found in database else
     * return 1.
     */
    private long setUniqueId() {
        //  long nextUId = 1;
        if (isRecordFound()) {
            long nextUId = (long) realm.where(RecordModel.class).max(KEY_ID);
            nextUId += 1;
            return nextUId;
        } else {
            return 1;
        }
    }

    /**
     * To insert a single object in realm database
     *
     * @param recordModel (RecordModel) : object that to be passed to store in database
     */
    public void insertSingleObject(final RecordModel recordModel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                recordModel.setId(setUniqueId());
                realm.copyToRealmOrUpdate(recordModel);
            }
        });
    }

    /**
     * To update value in realm database
     *
     * @param recordModel (RecordModel) : object that to be update in realm database
     */
    public void updateSelectedItemValue(RecordModel recordModel) {
        RecordModel updateRecord = realm.where(RecordModel.class).equalTo(KEY_ID, recordModel.getId()).findFirst();

        realm.beginTransaction();

        updateRecord.setNickName(recordModel.getNickName());
        updateRecord.setName(recordModel.getName());
        updateRecord.setFamilyName(recordModel.getFamilyName());
        updateRecord.setEmail(recordModel.getEmail());
        updateRecord.setPhoneNumber(recordModel.getPhoneNumber());
        updateRecord.setBirthdayInMills(recordModel.getBirthdayInMills());
        updateRecord.setDesignation(recordModel.getDesignation());
        updateRecord.setGender(recordModel.getGender());

        realm.commitTransaction();
    }

    /**
     * TODO stub is generated but developer or programmer need to add code as required.
     * To update value in realm database.
     *
     * @param recordModel (RecordModel) : value that to be updated in realm database
     */
    public void updateRecord(final RecordModel recordModel) {
        final RecordModel updateRecord = realm.where(RecordModel.class).equalTo(KEY_ID, recordModel.getId()).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(updateRecord);
            }
        });
    }

    /**
     * To remove single object from realm database
     *
     * @param deleteModel (RecordModel) : object that to be remove from realm
     */
    public void deleteSelectedItemValue(final RecordModel deleteModel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults = realm.where(RecordModel.class).equalTo(KEY_ID, deleteModel.getId()).findAll();
                realmResults.deleteFirstFromRealm();
            }
        });

    }

    /**
     * To delete all record from realm databse
     */
    public void deleteAll() {
        realm.beginTransaction();
        realm.delete(RecordModel.class);
        realm.commitTransaction();
    }

    /**
     * TO check that new inserted record is already exist or not
     *
     * @param emailId (String) : input String that to be check in realm database
     * @return boolean : it return <code>true</code> if record match found in database else it return <code>false</code>
     */
    public boolean isRecordAlreadyExist(String emailId) {
        return realm.where(RecordModel.class).equalTo(KEY_EMAIL, emailId).count() > 0;
    }

    /**
     * To check when user change existing record is already exist or not.
     *
     * @param recordModel (RecordModel) : object value that to be check in realm database
     * @return boolean : it return <code>true</code> if new record match found in database else it return <code>false</code>
     */
    public boolean isRecordAlreadyExist(RecordModel recordModel) {
        return realm.where(RecordModel.class).equalTo(KEY_EMAIL, recordModel.getEmail()).notEqualTo(KEY_ID, recordModel.getId()).count() > 0;
    }

}
