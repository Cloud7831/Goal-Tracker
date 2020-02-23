package com.cloud7831.goaltracker.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class GoalProvider extends ContentProvider {

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int GOALS = 100;
    private static final int GOAL_ID = 101;
    static {
        /*
         * The calls to addURI() go here for all of the content URI patterns that the provider should recognize.
         */
        matcher.addURI(GoalsContract.CONTENT_AUTHORITY, GoalsContract.PATH_GOALS, GOALS);
        matcher.addURI(GoalsContract.CONTENT_AUTHORITY, GoalsContract.PATH_GOALS + "/#", GOAL_ID);
    }


    public static final String LOG_TAG = GoalProvider.class.getSimpleName();
    private GoalDbHelper db;

    @Override
    public boolean onCreate(){
        db = new GoalDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        // Get readable database
        SQLiteDatabase database = db.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = matcher.match(uri);
        switch (match) {
            case GOALS:
                // For the GOALS code, query the goals table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.

                cursor = database.query(GoalsContract.GoalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case GOAL_ID:
                // For the GOAL_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.cloud7831/goaltracker/goals/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = GoalsContract.GoalEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the goals table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(GoalsContract.GoalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor so we know
        // what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = matcher.match(uri);
        switch (match) {
            case GOALS:
                sanityCheck(contentValues);
                return insertGoal(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a goal into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertGoal(Uri uri, ContentValues values) {

        SQLiteDatabase database = db.getWritableDatabase();

        long id = database.insert(GoalsContract.GoalEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the goal content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = matcher.match(uri);
        switch (match) {
            case GOALS:
                // Delete all rows that match the selection and selection args
                return deleteGoal(uri, selection, selectionArgs);
            case GOAL_ID:
                // Delete a single row given by the ID in the URI
                selection = GoalsContract.GoalEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteGoal(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = matcher.match(uri);
        if(contentValues.size() == 0){
            // No reason to try to update if there's nothing to update.
            return 0;
        }
        switch (match) {
            case GOALS:
                return updateGoal(uri, contentValues, selection, selectionArgs);
            case GOAL_ID:
                // For the GOAL_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = GoalsContract.GoalEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateGoal(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update goals in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more goals).
     * Return the number of rows that were successfully updated.
     */
    private int updateGoal(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        updateSanityCheck(values);

        SQLiteDatabase database = db.getWritableDatabase();

        int rowsAltered = database.update(GoalsContract.GoalEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsAltered > 0){
            // Notify all listeners that the data has changed for the goal content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAltered;
    }

    /**
     * Delete goals in the database. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully deleted.
     */
    private int deleteGoal(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = db.getWritableDatabase();

        int rowsAltered = database.delete(GoalsContract.GoalEntry.TABLE_NAME, selection, selectionArgs);

        if(rowsAltered > 0){
            // Notify all listeners that the data has changed for the goal content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAltered;
    }


    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case GOALS:
                return GoalsContract.GoalEntry.CONTENT_LIST_TYPE;
            case GOAL_ID:
                return GoalsContract.GoalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    public boolean sanityCheck(ContentValues values){

        if(TextUtils.isEmpty(values.getAsString(GoalsContract.GoalEntry.GOAL_NAME))){
            throw new IllegalArgumentException("Goal requires a name.");
        }

        Integer quota = values.getAsInteger(GoalsContract.GoalEntry.GOAL_QUOTA);
        if(quota == null || quota < 0){
            throw new IllegalArgumentException("Quota needs to be zero or greater than zero.");
        }

        Integer units = values.getAsInteger(GoalsContract.GoalEntry.GOAL_UNITS);
        if(units == null || !GoalsContract.GoalEntry.isValidUnits(units)){
            throw new IllegalArgumentException("Goal units is not one of the accepted values.");
        }

        Integer freq = values.getAsInteger(GoalsContract.GoalEntry.GOAL_FREQUENCY);
        if(freq == null || !GoalsContract.GoalEntry.isValidFrequency(freq)){
            throw new IllegalArgumentException("Goal frequency is not one of the accepted values.");
        }

        Integer intent = values.getAsInteger(GoalsContract.GoalEntry.GOAL_INTENTION);
        if(intent == null || !GoalsContract.GoalEntry.isValidIntention(intent)){
            throw new IllegalArgumentException("Goal intention is not one of the accepted values.");
        }

        return true;
    }

    public boolean updateSanityCheck(ContentValues values){
        if(values.containsKey(GoalsContract.GoalEntry.GOAL_NAME)){
            if(TextUtils.isEmpty(values.getAsString(GoalsContract.GoalEntry.GOAL_NAME))){
                throw new IllegalArgumentException("Goal requires a name.");
            }
        }

        if(values.containsKey(GoalsContract.GoalEntry.GOAL_QUOTA)) {
            Integer quota = values.getAsInteger(GoalsContract.GoalEntry.GOAL_QUOTA);
            if (quota == null || quota < 0) {
                throw new IllegalArgumentException("Quota needs to be zero or greater than zero.");
            }
        }

        if(values.containsKey(GoalsContract.GoalEntry.GOAL_UNITS)) {
            Integer units = values.getAsInteger(GoalsContract.GoalEntry.GOAL_UNITS);
            if (units == null || !GoalsContract.GoalEntry.isValidUnits(units)) {
                throw new IllegalArgumentException("Goal units is not one of the accepted values.");
            }
        }

        if(values.containsKey(GoalsContract.GoalEntry.GOAL_FREQUENCY)) {
            Integer freq = values.getAsInteger(GoalsContract.GoalEntry.GOAL_FREQUENCY);
            if (freq == null || !GoalsContract.GoalEntry.isValidFrequency(freq)) {
                throw new IllegalArgumentException("Goal frequency is not one of the accepted values.");
            }
        }

        if(values.containsKey(GoalsContract.GoalEntry.GOAL_INTENTION)) {
            Integer intent = values.getAsInteger(GoalsContract.GoalEntry.GOAL_INTENTION);
            if (intent == null || !GoalsContract.GoalEntry.isValidIntention(intent)) {
                throw new IllegalArgumentException("Goal intention is not one of the accepted values.");
            }
        }
        return true;
    }


}
