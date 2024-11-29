package com.example.aptitudetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private ArrayList<String> universidades; // Declara la lista de universidades

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ListView carrerasListView = findViewById(R.id.careerListView);
        TextView noCarrerasTextView = findViewById(R.id.noCarrerasTextView);
        Button terminarButton = findViewById(R.id.terminarButton);

        // Obtener las sugerencias de carreras desde el Intent
        ArrayList<String> carrerasSugeridas = getIntent().getStringArrayListExtra("careerSuggestions");

        // Inicializa la lista de universidades
        universidades = new ArrayList<>();
        universidades.add("Universidad A");
        universidades.add("Universidad B");
        // Agrega más universidades según sea necesario

        // Comprobar si hay sugerencias
        if (carrerasSugeridas == null || carrerasSugeridas.isEmpty()) {
            noCarrerasTextView.setVisibility(View.VISIBLE);
            carrerasListView.setVisibility(View.GONE);
        } else {
            noCarrerasTextView.setVisibility(View.GONE);
            carrerasListView.setVisibility(View.VISIBLE);

            // Crear adaptador y setearlo al ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carrerasSugeridas);
            carrerasListView.setAdapter(adapter);

            // Listener para cuando se haga clic en una carrera
            carrerasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String carreraSeleccionada = carrerasSugeridas.get(position);
                    // Aquí asegúrate de que el nombre de la actividad sea correcto
                    Intent intent = new Intent(ResultActivity.this, UniversidadesActivity.class);
                    intent.putExtra("carreraSeleccionada", carreraSeleccionada);
                    startActivity(intent);
                }
            });

        }

        // Configurar el botón "TERMINAR" para regresar al inicio
        terminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar de regreso a MainActivity
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Cierra la ResultActivity para no regresar a esta cuando se presione el botón de atrás
            }
        });
    }
}
