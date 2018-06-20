package com.company;

import java.util.ArrayList;

/**
 * Created by Christian on 29.06.2017.
 */
public class Order extends two_dim {
    private int amtRequested;
    private ArrayList<Product> productsWanted;
    private ArrayList<Product> deliveredProducts;
    private int id;
    private boolean inExecution = false;
    private int workturns = 0;
    private boolean completed = false;

    private String Command = "";

    public Order(int id, int x, int y, int amt, DroneProblem dp){
        productsWanted = new ArrayList<>();
        this.problem = dp;
        this.id = id;
        this.name = "Order " + id;
        this.location = new Location(x, y);
        amtRequested = amt;
        deliveredProducts = new ArrayList<>();
        Command = "";
    }



    public boolean completed(){
        return completed;
    }

    public void deliver(ArrayList<Product> ps, int workturns){
        this.workturns = workturns;
        ArrayList<Product> workingList = new ArrayList<>();
        for(Product p : ps) {

            deliveredProducts.add(p);

            say("Produkt mit Gewicht " + p.getWeight() + ", ID #" + p.getID() + " zu Kunde geliefert.", "  ");
             }
        ArrayList<Product> pW2 = new ArrayList<>();
        pW2 = (ArrayList<Product>) productsWanted.clone();
        for(Product p : ps){

            pW2.remove(p);
        }

        productsWanted = pW2;
        //setExecStatus(false); //TODO benötigt?

    }

    public void deliver(Product p, int workturns){
        this.workturns = (this.workturns>workturns)?this.workturns:workturns;
            deliveredProducts.add(p);

        say("Produkt mit Gewicht " + p.getWeight() + ", ID #" + p.getID() + " zu Kunde geliefert.", "  ");
       productsWanted.remove(p);

        //setExecStatus(false); //TODO benötigt?
    }

    // Speichert für jede Order den Command, um die Order erst auszuführen, wenn sie innerhalb der Ticks bearbeitet werden kann
    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }




    /*
        Gibt zurück wie viele Produkte noch auszuliefern sind.
     */
    public int getAmount(){
        return productsWanted.size();
    }

    /*
        Fügt ein Produkt der Bestellliste hinzu.
     */
    public void addProduct(Product p){
        productsWanted.add(p);
    }

    /*
        Gibt das Produkt an der Stelle id zurück.
     */
    public Product getProduct(int id){
        return productsWanted.get(id);
    }

    /*
        Gibt den Executionstatus zurück.
     */

    public boolean getExecStatus(){
        return inExecution;
    }

    /*
        Setzt den Executionstatus.
     */
    public void setExecStatus(boolean b){
        inExecution = b;
    }


    /*
        Gibt das Gewicht aller noch nicht erhaltenen Produkte zurück.
     */
    public int getWeight(){
        int sum = 0;
        for(int i = 0; i < productsWanted.size(); i++)
            sum += productsWanted.get(i).getWeight();
        return sum;
    }

    public ArrayList<Product> getAllProducts(){
        return productsWanted;
    }

    public void tick(){
        if(workturns > 0)
            workturns--;
        if(workturns==0){
            if(productsWanted.size() == 0)
                this.completed = true;
        }
    }

    public int getID(){
        return this.id;
    }

}
