package homeaway.com.foodfinder.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import homeaway.com.foodfinder.model.venueModel.Venue;

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
        String jsonFavoritess = gson.toJson(favorites);
        editor.putString(Config.FAVORITES_MAP,jsonFavoritess);
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

}
