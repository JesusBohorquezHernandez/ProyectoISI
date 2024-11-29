package com.example.aptitudetestapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup radioGroupAnswers;
    private Button buttonNext;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        questionTextView = findViewById(R.id.questionTextView);
        radioGroupAnswers = findViewById(R.id.radioGroupAnswers);
        buttonNext = findViewById(R.id.buttonNext);

        questions = loadQuestions();
        answers = new int[questions.size()];

        loadQuestion();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    loadQuestion();
                } else {
                    // Procesar las respuestas y mostrar los resultados
                    processResults();
                }
            }
        });
    }

    private void getCareerSuggestionAsync(final String selectedArea) {
        Log.d("TestActivity", "Área seleccionada para sugerencias: " + selectedArea);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String encodedArea = null;
                try {
                    encodedArea = URLEncoder.encode(selectedArea, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("TestActivity", "Error al codificar el área: " + e.getMessage());
                    return "Error al codificar el área.";
                }

                String urlString = "https://09dc-190-253-17-112.ngrok-free.app/aptitudetest/get_career_suggestion.php?area=" + encodedArea;
                Log.d("TestActivity", "URL de consulta: " + urlString);

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    Log.d("TestActivity", "Código de respuesta: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();

                        JSONArray jsonArray = new JSONArray(result.toString());
                        if (jsonArray.length() > 0) {
                            StringBuilder careerSuggestions = new StringBuilder();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                careerSuggestions.append(jsonArray.getString(i)).append("\n");
                            }
                            return careerSuggestions.toString();
                        } else {
                            return "Sin sugerencias disponibles.";
                        }
                    } else {
                        Log.e("TestActivity", "Error en la respuesta del servidor: " + responseCode);
                        return "Error en la respuesta del servidor.";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TestActivity", "Error al obtener sugerencias: " + e.getMessage());
                    return "Error al obtener sugerencias.";
                }
            }

            @Override
            protected void onPostExecute(String careerSuggestion) {
                Log.d("TestActivity", "Sugerencia recibida: " + careerSuggestion);

                // Convertir la sugerencia de carrera en un ArrayList
                ArrayList<String> careerSuggestionsList = new ArrayList<>();
                if (careerSuggestion != null) {
                    String[] suggestionsArray = careerSuggestion.split("\n");
                    for (String suggestion : suggestionsArray) {
                        careerSuggestionsList.add(suggestion.trim());  // Agregar cada sugerencia a la lista
                    }
                }

                // Enviar el resultado a ResultActivity
                Intent intent = new Intent(TestActivity.this, ResultActivity.class);
                intent.putStringArrayListExtra("careerSuggestions", careerSuggestionsList);  // Cambiar aquí
                startActivity(intent);
            }

        }.execute();
    }


    private void loadQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestion());
        radioGroupAnswers.removeAllViews();

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioGroupAnswers.addView(radioButton);
        }
    }

    private void saveAnswer() {
        int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            int answerIndex = radioGroupAnswers.indexOfChild(selectedRadioButton);
            answers[currentQuestionIndex] = answerIndex;  // Guardar el índice de respuesta
        }
    }

    private List<Question> loadQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("¿Qué actividades disfrutas más en tu tiempo libre?", new String[]{
                "A. Leer libros o investigar sobre temas que me interesan.",
                "B. Hacer actividades al aire libre, como deportes o caminatas.",
                "C. Crear cosas, como arte, música o proyectos de bricolaje.",
                "D. Ayudar a otros, ya sea escuchándolos o dándoles apoyo."
        }));
        questionList.add(new Question("¿Cómo prefieres trabajar?", new String[]{
                "A. Individualmente, a mi propio ritmo.",
                "B. En equipo, colaborando con otros.",
                "C. En un ambiente creativo y flexible.",
                "D. Resolviendo problemas prácticos."
        }));
        questionList.add(new Question("¿Qué materias disfrutas más en la escuela?", new String[]{
                "A. Ciencias sociales o literatura.",
                "B. Matemáticas o ciencias naturales.",
                "C. Arte o educación física.",
                "D. Educación cívica o psicología."
        }));
        questionList.add(new Question("¿Qué tipo de entorno laboral prefieres?", new String[]{
                "A. Una oficina con un horario estructurado.",
                "B. Un entorno dinámico donde no hay un día igual.",
                "C. Un estudio creativo o un espacio al aire libre.",
                "D. Un lugar donde pueda interactuar con personas y ayudarles."
        }));
        questionList.add(new Question("¿Cuál es tu principal motivación para elegir una carrera?", new String[]{
                "A. Contribuir al conocimiento y la cultura.",
                "B. Tener un impacto directo en la sociedad.",
                "C. Expresar mi creatividad y talento personal.",
                "D. Ayudar a otros a resolver problemas."
        }));

        return questionList;
    }

    private void processResults() {
        int[] counts = new int[4]; // A, B, C, D
        for (int answer : answers) {
            counts[answer]++;
        }

        int maxIndex = 0;
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > counts[maxIndex]) {
                maxIndex = i;
            }
        }

        String selectedArea = "";
        switch (maxIndex) {
            case 0:
                selectedArea = "Comunicación Social y Periodismo,Derecho,Finanzas y Contabilidad,Administración,Educación y Pedagogía";
                break;
            case 1:
                selectedArea = "Ciencias Exactas,Ingeniería y Tecnología,Estadística,Economía";
                break;
            case 2:
                selectedArea = "Arte y Diseño,Arquitectura,Hotelería y Turismo";
                break;
            case 3:
                selectedArea = "Salud y Medicina,Psicología y Ciencias Sociales,Derecho";
                break;
            default:
                selectedArea = "";
                break;
        }

        // Llama a la nueva función asíncrona
        getCareerSuggestionAsync(selectedArea);
    }

    // Clase interna para representar una pregunta
    private static class Question {
        private String question;
        private String[] options;

        public Question(String question, String[] options) {
            this.question = question;
            this.options = options;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getOptions() {
            return options;
        }
    }
}
