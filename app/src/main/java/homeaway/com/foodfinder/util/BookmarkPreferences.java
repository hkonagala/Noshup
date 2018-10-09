package homeaway.com.foodfinder.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import homeaway.com.foodfinder.model.venueModel.Venue;

public class BookmarkPreferences {

    private static BookmarkPreferences bookmarkPreferences;

    public static synchronized BookmarkPreferences getBookmarkPreferences() {
        if(bookmarkPreferences == null){
            bookmarkPreferences = new BookmarkPreferences();
        }
        return bookmarkPreferences;
    }

    public void saveBookmarkPreferences(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean(Config.BOOKMARK_KEY, value);
        edt.apply();
    }

    public Boolean getBookmarkPreferences(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Config.BOOKMARK_KEY, value);
    }

    /**
     *     Save and get bookmark list in SharedPreference
     */

    private void saveBookmarks(Context context, List<Venue> bookmarks) {
        SharedPreferences prefs = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String jsonBookmarks = gson.toJson(bookmarks);
        editor.putString(Config.BOOKMARKED_LIST,jsonBookmarks);
        editor.apply();
    }

    public List<Venue> getBookmarks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        String jsonBookmarks = prefs.getString(Config.BOOKMARKED_LIST, null);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<List<Venue>>(){}.getType();
        return gson.fromJson(jsonBookmarks, type);
    }

    public void addBookmark(Context context, Venue bookmark) {
        synchronized (this){
            List<Venue> bookmarks = getBookmarks(context);
            if (bookmarks == null)
                bookmarks = new ArrayList<>();
            bookmarks.add(bookmark);
            saveBookmarks(context, bookmarks);
        }
    }

    public void removeBookmark(Context context, Venue bookmark) {
        synchronized (this) {
            List<Venue> bookmarks = getBookmarks(context);
            if (bookmarks != null) {
                bookmarks.remove(bookmark);
                saveBookmarks(context, bookmarks);
            }
        }
    }

}
