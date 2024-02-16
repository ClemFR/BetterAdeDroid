package xyz.alpha_line.android.betterade;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import xyz.alpha_line.android.betterade.api.BetterAdeApi;

public class ItemSelector extends AppCompatActivity {

    public static final String INTENT_TYPE_RESSOURCE = "xyz.alpha_line.android.betterade.ItemSelector.INTENT_TYPE_RESSOURCE";
    public static final String INTENT_LISTE_RESSOURCE = "xyz.alpha_line.android.betterade.ItemSelector.INTENT_LISTE_RESSOURCE";

    private static final String STORAGE_FILE_LOCATION = "listes_ressources";
    private static final String TAG = "ItemSelector";


    private Spinner spinner;
    private ListView listView;

    private List<String> liste;
    private EditText search;
    private ArrayAdapter<String> adapter;
    private String[] listeArray;
    private boolean disableSpinner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        search = findViewById(R.id.search);

        // Bind bouton valider / annuler
        findViewById(R.id.valider).setOnClickListener(this::valider);
        findViewById(R.id.annuler).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Init du spinner
        spinner = (Spinner) findViewById(R.id.spinner);

        // Init de la liste
        listView = (ListView) findViewById(R.id.list_view);
        if (savedInstanceState != null) {
            String[] listeArray = savedInstanceState.getStringArray("liste");
            int spinnerPosition = savedInstanceState.getInt("spinner");
            disableSpinner = true;
            spinner.setSelection(spinnerPosition);
            liste = new ArrayList<>();
            if (listeArray != null) {
                Arrays.asList(listeArray).forEach(liste::add);
            }
        } else {
            liste = new ArrayList<>();
        }

        // Mise en place du système d'appui pour sélectionner un item
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, listView.isItemChecked(position));
            }
        });

        // Détection si le spinner change de valeur
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search.setText("");
                Log.i("ItemSelector", "onItemSelected: " + position);
                if (disableSpinner) {
                    disableSpinner = false;
                    Log.i("ItemSelector", "onItemSelected: Spinner désactivé");
                    return;
                }

                // On essaye de charger la liste depuis le stockage
                if (loadListeRessource(position, liste)) {
                    Log.i("ItemSelector", "onItemSelected: Liste chargée depuis le stockage");
                    listeArray = liste.toArray(new String[0]);
                    initRechercheRapide();
                } else {
                    Log.i("ItemSelector", "onItemSelected: Liste récupérée depuis l'API");
                    BetterAdeApi.recupereAfficheListe(liste, listView, position, ItemSelector.this, () -> {
                        // Callback exécuté après la récuperation de la liste
                        // On essaye de sauvegarder la liste
                        if (storeListeRessource(position, liste)) {
                            Log.i("ItemSelector", "onItemSelected: Liste sauvegardée");
                        } else {
                            Log.i("ItemSelector", "onItemSelected: Impossible de sauvegarder la liste");
                        }
                        listeArray = liste.toArray(new String[0]);
                    });
                    initRechercheRapide();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "onNothingSelected: ");
            }
        });

        initRechercheRapide();
    }

    private void initRechercheRapide() {
        // Init système recherche rapide
        Log.i("ItemSelector", "initRechercheRapide: " + liste.size());
        // adapter = new FilteredArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, liste);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, liste);
        listView.setAdapter(adapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                ((ArrayAdapter<String>) listView.getAdapter()).getFilter().filter(text);
                Log.i("ItemSelector", "afterTextChanged: Taille liste " + liste.size());
            }
        });
    }




    // Méthode appelée lors de l'appui sur le bouton de validation
    public void valider(View view) {
        // On récupère les items sélectionnés
        List<String> items = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                items.add(listView.getItemAtPosition(i).toString());
            }
        }

        if (items.size() == 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // On renvoie les items sélectionnés
        String lte = "";
        for (String item : items) {
            lte += item + ",";
        }
        // On enlève la dernière virgule
        lte = lte.substring(0, lte.length() - 1);

        getIntent().putExtra(INTENT_LISTE_RESSOURCE, lte);
        getIntent().putExtra(INTENT_TYPE_RESSOURCE, spinner.getSelectedItemPosition());
        setResult(RESULT_OK, getIntent());
        finish();
    }

    public boolean storeListeRessource(int typeRessource, List<String> liste) {
        File path = new File(getFilesDir(), STORAGE_FILE_LOCATION);
        path = new File(path, typeRessource + ".ser");

        if (!path.exists()) {
            try {
                path.getParentFile().mkdirs();
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }

        // On sérialise la liste
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(liste);
            oos.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadListeRessource(int typeRessource, List<String> liste) {
        File path = new File(getFilesDir(), STORAGE_FILE_LOCATION);
        path = new File(path, typeRessource + ".ser");

        if (!path.exists()) {
            return false;
        }

        if (path.isDirectory()) {
            return false;
        }

        // On regarde la date de dernière modification
        long lastModified = path.lastModified();
        long now = System.currentTimeMillis();

        // Si le fichier a été modifié il y a plus de 24h, on le supprime
        if (now - lastModified > 24 * 60 * 60 * 1000) {
            Log.i("ItemSelector", "loadListeRessource: Fichier liste trop vieux, on le supprime");
            path.delete();
            return false;
        }

        List<String> newListe;
        Log.i("ItemSelector", "loadListeRessource: Fichier liste trouvé");
        Log.i("ItemSelector", "loadListeRessource: " + path.getAbsolutePath());

        // On désérialise la liste
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            newListe = (List<String>) ois.readObject();
            ois.close();
            fis.close();

            try {
                liste.clear();
                // liste.addAll(newListe);
                for (String s : newListe) {
                    liste.add(s);
                }
                Log.i(TAG, "loadListeRessource: Rafraichissement de la liste");
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Méthode sauvegarde dans bundle pour rotation écran
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("liste", listeArray);
        outState.putInt("spinner", spinner.getSelectedItemPosition());
        spinner.setOnItemSelectedListener(null);
    }
}
