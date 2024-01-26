package xyz.alpha_line.android.betterade.api;

import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.islandparadise14.mintable.MinTimeTableView;
import com.islandparadise14.mintable.model.ScheduleEntity;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.alpha_line.android.betterade.CustomSchedule;
import xyz.alpha_line.android.betterade.MainActivity;
import xyz.alpha_line.android.betterade.recyclerview.CoursInfos;

public class BetterAdeApi {

    public static String API_BASE_URL = "https://api.ade.alpha-line.xyz/";
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);

    public static void recupereAfficheCoursDay(String idPromo, Date date, ArrayList<ScheduleEntity> coursInfosList, MinTimeTableView timetable, int typeRecherche) {
        String url_path = API_BASE_URL;
        if (typeRecherche == 0) {
            url_path += "day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else if (typeRecherche == 1) {
            url_path += "teacher/day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else if (typeRecherche == 2) {
            url_path += "room/day/" + idPromo + "/" + DATE_FORMAT.format(date);
        } else {
            Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i("BetterAdeApi", "recupereAfficheCoursDay: " + url_path);

        RequestHelper.simpleJSONArrayRequest(
                url_path,
                null,
                null,
                Request.Method.GET,
                response -> {
                    afficheListeCours(response, coursInfosList, timetable);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
                }
        );
    }

    public static void afficheListeCours(JSONArray arr, ArrayList<ScheduleEntity> liste, MinTimeTableView timetable) {
        ArrayList<ScheduleEntity> new_liste = new ArrayList<>();

        try {
            new_liste = CustomSchedule.fromJSONArray(arr);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
            System.out.println(arr.toString());
        }

        liste.clear();
        liste.addAll(new_liste);

        try {
            MainActivity.instance.runOnUiThread(() -> {
                timetable.updateSchedules(liste);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des informations", Toast.LENGTH_LONG).show();
        }
    }


    //---------------------------------------------------------------------------------------//
    //------------------------- GESTION LISTE POUR RECHERCHE RAPIDE -------------------------//
    //---------------------------------------------------------------------------------------//

    public static void recupereAfficheListe(List<String> lte, ListView lv, int typeListe) {
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
                Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
                return;
        }

        Log.i("BetterAdeApi", "recupereAfficheListe: " + url_path);

        RequestHelper.simpleJSONArrayRequest(
                url_path,
                null,
                null,
                Request.Method.GET,
                response -> {
                        afficheListe(response, lte, lv);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
                }
        );
    }

    private static void afficheListe(JSONArray arr, List<String> liste, ListView lv) {

        List<String> new_liste = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                new_liste.add(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
            System.out.println(arr.toString());
        }

        // Tri de la liste dans l'ordre alphabétique
        new_liste.sort(String::compareToIgnoreCase);

        liste.clear();
        liste.addAll(new_liste);

        try {
            MainActivity.instance.runOnUiThread(() -> {
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.instance, "Erreur lors de la récupération des promos", Toast.LENGTH_LONG).show();
        }

    }

}

