package com.example.furever;

public class DogRecommendation {
    public String breed;
    public String match_percent;
    public String why;
    public String care_tips;
    public String imagePath;

    public DogRecommendation(String breed, String match_percent, String why, String care_tips, String imagePath) {
        this.breed = breed;
        this.match_percent = match_percent;
        this.why = why;
        this.care_tips = care_tips;
        this.imagePath = imagePath;
    }
}
