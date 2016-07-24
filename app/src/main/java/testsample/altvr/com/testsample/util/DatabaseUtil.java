package testsample.altvr.com.testsample.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import testsample.altvr.com.testsample.vo.PhotoVo;

/**
 * Created by tejus on 4/14/2016.
 */
public class DatabaseUtil extends SQLiteOpenHelper {
    private LogUtil log = new LogUtil(DatabaseUtil.class);

    private static final int DATABASE_VERSION = 2;
    //DB and tables
    private static final String DATABASE_NAME = "imagesearcher";
    private static final String TABLE_PHOTOS = "photos";

    //Columns for Images table
    private static final String KEY_PHOTO_ID = "id";
    private static final String KEY_PHOTO_PAGE_URL = "page_url";
    private static final String KEY_PHOTO_TYPE = "type";
    private static final String KEY_PHOTO_TAG = "tag";
    private static final String KEY_PHOTO_PREVIEW_URL = "preview_url";
    private static final String KEY_PHOTO_PREVIEW_WIDTH = "preview_width";
    private static final String KEY_PHOTO_PREVIEW_HEIGHT = "preview_height";
    private static final String KEY_PHOTO_WEBFORMAT_URL = "webformat_url";
    private static final String KEY_PHOTO_WEBFORMAT_WIDTH = "webformat_width";
    private static final String KEY_PHOTO_WEBFORMAT_HEIGHT = "webformat_height";
    private static final String KEY_PHOTO_IMAGE_WIDTH = "image_width";
    private static final String KEY_PHOTO_IMAGE_HEIGHT = "image_height";
    private static final String KEY_PHOTO_VIEW = "view";
    private static final String KEY_PHOTO_DOWNLOAD = "download";
    private static final String KEY_PHOTO_FAVORITES = "favorites";
    private static final String KEY_PHOTO_LIKES = "likes";
    private static final String KEY_PHOTO_COMMENTS = "comments";
    private static final String KEY_PHOTO_USER_ID = "user_id";
    private static final String KEY_PHOTO_USER = "user";
    private static final String KEY_PHOTO_USER_IMAGEURL = "user_image_url";


    SQLiteDatabase mDb;

    private String[] ALL_COLUMNS = {
            KEY_PHOTO_ID,
            KEY_PHOTO_PAGE_URL,
            KEY_PHOTO_TYPE,
            KEY_PHOTO_TAG,
            KEY_PHOTO_PREVIEW_URL,
            KEY_PHOTO_PREVIEW_WIDTH,
            KEY_PHOTO_PREVIEW_HEIGHT,
            KEY_PHOTO_WEBFORMAT_URL,
            KEY_PHOTO_WEBFORMAT_WIDTH,
            KEY_PHOTO_WEBFORMAT_HEIGHT,
            KEY_PHOTO_IMAGE_WIDTH,
            KEY_PHOTO_IMAGE_HEIGHT,
            KEY_PHOTO_VIEW,
            KEY_PHOTO_DOWNLOAD,
            KEY_PHOTO_FAVORITES,
            KEY_PHOTO_LIKES,
            KEY_PHOTO_COMMENTS,
            KEY_PHOTO_USER_ID,
            KEY_PHOTO_USER,
            KEY_PHOTO_USER_IMAGEURL,
    };

    public DatabaseUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_PHOTOS + "("
                + KEY_PHOTO_ID + " STRING PRIMARY KEY UNIQUE, "
                + KEY_PHOTO_PAGE_URL + " TEXT, "
                + KEY_PHOTO_TYPE + " TEXT, "
                + KEY_PHOTO_TAG + " TEXT, "
                + KEY_PHOTO_PREVIEW_URL + " TEXT, "
                + KEY_PHOTO_PREVIEW_WIDTH + " LONG, "
                + KEY_PHOTO_PREVIEW_HEIGHT + " LONG, "
                + KEY_PHOTO_WEBFORMAT_URL + " TEXT, "
                + KEY_PHOTO_WEBFORMAT_WIDTH + " LONG, "
                + KEY_PHOTO_WEBFORMAT_HEIGHT + " LONG, "
                + KEY_PHOTO_IMAGE_WIDTH + " LONG, "
                + KEY_PHOTO_IMAGE_HEIGHT + " LONG, "
                + KEY_PHOTO_VIEW + " LONG, "
                + KEY_PHOTO_DOWNLOAD + " LONG, "
                + KEY_PHOTO_FAVORITES + " LONG, "
                + KEY_PHOTO_LIKES + " LONG, "
                + KEY_PHOTO_COMMENTS + " LONG, "
                + KEY_PHOTO_USER_ID + " LONG, "
                + KEY_PHOTO_USER + " TEXT, "
                + KEY_PHOTO_USER_IMAGEURL + " TEXT)";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    /**
     * YOUR CODE HERE
     * <p/>
     * For part 1b, you should fill in the various CRUD operations below to manipulate the db
     * returned by getWritableDatabase() to store/load photos.
     */

