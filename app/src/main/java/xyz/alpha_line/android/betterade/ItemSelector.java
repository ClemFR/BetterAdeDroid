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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xyz.alpha_line.android.betterade.api.BetterAdeApi;
import xyz.alpha_line.android.betterade.util.FilteredArrayAdapter;

public class ItemSelector extends AppCompatActivity {

    public static final String INTENT_TYPE_RESSOURCE = "xyz.alpha_line.android.betterade.ItemSelector.INTENT_TYPE_RESSOURCE";
    public static final String INTENT_LISTE_RESSOURCE = "xyz.alpha_line.android.betterade.ItemSelector.INTENT_LISTE_RESSOURCE";


    private Spinner spinner;
    private ListView listView;

    private List<String> liste;
    private EditText search;

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


        // Init de la liste
        liste = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_view);

        // Mise en place du système d'appui pour sélectionner un item
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, listView.isItemChecked(position));
            }
        });

        // Init du spinner
        spinner = (Spinner) findViewById(R.id.spinner);

        // Détection si le spinner change de valeur
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search.setText("");
                BetterAdeApi.recupereAfficheListe(liste, listView, position);
                initRechercheRapide();
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
        listView.setAdapter(new FilteredArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, liste));
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
}
