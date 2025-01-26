package com.example.fittrack;

import android.widget.ImageView;

public class ExerciseTypeModel {
    private String exerciseType;
    private int imgExerciseType;

    public ExerciseTypeModel(String exerciseType, int imgExerciseType) {
        this.exerciseType = exerciseType;
        this.imgExerciseType = imgExerciseType;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getImgExerciseType() {
        return imgExerciseType;
    }

    public void setImgExerciseType(int imgExerciseType) {
        this.imgExerciseType = imgExerciseType;
    }
}
