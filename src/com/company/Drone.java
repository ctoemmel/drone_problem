package com.company;

import java.util.ArrayList;

/**
 * Created by Christian on 29.06.2017.
 */
public class Drone extends two_dim{

    private int maxPayload;


    private int currentPayLoad;
    private boolean busy;
    private int waitTurns;
    private int workturns;
    private Location destination;
    private ArrayList<Product> loadedProducts;
    private int id;
    public enum MODE {
        INIT,
        LOAD,
        DELIVER,
        UNLOAD
    };
    MODE mode = MODE.INIT;

    private Warehouse grabProductWarehouse;


    public Drone(int id, int pl, DroneProblem dp, Warehouse wh){
        this.problem = dp;
        this.name = "Drone "+id;
        this.location = new Location(wh.getX(), wh.getY());
        this.maxPayload = pl;
        this.busy = false;
        this.waitTurns = 0;
        this.id = id;
        this.loadedProducts = new ArrayList<>();
    }

    public void setBusy(boolean b){
        busy = b;
    }

    public void tick(){
        //Sollte so passen

        if(workturns>-10) //sinngemäß >0, aber -10 ist cooler
        workturns--;
        if(workturns==0) {
            busy = false;

        }
        if(waitTurns>0)
            waitTurns--;
    }

    public void Load(Warehouse w, Order o, Product p, int amount){
        w.removeProduct(p.getID());
        loadedProducts.add(p);
        setCurrentPayLoad(getCurrentPayLoad()+p.getWeight());


        //TODO erstmal hinfliegen

            workturns = workturns + 1*this.distanceTo(w.getLocation()) + 1;

        int x = w.getX();
        int y = w.getY();

        this.setX(x);
        this.setY(y);


        String command = this.id + " L "+ w.getID() + " " + p.getID() + " " + amount;
        o.setCommand(o.getCommand() + "\n" + command);
        //problem.addToOutputFile(command);
    }

    public void Deliver(ArrayList<Warehouse> w, Order o, ArrayList<Product> products){

        //TODO erstmal hinfliegen
        workturns = workturns + this.distanceTo(o.getLocation()) + 1;
        o.deliver(products, workturns); // vorher +2
        busy = true;
        String command = "";


        this.setX(o.getLocation().getX());
        this.setY(o.getLocation().getY());

        for(Product p : products) {
            setCurrentPayLoad(getCurrentPayLoad()-p.getWeight());
            command += "\n" + this.id + " D " + o.getID() + " " + p.getID() + " 1";

            //problem.addToOutputFile(command);
        }
        o.setCommand(o.getCommand() + command);

    }

    public void Deliver(ArrayList<Warehouse> w, Order o, Product product){

        //TODO erstmal hinfliegen
        workturns = workturns + this.distanceTo(o.getLocation()) + 1;
        o.deliver(product, workturns); // vorher +2
        setCurrentPayLoad(getCurrentPayLoad()-product.getWeight());
        busy = true;
        String command = "";


        this.setX(o.getLocation().getX());
        this.setY(o.getLocation().getY());

        command = this.id + " D " + o.getID() + " " + product.getID() + " 1";
        //problem.addToOutputFile(command);
        o.setCommand(o.getCommand() + "\n" + command);
    }


    public void Unload(Order o){
        //TODO

    }

    public void moveTo(Location l){
        if(!busy) {
            say("Bewegt sich nach " + l.getName());
            this.location = new Location(l.getX(), l.getY());
            workturns = (int) Math.ceil(this.distanceTo(l));
            busy = true;
        }
    }


    public void Wait(int t){
        this.waitTurns = t;
    }

    public int getWaitTurns(){
        return waitTurns;
    }

    public int getWorkturns(){
        return workturns;
    }

    public boolean isBusy(){
        return busy;
    }

    public int getID(){
        return this.id;
    }

    public void aktualisiereGewicht(){
        int w = 0;
        for(Product p : loadedProducts){
            w += p.getWeight();
        }
        this.currentPayLoad = w;
    }

    public int getCurrentPayLoad() {
        return currentPayLoad;
    }
    public void setCurrentPayLoad(int currentPayLoad) {
        this.currentPayLoad = currentPayLoad;
    }
    public int getMaxPayload() {
        return maxPayload;
    }


    public boolean canLoad(Product p){
        aktualisiereGewicht();
        return maxPayload-currentPayLoad>=p.getWeight();
    }

    public int canLoad(Order o){
        aktualisiereGewicht();
        int weightSum = 0;
        for(Product p : o.getAllProducts()){
            weightSum += p.getWeight();
        }
        return maxPayload-currentPayLoad-weightSum;
    }
}