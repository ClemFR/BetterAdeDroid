package xyz.alpha_line.android.betterade.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import xyz.alpha_line.android.betterade.util.InstallNotifier;
import xyz.alphaline.mintimetablenew.MinTimeTableView;
import xyz.alphaline.mintimetablenew.model.ScheduleEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import xyz.alpha_line.android.betterade.CustomSchedule;

public class BetterAdeApi {

    public static String API_BASE_URL = "https://api.ade.alpha-line.xyz/";
    // public static String API_BASE_URL = "http://localhost:5000/";
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);

    public static void recupereAfficheCoursDay(String idPromo, Date date, ArrayList<ScheduleEntity> coursInfosList, MinTimeTableView timetable, int typeRecherche, Activity a) {
        String url_path = API_BASE_URL;
        if (typeRecherche == 0) {
            url_path += "day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else if (typeRecherche == 1) {
            url_path += "teacher/day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else if (typeRecherche == 2) {
            url_path += "room/day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else {
            Toast.makeText(a, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i("BetterAdeApi", "recupereAfficheCoursDay: " + url_path);

        RequestHelper.simpleJSONArrayRequest(
                url_path,
                getHeaders(a),
                null,
                Request.Method.GET,
                response -> {
                    afficheListeCours(response, coursInfosList, timetable, a);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(a, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
                },
                a
        );
    }

    public static void afficheListeCours(JSONArray arr, ArrayList<ScheduleEntity> liste, MinTimeTableView timetable, Activity a) {
        ArrayList<ScheduleEntity> new_liste = new ArrayList<>();

        try {
            new_liste = CustomSchedule.fromJSONArray(arr);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(a, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
            System.out.println(arr.toString());
        }

        liste.clear();
        liste.addAll(new_liste);

        try {
            a.runOnUiThread(() -> {
                timetable.updateSchedules(liste);
                if (liste.size() == 0) {
                    Toast.makeText(a, "Aucun cours, YAY ! ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(a, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
        }
    }


    //---------------------------------------------------------------------------------------//
    //------------------------- GESTION LISTE POUR RECHERCHE RAPIDE -------------------------//
    //---------------------------------------------------------------------------------------//

    public static void recupereAfficheListe(List<String> lte, ListView lv, int typeListe, Activity a, Runnable callback) {
        String url_path = API_BASE_URL;
        switch (typeListe) {
            case 0:
                url_path += "promos";
                break;
            case 1:
                url_path += "teachers";
                break;
            case 2:
                url_path += "rooms";
                break;
            default:
                Toast.makeText(a, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
                return;
        }

        Log.i("BetterAdeApi", "recupereAfficheListe: " + url_path);

        RequestHelper.simpleJSONArrayRequest(
                url_path,
                getHeaders(a),
                null,
                Request.Method.GET,
                response -> {
                        afficheListe(response, lte, lv, a, callback);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(a, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
                },
                a
        );
    }

    private static void afficheListe(JSONArray arr, List<String> liste, ListView lv, Activity a, Runnable callback) {

        List<String> new_liste = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                new_liste.add(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(a, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
            System.out.println(arr.toString());
        }

        // Tri de la liste dans l'ordre alphabétique
        new_liste.sort(String::compareToIgnoreCase);

        liste.clear();
        liste.addAll(new_liste);

        try {
            a.runOnUiThread(() -> {
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(a, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
        }

        callback.run();

    }

    public static void notifierNouvelleInstall(String id, Consumer<JSONObject> processResponse, Consumer<VolleyError> processError, Context c) {
        // On récupère l'id de l'appareil
        RequestHelper.simpleJSONObjectRequest(
                API_BASE_URL + "metrics/install/" + id,
                null,
                null,
                Request.Method.GET,
                processResponse::accept,
                processError::accept,
                c
        );
    }

    private static Map<String, String> getHeaders(Context c) {
        Map<String, String> headers = new HashMap<>();

        // On ajoute l'id de l'appareil
        SharedPreferences prefs = c.getSharedPreferences(InstallNotifier.PREFS_NAME, Context.MODE_PRIVATE);
        String id = prefs.getString("id", null);
        if (id != null) {
            headers.put("X-Device", id);
        }

        return headers;
    }
}

