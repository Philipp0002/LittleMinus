package de.hahnphilipp.littleminus.location;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hahnphilipp.littleminus.shared.Constants;
import de.hahnphilipp.littleminus.shared.Preferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StoresService {

    public static void setCountryId(String countryId) {
        Preferences.putString(Constants.PREF_COUNTRY_ID, countryId);
    }

    public static String getCountryId() {
        return Preferences.getString(Constants.PREF_COUNTRY_ID, null);
    }

    public static void requestCountries(RequestCountriesCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(Constants.BASE_APPGATEWAY_API + Constants.ENDPOINT_COUNTRIES_LIST)
                .addHeader("User-Agent", Constants.USER_AGENT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<Country> countries;
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    countries = objectMapper.readValue(response.body().string(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Country.class));
                } catch (Throwable e) {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                    return;
                }
                callback.onSuccess(countries);
            }
        });
    }

    public static void requestStores(RequestStoresCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(Constants.BASE_STORES_API + String.format(Constants.ENDPOINT_STORES_LIST, StoresService.getCountryId()))
                .addHeader("User-Agent", Constants.USER_AGENT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<Store> stores = new ArrayList<>();
                try {
                    String json = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode node = objectMapper.readTree(json);
                    for(JsonNode storeNode : node) {
                        Store store = new Store();
                        store.storeKey = storeNode.get("storeKey").asText();
                        store.name = storeNode.get("name").asText();
                        store.address = storeNode.get("address").asText();
                        store.postalCode = storeNode.get("postalCode").asText();
                        store.locality = storeNode.get("locality").asText();
                        store.latitude = storeNode.get("location").get("latitude").asDouble();
                        store.longitude = storeNode.get("location").get("longitude").asDouble();
                        stores.add(store);
                    }
                } catch (Throwable e) {
                    callback.onFailure(e.getMessage());
                    return;
                }
                callback.onSuccess(stores);
            }
        });
    }

    public interface RequestCountriesCallback {
        void onSuccess(List<Country> countryList);
        void onFailure(String error);
    }

    public interface RequestStoresCallback {
        void onSuccess(List<Store> storeList);
        void onFailure(String error);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Country {
        public String id;
        public String defaultName;
        public String enDefaultName;
    }

    public static class Store {
        public String storeKey;
        public String name;
        public String address;
        public String postalCode;
        public String locality;
        public double latitude;
        public double longitude;

    }
}
