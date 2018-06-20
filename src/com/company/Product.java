package com.company;

/**
 * Created by Christian on 29.06.2017.
 */
public class Product {
    private int weight;
    private int id;
    private DroneProblem problem;

    public Product(int id, int weight, DroneProblem dp){
        this.problem = dp;
        this.id = id;
        this.weight = weight;
    }

    public int getWeight(){
        return this.weight;
    }

    public int getID(){
        return this.id;
    }

    public String getName() {
        return "Produkt " + this.id;
    }

    public boolean equals(Product p){
        return p.getID()==this.id;
    }
}
