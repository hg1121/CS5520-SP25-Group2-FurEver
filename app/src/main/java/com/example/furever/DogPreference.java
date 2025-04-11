package com.example.furever;

import java.io.Serializable;

public class DogPreference implements Serializable {
    public String size;         // Q1
    public String exercise;     // Q2
    public String coatLength;  // Q3
    public String homeType;     // Q4
    public String haveChildren; // Q5
    public String budget;  // Q6

    public DogPreference() {}   // 必须有无参构造才能通过 Intent 传递
}
