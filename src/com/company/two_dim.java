package com.company;

/**
 * Created by Christian on 30.06.2017.
 */
public abstract class two_dim {

    /*

            Abstrakte Klasse für alle zweidimensionalen Objekte (Main, Order, Warehouse, Drone)


     */

    protected Location location;
    protected String name;
    protected DroneProblem problem;

    public void setLocation(Location l){
        this.location = l;
    }

    public Location getLocation(){
        return this.location;
    }

    public int getX(){
        return this.location.getX();
    }

    public int getY(){
        return this.location.getY();
    }

    public void setX(int x){
        this.location.setX(x);
    }

    public void setY(int y){
        this.location.setY(y);
    }

    public DroneProblem getProblem() {
        return problem;
    }

    public void setProblem(DroneProblem problem) {
        this.problem = problem;
    }


    public int distanceTo(Location l){
        return this.location.distanceTo(l);
    }

    public void say(String s){
        System.out.println(name + ": " + s);
    }

    public void say(String s, String prefix){
        System.out.println(prefix + name + ": " + s);
    }

    public String getName(){
        return this.name;
    }

}