    public boolean saveOrUpdatePhoto(PhotoVo photoVo) {

        long id = -1;
        try {

            mDb.beginTransaction();

            ContentValues values = new ContentValues();

            values.put(KEY_PHOTO_ID, photoVo.id);
            values.put(KEY_PHOTO_PAGE_URL, photoVo.pageURL);
            values.put(KEY_PHOTO_TYPE, photoVo.type);
            values.put(KEY_PHOTO_TAG, photoVo.tags);
            values.put(KEY_PHOTO_PREVIEW_URL, photoVo.previewURL);
            values.put(KEY_PHOTO_PREVIEW_WIDTH, photoVo.previewWidth);
            values.put(KEY_PHOTO_PREVIEW_HEIGHT, photoVo.previewHeight);
            values.put(KEY_PHOTO_WEBFORMAT_URL, photoVo.webformatURL);
            values.put(KEY_PHOTO_WEBFORMAT_WIDTH, photoVo.webformatWidth);
            values.put(KEY_PHOTO_WEBFORMAT_HEIGHT, photoVo.webformatHeight);
            values.put(KEY_PHOTO_IMAGE_WIDTH, photoVo.imageWidth);
            values.put(KEY_PHOTO_IMAGE_HEIGHT, photoVo.imageHeight);
            values.put(KEY_PHOTO_VIEW, photoVo.views);
            values.put(KEY_PHOTO_DOWNLOAD, photoVo.downloads);
            values.put(KEY_PHOTO_FAVORITES, photoVo.favorites);
            values.put(KEY_PHOTO_LIKES, photoVo.likes);
            values.put(KEY_PHOTO_COMMENTS, photoVo.comments);
            values.put(KEY_PHOTO_USER_ID, photoVo.user_id);
            values.put(KEY_PHOTO_USER, photoVo.user);
            values.put(KEY_PHOTO_USER_IMAGEURL, photoVo.userImageURL);


            id = mDb.insertWithOnConflict(TABLE_PHOTOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (id == -1) {
                log.e("insert failure");
            } else {
                mDb.setTransactionSuccessful();
                log.d("insert database " + photoVo.id + " ok");
            }
        } catch (Exception e) {
            log.e("insert database error");
        } finally {
            mDb.endTransaction();
        }
        return (id != -1);
    }


    public PhotoVo getPhoto(String id) {

        PhotoVo photoVo = null;
        try {

            mDb.beginTransaction();
            String whereClause = KEY_PHOTO_ID + " == ?";
            String[] whereArgs = new String[]{id};

            Cursor cursor = mDb.query(TABLE_PHOTOS,
                    ALL_COLUMNS, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                photoVo = cursorToPhoto(cursor);
                cursor.moveToNext();
            }

            cursor.close();
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            log.e("get database error");
        } finally {
            mDb.endTransaction();
        }

        return photoVo;

    }

    private PhotoVo cursorToPhoto(Cursor cursor) {
        PhotoVo photoVo = null;
        if (cursor != null) {
            photoVo = new PhotoVo();
            photoVo.id = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_ID));
            photoVo.pageURL = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_PAGE_URL));
            photoVo.type = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_TYPE));
            photoVo.tags = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_TAG));
            photoVo.previewURL = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_PREVIEW_URL));
            photoVo.previewWidth = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_PREVIEW_WIDTH));
            photoVo.previewHeight = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_PREVIEW_HEIGHT));
            photoVo.webformatURL = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_WEBFORMAT_URL));
            photoVo.webformatWidth = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_WEBFORMAT_WIDTH));
            photoVo.webformatHeight = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_WEBFORMAT_HEIGHT));
            photoVo.imageWidth = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_IMAGE_WIDTH));
            photoVo.imageHeight = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_IMAGE_HEIGHT));
            photoVo.views = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_VIEW));
            photoVo.downloads = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_DOWNLOAD));
            photoVo.favorites = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_FAVORITES));
            photoVo.likes = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_LIKES));
            photoVo.comments = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_COMMENTS));
            photoVo.user_id = cursor.getInt(cursor.getColumnIndex(KEY_PHOTO_USER_ID));
            photoVo.user = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_USER));
            photoVo.userImageURL = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_USER_IMAGEURL));
        }
        return photoVo;


    }

    public void deletePhoto(String id) {

        try {

            mDb.beginTransaction();
            mDb.delete(TABLE_PHOTOS, KEY_PHOTO_ID + " = " + id, null);
            mDb.setTransactionSuccessful();
            log.d("delete database " + id + " ok");
        } catch (Exception e) {
            log.e("delete database error");
        } finally {
            mDb.endTransaction();
        }
    }

    public List<PhotoVo> getAllPhoto() {

        List<PhotoVo> photoVoList = new ArrayList<>();
        try {

            mDb.beginTransaction();


            Cursor cursor = mDb.query(TABLE_PHOTOS,
                    ALL_COLUMNS, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PhotoVo photoVo = cursorToPhoto(cursor);
                photoVoList.add(photoVo);
                cursor.moveToNext();
            }

            cursor.close();
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            log.e("get all database error");
        } finally {
            mDb.endTransaction();
        }

        return photoVoList;

    }


}
