package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDatabaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public FoodDatabaseUtil(FirebaseFirestore db) {
        this.db = db;
    }

    public void getTotalCalories(String todayDate, DayCaloriesCallback callback) {
        CollectionReference collectionRef = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(todayDate)
                .collection("Meals");

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                double totalCalories = 0.00;
                for(DocumentSnapshot snapshot : querySnapshot) {
                    double calories = (double) snapshot.get("calories");
                    totalCalories += calories;
                }
                callback.onCallback(totalCalories);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    public void getTodayNutrient(String todayDate, NutrientCallback callback) {
        CollectionReference collectionRef = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(todayDate)
                .collection("Meals");

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                double fatAmount = 0.00;
                double saturatedFatAmount = 0.00;
                double proteinAmount = 0.00;
                double sodiumAmount = 0.00;
                double potassiumAmount = 0.00;
                double carbsAmount = 0.00;
                double fiberAmount = 0.00;
                double sugarAmount = 0.00;
                for(DocumentSnapshot snapshot : querySnapshot) {
                    double fat = snapshot.getDouble("fat") != null ? snapshot.getDouble("fat") : 0.00;
                    double saturated_fat = snapshot.getDouble("saturated_fat") != null ? snapshot.getDouble("saturated_fat") : 0.00;
                    double protein = snapshot.getDouble("protein") != null ? snapshot.getDouble("protein") : 0.00;
                    double sodium = snapshot.getDouble("sodium") != null ? snapshot.getDouble("sodium") : 0.00;
                    double potassium = snapshot.getDouble("potassium") != null ? snapshot.getDouble("potassium") : 0.00;
                    double carbs = snapshot.getDouble("carbs") != null ? snapshot.getDouble("carbs") : 0.00;
                    double fiber = snapshot.getDouble("fiber") != null ? snapshot.getDouble("fiber") : 0.00;
                    double sugar = snapshot.getDouble("sugar") != null ? snapshot.getDouble("sugar") : 0.00;
                    fatAmount += fat;
                    saturatedFatAmount += saturated_fat;
                    proteinAmount += protein;
                    sodiumAmount += sodium;
                    potassiumAmount += potassium;
                    carbsAmount += carbs;
                    fiberAmount += fiber;
                    sugarAmount += sugar;
                }
                callback.onCallback(fatAmount, saturatedFatAmount, proteinAmount, sodiumAmount, potassiumAmount, carbsAmount, fiberAmount, sugarAmount);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void getCaloriesForMealType(String todayDate, CaloriesMealCallback callback) {
        HashMap<String, Double> calorieMealMap = new HashMap<>();
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks"};
        int[] completedQueries = {0};

        for (String meal : mealTypes) {
            Query query = db.collection("Users")
                    .document(UserID)
                    .collection("Nutrition")
                    .document(todayDate)
                    .collection("Meals")
                    .whereEqualTo("mealType", meal);

            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    double calorieMeal = 0.00;
                    for (DocumentSnapshot snapshot : querySnapshot) {
                        double data = (double) snapshot.get("calories");
                        calorieMeal += data;
                    }
                    calorieMealMap.put(meal, calorieMeal);
                    synchronized (completedQueries) {
                        completedQueries[0]++;
                        if (completedQueries[0] == mealTypes.length) {
                            callback.onCallback(calorieMealMap);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Failed to fetch calories for meal: " + meal);
                    synchronized (completedQueries) {
                        completedQueries[0]++;
                        if (completedQueries[0] == mealTypes.length) {
                            callback.onCallback(calorieMealMap);
                        }
                    }
                }
            });
        }
    }

    public void retrieveCaloriesBurnedToday(String todayDate, DayCaloriesBurnedCallback callback) {
        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                        .whereEqualTo("shortDate", todayDate);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                int totalCaloriesBurned = 0;
                List<DocumentSnapshot> snapshots = querySnapshot.getDocuments();
                for(DocumentSnapshot document : snapshots) {
                    long caloriesBurned = (long) document.get("caloriesBurned");
                    totalCaloriesBurned += caloriesBurned;
                }
                callback.onCallback(totalCaloriesBurned);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    public void saveFood(FoodModel food) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        String date = dateFormat.format(today);

        double servingMultiplier = food.getServingSize() / 100.0;

        Map<String, Object> FoodMap = new HashMap<>();
        FoodMap.put("foodName", food.getFoodName());
        FoodMap.put("calories", ((food.getCalories())));
        FoodMap.put("servingSize", food.getServingSize());
        FoodMap.put("servingQuantity", food.getServingQuantity());
        FoodMap.put("mealType", food.getMealType());
        FoodMap.put("fat", food.getFat() * servingMultiplier);
        FoodMap.put("saturated_fat", food.getSaturated_fat() * servingMultiplier);
        FoodMap.put("protein", food.getProtein() * servingMultiplier);
        FoodMap.put("sodium", food.getSodium() * servingMultiplier);
        FoodMap.put("potassium", food.getPotassium() * servingMultiplier);
        FoodMap.put("carbs", food.getCarbs() * servingMultiplier);
        FoodMap.put("fiber", food.getFiber() * servingMultiplier);
        FoodMap.put("sugar", food.getSugar() * servingMultiplier);


        CollectionReference collectionRef = db.collection("Users")
                .document(UserID)
                .collection("Nutrition")
                .document(date)
                .collection("Meals");

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()) {
//                        updateDoc(docRef, FoodMap);
//                    } else {
//                        createDoc(docRef, FoodMap);
//                    }
//                } else {
//                    System.out.println("No document got: " + task.getException());
//                }
//            }
//        });

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                collectionRef.add(FoodMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("Added new food to the daily caloric intake");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Food couldn't be added");
                    }
                });
            }
        });

    }
//    private void updateDoc(CollectionReference docRef, Map<String, Object> FoodMap) {
//        docRef.update("food", FieldValue.arrayUnion(FoodMap))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        System.out.println("food saved");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("food not saved" + e);
//                    }
//                });
//    }

//    private void createDoc(CollectionReference docRef, Map<String, Object> FoodMap) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("food", FieldValue.arrayUnion(FoodMap));
//
//        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                System.out.println("food saved");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("food not saved");
//            }
//        });
//    }

    public interface NutrientCallback {
        void onCallback(double fatAmount, double saturatedFatAmount, double proteinAmount, double sodiumAmount, double potassiumAmount, double carbsAmount, double fiberAmount, double sugarAmount);
    }

    public interface CaloriesMealCallback {
        void onCallback(HashMap<String, Double> caloriesMap);
    }

    public interface DayCaloriesBurnedCallback {
        void onCallback(long calories);
    }

    public interface DayCaloriesCallback {
        void onCallback(double calories);
    }
}
