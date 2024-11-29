package com.example.aptitudetestapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class UniversidadesActivity extends AppCompatActivity {

    private ListView listViewUniversidades;
    private ProgressBar progressBar;
    private ArrayList<Universidad> universidadesList; // Lista de universidades

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universidades);

        listViewUniversidades = findViewById(R.id.listViewUniversidades);
        progressBar = findViewById(R.id.progressBar);

        // Obtener la carrera seleccionada desde el Intent
        String carreraSeleccionada = getIntent().getStringExtra("carreraSeleccionada");

        if (carreraSeleccionada != null && !carreraSeleccionada.isEmpty()) {
            // Llamar al método que obtiene las universidades relacionadas con la carrera seleccionada
            obtenerUniversidadesPorCarrera(carreraSeleccionada);
        } else {
            Toast.makeText(this, R.string.no_carrera_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerUniversidadesPorCarrera(String carrera) {
        progressBar.setVisibility(View.VISIBLE);
        // Asegúrate de que la URL sea correcta
        new FetchUniversidadesTask().execute("https://09dc-190-253-17-112.ngrok-free.app/aptitudetest/get_universidades_por_carrera.php?carrera=" + carrera);
    }

    private class FetchUniversidadesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    Log.d("FetchUniversidadesTask", "Contenido de la respuesta: " + result.toString()); // Añadir log
                } else {
                    Log.e("FetchUniversidadesTask", "Error en la respuesta HTTP: " + responseCode);
                    return null;
                }

            } catch (Exception e) {
                Log.e("FetchUniversidadesTask", "Error en conexión: " + e.getMessage());
                return null;
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Log.d("FetchUniversidadesTask", "Respuesta de la API: " + result);

            if (result == null || result.isEmpty()) {
                Toast.makeText(UniversidadesActivity.this, R.string.error_obtaining_universities, Toast.LENGTH_SHORT).show();
                return;
            }

            universidadesList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject universidad = jsonArray.getJSONObject(i);
                    String universidadNombre = universidad.optString("nombre", getString(R.string.name_not_available));
                    String universidadId = universidad.optString("id", "0"); // usa "0" si no tiene id
                    universidadesList.add(new Universidad(universidadId, universidadNombre));
                    Log.d("UniversidadesActivity", "Universidad añadida: " + universidadNombre + ", ID: " + universidadId);
                }

                ArrayAdapter<Universidad> adapter = new ArrayAdapter<>(UniversidadesActivity.this, android.R.layout.simple_list_item_1, universidadesList);
                listViewUniversidades.setAdapter(adapter);

                listViewUniversidades.setOnItemClickListener((parent, view, position, id) -> {
                    if (universidadesList != null && position < universidadesList.size()) {
                        Universidad selectedUniversidad = universidadesList.get(position);
                        String universidadId = selectedUniversidad.getId();
                        Intent intent = new Intent(UniversidadesActivity.this, UniversidadInfoActivity.class);
                        intent.putExtra("universidadId", universidadId);
                        Log.d("UniversidadesActivity", "Enviando universidadId: " + universidadId); // Log adicional
                        startActivity(intent);
                    } else {
                        Toast.makeText(UniversidadesActivity.this, R.string.university_info_not_found, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (JSONException e) {
                Log.e("FetchUniversidadesTask", "Error al parsear el JSON: " + e.getMessage());
                Toast.makeText(UniversidadesActivity.this, R.string.error_processing_info, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
