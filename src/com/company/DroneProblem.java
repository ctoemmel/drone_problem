package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Christian on 29.06.2017.
 */
public class DroneProblem {
    ArrayList<Product> products;
    ArrayList<Warehouse> warehouses;
    ArrayList<Drone> drones;
    ArrayList<Order> orders;
    private int rows;
    private int columns;
    private int numberOfDrones;
    private int maxTurns;
    private int maxPayload;
    private int numberOfOrders;
    private int turnCounter = 0;
    private Score score;
    private LinkedHashMap<Integer, Order> completedOrders;

    private VisualMap vm;

    private String outputFile = "";

    private String filename = "";

    public DroneProblem(String description, String filename){
        String[] descs = description.split(" ");
        System.out.println(descs[4]);
        int[] descInts = new int[descs.length];
        this.filename = filename;

        this.rows           = Integer.parseInt(descs[0]);
        this.columns        = Integer.parseInt(descs[1]);
        this.numberOfDrones = Integer.parseInt(descs[2]);
        this.maxTurns       = Integer.parseInt(descs[3]);
        this.maxPayload     = Integer.parseInt(descs[4]);

    }

    public void init(ArrayList<Product> products, ArrayList<Warehouse> warehouses, ArrayList<Drone> drones, ArrayList<Order> orders){
        this.products = products;
        this.warehouses = warehouses;
        this.drones = drones;
        this.orders = orders;
        this.score = new Score();
        this.completedOrders = new LinkedHashMap<>();
        boolean debugDraw = false;


        if(debugDraw) {
            vm = new VisualMap(rows, columns, orders, warehouses, drones);
            try {
                vm.draw();
            } catch (Exception e) {

            }
        }
    }



    public void solve(){
        //TODO
        /*
                WICHTIG
                        Es geht NICHT darum, alle Aufträge abzuwickeln, sondern einen hohen Score
                        in der gegebenen Zeit zu erzielen!
                        Mein erster Versuch hat darin geendet, dass ich von Anfang an auf den Ansatz
                        alle Aufträge abzuwickeln hingearbeitet habe.

         */
        //sortOrdersByWeight(orders);
        //sortOrdersByAmountOfProducts(orders);
        sortOrdersByDistance(warehouses.get(0));
        sortProductsByWeight(orders);

        int initial = 0;
        int run = 0;
        int ordersSize = orders.size();
        for(Order o : orders){
            if(o.getWeight()<=200)
                initial++;
        }
        System.out.println("Initial lösbare Aufträge " + initial);

        while(turnCounter<maxTurns && orders.size()>0){
            for(Order o : orders){
                if(o.getWeight()<=maxPayload && !o.getExecStatus()){
                    for(Drone d : drones){
                        if(!d.isBusy()){
                            o.setExecStatus(true);
                            d.say("bearbeitet Auftrag " + o.getName() + "  ");

                            for(Product p : o.getAllProducts()) {
                                int amount = 1; //TODO gibt es mehrere Produkte in einem Warehouse, die man gleichzeitig Laden kann?
                                Warehouse target = null;
                                //wo ist das Product?
                                sortWarehousesByOrderProducts(warehouses, o);
                                sortWarehousesByDistance(warehouses, o, d);
                                for (Warehouse w : warehouses){
                                    ArrayList<Product> productsinWarehouses = w.getProductsInWarehouse();
                                    for (Product pr : productsinWarehouses){
                                        if (pr.getID() == p.getID())
                                            target = w;
                                    }
                                }

                                d.Load(target, o, p, amount);
                            }

                            d.Deliver(warehouses, o, o.getAllProducts());
                            break;
                        }
                    }
                }
            }

            //Wie viele Produkte enthält eine Order maximal?
            int orderMax = 0;
            for (Order o : orders) {
                if (o.getAmount()> orderMax)
                    orderMax = o.getAmount();
            }


                // Führe Orders aus in aufsteigender Anzahl von Produkten
            for (int amount = 2;amount<=orderMax;amount++) {
                for (Order o : orders) {
                    if (!o.getExecStatus() && o.getAmount() == amount) {
                        for (Drone d : drones) {
                            if (!d.isBusy()) {
                                o.setExecStatus(true);
                                d.say("bearbeitet Auftrag " + o.getName() + "  ");
                                // Tue solange, bis alle Produkte der Order geliefert wurden
                                while (o.getAllProducts().size() > 0) {
                                    boolean droneIsFull = false;
                                    ArrayList<Product> productsForDelivery = new ArrayList<Product>();
                                        for (Product p : o.getAllProducts()) {
                                            int productAmount = 1; //TODO gibt es mehrere Produkte in einem Warehouse, die man gleichzeitig Laden kann?
                                            Warehouse target = null;
                                            //wo ist das Produkt?
                                            sortWarehousesByOrderProducts(warehouses, o);
                                            sortWarehousesByDistance(warehouses, o, d);
                                            for (Warehouse w : warehouses) {
                                                ArrayList<Product> productsinWarehouses = w.getProductsInWarehouse();
                                                for (Product pr : productsinWarehouses) {
                                                    if (pr.getID() == p.getID())
                                                        target = w;
                                                }
                                            }
                                            //Fliege zum Warenhaus und lade das Produkt
                                            if ((d.getMaxPayload() - d.getCurrentPayLoad()) > p.getWeight()) {
                                                d.Load(target, o, p, productAmount);
                                                productsForDelivery.add(p);
                                            } else
                                                droneIsFull = true;
                                        }
                                    //Liefere das Produkt
                                    d.Deliver(warehouses, o, productsForDelivery);

                                }
                                break;
                            }
                        }
                    }
                }
            }






            //ODOT
           // System.out.println("Zug: " + run);
            //Jeden Zug wird ein mal tick() aufgerufen, was alle Dronen einen Zug machen lässt (also bspw. eine Zeiteinheit
            //vom Flug zu einer Order oder so
            run = tick();
            //if (ordersSize != orders.size()){
            //    ordersSize = orders.size();
                System.out.println("Tick #" + run + " | Anzahl Aufträge:" + orders.size());
            //}
        }
        writeToFile();
    }

