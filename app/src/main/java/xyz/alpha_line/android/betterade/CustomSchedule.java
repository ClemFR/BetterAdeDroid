package xyz.alpha_line.android.betterade;

import xyz.alphaline.mintimetablenew.model.ScheduleDay;
import xyz.alphaline.mintimetablenew.model.ScheduleEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomSchedule {

    public static final SimpleDateFormat HEURE_MINUTE_FORMAT = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    public static final String[] CHAINE_A_NETTOYER = {
            "TD S.",
            "TD R.",
            "TD",
            "TP S.",
            "TP R.",
            "TP",
            "CM S.",
            "CM R.",
            "CM",
            "PROJET"
    };

    public static ScheduleEntity fromJSONObject(JSONObject jsonObject) throws JSONException {
        String titre = jsonObject.getString("summary");

        List<String> prof = new ArrayList<>();
        JSONArray profs = jsonObject.getJSONArray("teachers");
        for (int i = 0; i < profs.length(); i++) {
            prof.add(profs.getString(i));
        }
        if (profs.length() == 0) {
            prof.add("Aucun enseignant");
        }

        List<String> salle = new ArrayList<>();
        try {
            JSONArray salles = jsonObject.getJSONArray("location");
            for (int i = 0; i < salles.length(); i++) {
                salle.add(salles.getString(i));
            }
        } catch (JSONException e) {
            salle.add("Aucune salle");
        }

        List<String> groupes = new ArrayList<>();
        try {
            JSONArray groupesJSON = jsonObject.getJSONArray("ade_groups");
            for (int i = 0; i < groupesJSON.length(); i++) {
                groupes.add(groupesJSON.getString(i));
            }
        } catch (JSONException e) {
            groupes.add("Aucun groupe");
        }

        Calendar heureDebut = getCalendarFromISODate(jsonObject.getString("start"));
        Calendar heureFin = getCalendarFromISODate(jsonObject.getString("end"));

        String backgroundColor = generateColorFromHash(titre);

        return new ScheduleEntity(
                1,
                titre,
                salle,
                ScheduleDay.MONDAY,
                HEURE_MINUTE_FORMAT.format(heureDebut.getTime()),
                HEURE_MINUTE_FORMAT.format(heureFin.getTime()),
                backgroundColor,
                textColor(backgroundColor),
                prof,
                groupes
        );
    }


    public static ArrayList<ScheduleEntity> fromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<ScheduleEntity> listeCours = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            listeCours.add(fromJSONObject(jsonObject));
        }

        return listeCours;
    }

    private static Calendar getCalendarFromISODate(String ISODate) {
        Calendar calendar = Calendar.getInstance();
        if (ISODate.charAt(ISODate.length() - 1) != 'Z') {
            ISODate += "Z";
        }
        Instant i = Instant.parse(ISODate);
        calendar.setTimeInMillis(i.toEpochMilli());
        return calendar;
    }

    private static String generateColorFromHash(String toHash) {
        // On nettoie la chaîne de caractères
        for (String s : CHAINE_A_NETTOYER) {
            // On supprime les occurrences de la chaîne de caractères
            // case insensitive
            toHash = toHash.replaceAll("(?i)" + s, "");
        }

        // On supprime les espaces multiples
        toHash = toHash.replaceAll("\\s+", " ");

        // Hashage de la chaîne de caractères
        int hash = toHash.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        return String.format("#88%02X%02X%02X", r, g, b);
    }

    private static String textColor(String backgroundColor) {
        // Couleur du texte (noir ou blanc) en fonction de la luminance de la couleur de fond
        int r = Integer.parseInt(backgroundColor.substring(3, 5), 16);
        int g = Integer.parseInt(backgroundColor.substring(5, 7), 16);
        int b = Integer.parseInt(backgroundColor.substring(7, 9), 16);
        int luminance = (int) Math.sqrt(0.299 * r * r + 0.587 * g * g + 0.114 * b * b);
        if (luminance > 130) {
            return "#000000";
        } else {
            return "#FFFFFF";
        }
    }
}
