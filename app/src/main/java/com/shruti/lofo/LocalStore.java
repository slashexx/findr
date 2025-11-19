package com.shruti.lofo;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shruti.lofo.ui.Found.FoundItems;
import com.shruti.lofo.ui.Lost.LostItems;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalStore {

    private static final String PREF_NAME = "LocalStorePrefs";
    private static final String KEY_FOUND_ITEMS = "FoundItems";
    private static final String KEY_LOST_ITEMS = "LostItems";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public LocalStore(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveFoundItem(FoundItems item) {
        List<FoundItems> items = getFoundItems();
        items.add(item);
        saveFoundItemsList(items);
    }

    public List<FoundItems> getFoundItems() {
        String json = sharedPreferences.getString(KEY_FOUND_ITEMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<FoundItems>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveFoundItemsList(List<FoundItems> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(KEY_FOUND_ITEMS, json).apply();
    }

    public void saveLostItem(LostItems item) {
        List<LostItems> items = getLostItems();
        items.add(item);
        saveLostItemsList(items);
    }

    public List<LostItems> getLostItems() {
        String json = sharedPreferences.getString(KEY_LOST_ITEMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<LostItems>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveLostItemsList(List<LostItems> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(KEY_LOST_ITEMS, json).apply();
    }
}
