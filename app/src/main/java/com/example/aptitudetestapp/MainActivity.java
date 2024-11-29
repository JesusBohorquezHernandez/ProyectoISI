package com.example.aptitudetestapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://09dc-190-253-17-112.ngrok-free.app/aptitudetest/";
    private Spinner spinnerCiudades;
    private ListView listViewUniversidades;
    private ListView listViewProgramas;
    private ProgressBar progressBar;

    private void realizarTest() {
        Intent intent = new Intent(this, TestActivity.class); // Reemplazar con tu actividad de test
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        spinnerCiudades = findViewById(R.id.spinner_ciudades);
        listViewUniversidades = findViewById(R.id.listViewUniversidades);
        listViewProgramas = findViewById(R.id.listViewProgramas);
        progressBar = findViewById(R.id.progressBar);

        Button buttonTestAptitud = findViewById(R.id.buttonTestAptitud);
        buttonTestAptitud.setOnClickListener(v -> realizarTest());

        configurarSpinner();
        manejarIntent();
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapterCiudades = ArrayAdapter.createFromResource(this,
                R.array.ciudades_array, android.R.layout.simple_spinner_item);
        adapterCiudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudades.setAdapter(adapterCiudades);

        spinnerCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String ciudadSeleccionada = spinnerCiudades.getSelectedItem().toString();
                listViewProgramas.setAdapter(null);
                obtenerUniversidades(ciudadSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        listViewUniversidades.setOnItemClickListener((parent, view, position, id) -> {
            String universidadSeleccionada = (String) parent.getItemAtPosition(position);
            obtenerProgramasAcademicos(universidadSeleccionada);
        });
    }

    private void manejarIntent() {
        Intent intent = getIntent();
        String selectedOption = intent.getStringExtra("selectedOption");
        if (selectedOption != null && !selectedOption.isEmpty()) {
            obtenerProgramasAcademicosPorOpcion(selectedOption);
        }
    }

    private void obtenerUniversidades(String ciudad) {
        ejecutarTarea(BASE_URL + "get_universidades.php?ciudad=" + ciudad, this::procesarUniversidades);
    }

    private void obtenerProgramasAcademicos(String universidad) {
        ejecutarTarea(BASE_URL + "get_programas.php?universidad=" + universidad, this::procesarProgramas);
    }

    private void obtenerProgramasAcademicosPorOpcion(String selectedOption) {
        String universidad = asignarUniversidadPorOpcion(selectedOption);
        if (!universidad.isEmpty()) {
            obtenerProgramasAcademicos(universidad);
        } else {
            Toast.makeText(this, "Opción no válida", Toast.LENGTH_SHORT).show();
        }
    }

    private String asignarUniversidadPorOpcion(String option) {
        switch (option) {
            case "A": return "Universidad A";
            case "B": return "Universidad B";
            case "C": return "Universidad C";
            case "D": return "Universidad D";
            default: return "";
        }
    }

    private void ejecutarTarea(String url, ResultadoCallback callback) {
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                StringBuilder result = new StringBuilder();
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                    conn.setRequestMethod("GET");

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    Log.e("AsyncTask", "Error: " + e.getMessage());
                    return null;
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                progressBar.setVisibility(View.GONE);
                if (result != null) {
                    callback.onResultado(result);
                } else {
                    Toast.makeText(MainActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(url);
    }

    private void procesarUniversidades(String json) {
        ArrayList<String> universidadesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject universidad = jsonArray.getJSONObject(i);
                universidadesList.add(universidad.getString("nombre"));
            }
            listViewUniversidades.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, universidadesList));
        } catch (JSONException e) {
            Toast.makeText(this, "Error procesando universidades", Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarProgramas(String json) {
        ArrayList<String> programasList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject programa = jsonArray.getJSONObject(i);
                programasList.add(programa.getString("nombre"));
            }
            listViewProgramas.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programasList));
        } catch (JSONException e) {
            Toast.makeText(this, "Error procesando programas", Toast.LENGTH_SHORT).show();
        }
    }

    private interface ResultadoCallback {
        void onResultado(String json);
    }
}
