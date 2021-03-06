package homeaway.com.placefinder.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import homeaway.com.placefinder.model.venueModel.Venue;

/**
 * Helper class for storing and retrieving shared preferences
 */
public class FavoritePreferences {

    private static FavoritePreferences favoritePreferences;

    public static synchronized FavoritePreferences getFavoritePreferences() {
        if(favoritePreferences == null){
            favoritePreferences = new FavoritePreferences();
        }
        return favoritePreferences;
    }

    /**
     *     Save and get favorite map in SharedPreference
     */

    private void saveFavorites(Context context, Map<String, Venue> favorites) {
        SharedPreferences prefs = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(Config.FAVORITES_MAP,jsonFavorites);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Map<String, Venue> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        String jsonFavoritess = prefs.getString(Config.FAVORITES_MAP, null);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Map<String, Venue>>(){}.getType();
        return gson.fromJson(jsonFavoritess, type);
    }

    public void addFavorites(Context context, Venue favorite) {
        synchronized (this){
            Map<String, Venue> favorites = getFavorites(context);
            if (favorites == null) {
                favorites = new HashMap<>();
            }
            favorites.put(favorite.getId(), favorite);
            saveFavorites(context, favorites);
        }
    }

    public void removeFavorites(Context context, Venue favorite) {
        synchronized (this) {
            Map<String, Venue> favorites = getFavorites(context);
            if (favorites != null) {
                favorites.remove(favorite.getId());
                saveFavorites(context, favorites);
            }
        }
    }

    //adds a boolean tag for checked and unchecked venues
    public void isFavorite(Context context, String id, boolean property) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean(id, property);
        edt.apply();
    }

    //returns if a venue is bookmarked or not
    public boolean hasFavorited(Context context, String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(id, false);
    }
}
