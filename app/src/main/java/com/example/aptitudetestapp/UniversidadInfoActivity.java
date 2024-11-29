package com.example.aptitudetestapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UniversidadInfoActivity extends AppCompatActivity {

    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universidad_info);

        infoTextView = findViewById(R.id.universidadInfoTextView);
        String universidadId = getIntent().getStringExtra("universidadId");

        Log.d("UniversidadInfoActivity", "Recibido universidadId: " + universidadId); // Verificar ID recibido

        if (universidadId != null && !universidadId.isEmpty()) {
            // Llamar al método para obtener la información de la universidad
            getUniversidadInfo(universidadId);
        } else {
            infoTextView.setText(R.string.universidad_id_invalid);
        }
    }

    private void getUniversidadInfo(final String universidadId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                StringBuilder result = new StringBuilder();
                try {
                    String urlString = "https://09dc-190-253-17-112.ngrok-free.app/aptitudetest/get_universidad_id.php?id=" + universidadId;
                    URL url = new URL(urlString);
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
                    } else {
                        Log.e("UniversidadInfoActivity", "Error en la respuesta: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("UniversidadInfoActivity", "Error al obtener información de la universidad: " + e.getMessage());
                }
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("UniversidadInfoActivity", "Respuesta de la API: " + result); // Log de la respuesta completa

                if (result == null || result.isEmpty()) {
                    infoTextView.setText(R.string.universidad_info_not_found);
                    return;
                }

                try {
                    // Intenta primero con JSONArray
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0) {
                        JSONObject universidad = jsonArray.getJSONObject(0);
                        String info = "Nombre: " + universidad.optString("nombre", getString(R.string.unavailable)) + "\n" +
                                "Dirección: " + universidad.optString("direccion", getString(R.string.unavailable)) + "\n" +
                                "Tipo: " + universidad.optString("tipo", getString(R.string.unavailable)) + "\n" +
                                "Sitio Web: " + universidad.optString("sitio_web", getString(R.string.unavailable));
                        infoTextView.setText(info);
                    } else {
                        infoTextView.setText(R.string.universidad_info_not_found);
                    }
                } catch (Exception e) {
                    Log.e("UniversidadInfoActivity", "Error al procesar los datos de la universidad: " + e.getMessage());
                    try {
                        // Intenta con JSONObject si no es un array
                        JSONObject universidad = new JSONObject(result);
                        String info = "Nombre: " + universidad.optString("nombre", getString(R.string.unavailable)) + "\n" +
                                "Dirección: " + universidad.optString("direccion", getString(R.string.unavailable)) + "\n" +
                                "Tipo: " + universidad.optString("tipo", getString(R.string.unavailable)) + "\n" +
                                "Sitio Web: " + universidad.optString("sitio_web", getString(R.string.unavailable));
                        infoTextView.setText(info);
                    } catch (Exception ex) {
                        Log.e("UniversidadInfoActivity", "Error al procesar los datos de la universidad: " + ex.getMessage());
                        infoTextView.setText(R.string.error_processing_info);
                    }
                }
            }
        }.execute();
    }
}
