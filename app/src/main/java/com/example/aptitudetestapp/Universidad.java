package com.example.aptitudetestapp;

public class Universidad {
    private String id;
    private String nombre;

    public Universidad(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Esto asegura que el ListView muestre el nombre de la universidad
    }
}