    public int tick(){
        /*
            Lässt einen Zug vergehen. Also konkret, jeder Drone ruft einmal tick() auf.
            Wenn eine Drone auf workturns==0 tickt, dann lädt sie alles was die hat beim aktuellen Order-standort aus
            Das soll sozusagen die Bewegung simulieren
            TODO optimieren: Vielleicht kann eine Drone mehrere Pakete mitnehmen für verschiedene Orders.
         */
        isSolved();
        turnCounter++;

        for(Drone d : drones){
            //turnCounter++;
            d.tick();
        }

        for(Order o : orders){
            o.tick();
        }

        return turnCounter;

    }

    /*
            Nimmt die erledigten Aufträge aus der orders-ArrayList und packt sie samt erzieltem Score in
            die completedOrders - Hashmap.

            Fragt ursprünglich ab, ob alle Aufträge erledigt sind. Da es aber nicht drum geht alle Aufträge zu erledigen,
            sondern viele möglichst schnell, ist die Funktion nicht mehr zu diesem Zwecke zu nutzen

     */
    public boolean isSolved(){ //returned false: Nicht gelöst! returned true: gelöst!
        boolean finalCheck = true;
        ArrayList<Order> notCompletedOrders = new ArrayList<>();
        for(Order o : orders){
            boolean oC = o.completed();
            if(oC) {
                o.say("Erfolgreich beendet.");
                o.getProblem().addToOutputFile(o.getCommand());
                int orderScore = score.getScore(turnCounter, maxTurns);
                completedOrders.put(orderScore, o);
            }
            if(!oC) {
                notCompletedOrders.add(o);
                finalCheck = false;
            }
        }
        orders = notCompletedOrders;
        return finalCheck;
    }
    /*
            Sucht die Drone heraus, die den geringsten physischen Abstand zu einer Order hat
            Vernachlässigt im aktuellen Ansatz noch das bereits geladene Gewicht und das Gewicht des Auftrags
            TODO VIEL POTENTIAL UND DERZEIT MIT DEN GENANNTEN VERNACHLÄSSIGUNGEN NOCH FALSCH
     */

    public Drone findBestDrone(Order o){ //TODO Sehr naiver Ansatz. Viel Verbesserungspotential
        Drone bestDrone = null;
        double mindist = Double.MAX_VALUE;
        double currentdist = 0.0;
        for(Drone d : drones){
            if(!d.isBusy()){
                currentdist = d.distanceTo(o.getLocation());
                if(currentdist<mindist){
                    mindist = currentdist;
                    bestDrone = d;
                }

            }
        }
        return bestDrone;
    }

