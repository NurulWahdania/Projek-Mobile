package com.example.projek.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AnalyzedInstruction implements Serializable {
    @SerializedName("name")
    private String name; // Name of the instruction set (can be empty)
    @SerializedName("steps")
    private List<Step> steps;

    // Constructor, Getters, and Setters
    public AnalyzedInstruction(String name, List<Step> steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}