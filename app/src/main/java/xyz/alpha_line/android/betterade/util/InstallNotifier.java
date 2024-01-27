package xyz.alpha_line.android.betterade.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import xyz.alpha_line.android.betterade.api.BetterAdeApi;


public class InstallNotifier {

    public static final String PREFS_NAME = "xyz.alpha_line.android.betterade.util.InstallNotifier";

    public static void detectNewInstall(Context c) {

        // On ouvre les préférences
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // On regarde si l'id de l'appareil est déjà enregistré
        String id = prefs.getString("id", null);
        if (id != null) {
            return;
        }

        // On récupère l'id de l'appareil
        id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

        String finalId = id;
        BetterAdeApi.notifierNouvelleInstall(
                id,
                response -> {
                    // On enregistre l'id de l'appareil
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", finalId);
                    editor.apply();
                },
                error -> {
                    Toast.makeText(c, "Erreur device id", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                },
                c
        );
    }
}
