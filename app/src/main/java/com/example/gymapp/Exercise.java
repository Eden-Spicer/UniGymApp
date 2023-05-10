package com.example.gymapp;

public class Exercise {
    private String name;        // the name of the exercise
    private float weight;       // the weight used for the exercise
    private int repetitions;    // the number of repetitions performed for the exercise

    public Exercise(String name, float weight, int repetitions) {
        this.name = name;
        this.weight = weight;
        this.repetitions = repetitions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }
}