    public int getNumberOfDrones(){
        return this.numberOfDrones;
    }

    public int getMaxPayload(){
        return this.maxPayload;
    }

    public int getMaxTurns(){
        return this.maxTurns;
    }

    public int getRows(){
        return this.rows;
    }

    public int getColumns(){
        return this.columns;
    }

    public void setNumberOfOrders(int numberOfOrders){
        this.numberOfOrders = numberOfOrders;
    }

    public void sortOrdersByDistance(Warehouse w){
        ArrayList<Order> newo = new ArrayList<>();
        boolean notSorted = true;

       while(notSorted){

           for(int i = 0; i < orders.size()-1; i++){
               if(orders.get(i).distanceTo(w.getLocation()) > orders.get(i+1).distanceTo(w.getLocation())){
                   Order tmp = orders.get(i);
                   orders.set(i, orders.get(i+1));
                   orders.set(i+1, tmp);
               }
           }


           boolean tmpNotSorted = false;
           for(int i = 0; i < orders.size()-1; i++){
               if(!(orders.get(i).distanceTo(w.getLocation()) <= orders.get(i+1).distanceTo(w.getLocation()))){
                   tmpNotSorted = true;
               }

           }

           notSorted = tmpNotSorted;
       }
        for(Order o : orders){
            //System.out.println("Order entfernung" + o.distanceTo(w.getLocation()));
        }
    }

    public void sortProductsByWeight(List<Order> orders){
        for (Order o : orders){
            List<Product> products = o.getAllProducts();
            Collections.sort(products, (second, first) -> first.getWeight() - second.getWeight());
        }
    }

    public void sortWarehousesByOrderProducts (ArrayList<Warehouse> warehouses, Order o){

        for (Warehouse w : warehouses){
            w.setProductsOfOrder(0);
            for (Product po : o.getAllProducts()) {
                for (Product pw : w.getProductsInWarehouse()) {
                    if (pw.getWeight() == po.getWeight()){
                        w.setProductsOfOrder(w.getProductsOfOrder()+1);
                        break;
                    }
                }
            }
        }

        ArrayList<Warehouse> w = warehouses;
        Collections.sort(w, (first, second) -> first.getProductsOfOrder() - second.getProductsOfOrder());
    }

    public void sortWarehousesByDistance (ArrayList<Warehouse> warehouses, Order o, Drone d){
        ArrayList<Warehouse> w = warehouses;
        Collections.sort(w, (second, first) -> (first.distanceTo(o.getLocation()) + first.distanceTo(d.getLocation())) - (second.distanceTo(o.getLocation())+ second.distanceTo(d.getLocation())));
    }

    public void sortOrdersByWeight(ArrayList<Order> orders){
        Collections.sort(orders, (second, first) -> first.getWeight() - second.getWeight());
    }

    public void sortOrdersByAmountOfProducts (ArrayList<Order> orders){
        Collections.sort(orders, (second, first) -> first.getAmount() - second.getAmount());
    }

    public void writeToFile(){
     /*   String futureFile = "("+size+", (";
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                Kakurofeld field = fields[i][j];

                if(field.getType()==-2){
                    futureFile+="0, ";
                }
                else if(field.getType()==-1){
                    futureFile+="("+field.getRechts()+", "+field.getUnten()+"), ";
                }
                else if(field.getType()==0){
                    futureFile+=field.getLoesung()+", ";
                }
            }
        }
        futureFile=futureFile.substring(0,futureFile.length()-2);
        futureFile += "), "+Guesses.getGuesses() + ", "+backtracks+")";
        System.out.println(futureFile); */
     int amtLines = outputFile.split("\n").length - 1;
     String futureFile = amtLines + outputFile;
     String file = this.filename.substring(0,this.filename.length()-2) + "out";
        File result = new File(file);
        try{
            result.createNewFile();

            FileWriter fileWriter = new FileWriter(result, false);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(futureFile);
            bufferedWriter.close();

            System.out.println("Datei erfolgreich geschrieben.");
        } catch(IOException e) {
            System.out.println("Fehler beim Schreiben.");
            System.exit(1);
        }

    } //Ausgabefunktion

    public void addToOutputFile(String s){
        outputFile += s;
    }

}