package com.roman.ttu.client.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roman.ttu.client.rest.model.ImagesWrapper;
import com.roman.ttu.client.rest.model.UserImagesWrapper;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.roman.ttu.client.db.TessClientDatabase.*;
import static com.roman.ttu.client.rest.model.ImagesWrapper.*;

public class PendingImagesDAO {

    TessClientDatabaseHelper dbHelper;

    public PendingImagesDAO(Context context) {
        dbHelper = new TessClientDatabaseHelper(context);
    }

    public void save(ImagesWrapper imagesWrapper, String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ImageWrapper regNumberImageWrapper = imagesWrapper.regNumberImage;
        ImageWrapper totalCostImageWrapper = imagesWrapper.totalCostImage;

        ContentValues contentValues = new ContentValues();

        contentValues.put(UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_IMAGE, regNumberImageWrapper.encodedImage);
        contentValues.put(UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_FILE_EXTENSION, regNumberImageWrapper.fileExtension);

        contentValues.put(UserPendingImages.COLUMN_NAME_TOTAL_COST_IMAGE, totalCostImageWrapper.encodedImage);
        contentValues.put(UserPendingImages.COLUMN_NAME_TOTAL_COST_FILE_EXTENSION, totalCostImageWrapper.fileExtension);
        contentValues.put(UserPendingImages.COLUMN_NAME_USER_ID, userId);
        db.insert(UserPendingImages.TABLE_NAME, null, contentValues);
    }

    public Collection<UserImagesWrapper> find(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserPendingImages._ID,
                UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_IMAGE,
                UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_FILE_EXTENSION,
                UserPendingImages.COLUMN_NAME_TOTAL_COST_IMAGE,
                UserPendingImages.COLUMN_NAME_USER_ID,
                UserPendingImages.COLUMN_NAME_INSERTED_AT

        };

        String selection = UserPendingImages.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = {userId};
        String sortOrder = UserPendingImages.COLUMN_NAME_INSERTED_AT + " DESC";

        Cursor c = db.query(UserPendingImages.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return mapUserPendingImages(c);
    }

    private Collection<UserImagesWrapper> mapUserPendingImages(Cursor c) {
        Set<UserImagesWrapper> userImages = new HashSet<>();
        if(c != null && c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(UserPendingImages._ID));
                String enterpriseIdImage = c.getString(c.getColumnIndex(UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_IMAGE));
                String enterpriseIdFileExtension = c.getString(c.getColumnIndex(UserPendingImages.COLUMN_NAME_ENTERPRISE_ID_FILE_EXTENSION));
                String totalCostImage = c.getString(c.getColumnIndex(UserPendingImages.COLUMN_NAME_TOTAL_COST_IMAGE));
                String totalCostFileExtension = c.getString(c.getColumnIndex(UserPendingImages.COLUMN_NAME_TOTAL_COST_FILE_EXTENSION));
                Object insertedAt = c.getString(c.getColumnIndex(UserPendingImages.COLUMN_NAME_INSERTED_AT));

                UserImagesWrapper userImagesWrapper = new UserImagesWrapper(id, new ImageWrapper(enterpriseIdImage, enterpriseIdFileExtension),
                        new ImageWrapper(totalCostImage, totalCostFileExtension),
                        new Date());

                userImages.add(userImagesWrapper);

            } while (c.moveToNext());
        }

        return userImages;
    }
}