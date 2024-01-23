package xyz.alpha_line.android.betterade.recyclerview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CoursInfos {

    public String titre;
    public String prof;
    public String salle;
    public Calendar heureDebut;
    public Calendar heureFin;

    public CoursInfos(String titre, String prof, String salle, Calendar heureDebut, Calendar heureFin) {
        this.titre = titre;
        this.prof = prof;
        this.salle = salle;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public static List<CoursInfos> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<CoursInfos> coursInfos = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            coursInfos.add(fromJSONObject(jsonObject));
        }

        return coursInfos;
    }

    public static CoursInfos fromJSONObject(JSONObject jsonObject) throws JSONException {
        String titre = jsonObject.getString("summary");

        String prof = "";
        JSONArray profs = jsonObject.getJSONArray("teachers");
        for (int i = 0; i < profs.length(); i++) {
            prof += profs.getString(i);
            if (i < profs.length() - 1) {
                prof += ", ";
            }
        }
        if (profs.length() == 0) {
            prof = "Aucun enseignant";
        }

        String salle = "";
        try {
            JSONArray salles = jsonObject.getJSONArray("location");
            for (int i = 0; i < salles.length(); i++) {
                salle += salles.getString(i);
                if (i < salles.length() - 1) {
                    salle += ", ";
                }
            }
        } catch (JSONException e) {
            salle = "Aucune salle";
        }


        Calendar heureDebut = getCalendarFromISODate(jsonObject.getString("start"));
        Calendar heureFin = getCalendarFromISODate(jsonObject.getString("end"));

        return new CoursInfos(titre, prof, salle, heureDebut, heureFin);
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
}
