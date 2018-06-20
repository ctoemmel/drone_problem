package com.company;

/**
 * Created by Christian on 29.06.2017.
 */
public class Location {


    private int x;
    private int y;

    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int distanceTo(Location l){
        int dist = (int) Math.ceil(Math.sqrt(Math.pow(l.getX() - this.getX(), 2) + Math.pow(l.getY() - this.getY(), 2)));

        return dist;
    }

    public boolean equals(Location l){
        //System.out.println("Entspricht "+l.getX()+"/"+l.getY()+ " == "+ this.getX() + "/"+this.getY());
        boolean equal = (l.getX()==this.getX()) && (l.getY() == this.getY());
        System.out.println(equal);
        return equal;
    }

    public String getName(){
        return this.getX()+"/"+this.getY();
    }
}
