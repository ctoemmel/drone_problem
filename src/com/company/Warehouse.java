package com.company;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Christian on 29.06.2017.
 */
public class Warehouse extends two_dim {

    private ArrayList<Product> productsInWarehouse;
    private int id;
    private int productsOfOrder; //(Hilfsvariable), wird zum sortieren benötigt


    public Warehouse(int id, int x, int y, int amountDifferentProducts, DroneProblem dp){ //amountDifferentProducts useless
        this.problem = dp;
        this.id = id;
        this.name = "Warenhaus " + id;
        this.location = new Location(x, y);
        this.productsInWarehouse = new ArrayList<>();
    }

    public void addProduct(Product p){
        productsInWarehouse.add(p);
    }

    public int getAmountOfProduct(int id){
        int amountOfProductsWithID = 0;
        for(int i = 0; i < productsInWarehouse.size(); i++){
            if (productsInWarehouse.get(i).getID() == id)
                amountOfProductsWithID++;
        }
        return amountOfProductsWithID;
    }

    public void removeProduct(int id){
        ArrayList<Product> newP = new ArrayList<>();
        newP = (ArrayList<Product>) productsInWarehouse.clone();
        for(Product p : newP){
            if(p.getID() == id){
                newP.remove(p);
                break;
            }
        }
        productsInWarehouse = (ArrayList<Product>) newP.clone();
        return;
    }

    public int getID(){
        return this.id;
    }
    public ArrayList<Product> getProductsInWarehouse() {
        return productsInWarehouse;
    }

    public int getProductsOfOrder() {
        return productsOfOrder;
    }

    public void setProductsOfOrder(int productsOfOrder) {
        this.productsOfOrder = productsOfOrder;
    }


}
