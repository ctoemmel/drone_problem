package com.company;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException{


        boolean debug = false; //True = Konsolenoutput; False = Kein Konsolenoutput (->Laufzeit besser)
        if (!debug) {

            System.setOut(new PrintStream(new OutputStream() {
                public void close() {
                }

                public void flush() {
                }

                public void write(byte[] b) {
                }

                public void write(byte[] b, int off, int len) {
                }

                public void write(int b) {
                }
            }));
        }

        String file;
        String fileStr;
        if(args.length > 0)
            file = args[0];
        else
            file = "./moaw.in";
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            fileStr = sb.toString();
        } finally {
            br.close();
        }
        //System.out.println("Folgende Datei gelesen:");
        //System.out.println(fileStr);
        System.out.println("");
        System.out.println("");
        System.out.println("");

        String[] lines = fileStr.split("\\\n");
        for(int i = 0; i < lines.length; i++){
            lines[i] = lines[i].substring(0, lines[i].length()-1);
        }

        String firstline = lines[0];

        DroneProblem droneProblem = new DroneProblem(firstline, file);
        int maxPayload = droneProblem.getMaxPayload();


        //Alle Produktgewichte abspeichern
        int numberOfProducts = Integer.parseInt(lines[1]);
        String[] productsWeights = lines[2].split(" ");
        ArrayList<Product> products = new ArrayList<>();
        for(int i = 0; i < numberOfProducts; i++){
            int singleProductWeight = Integer.parseInt(productsWeights[i]);
            products.add(new Product(i, singleProductWeight, droneProblem));
            //System.out.println("Produkt #"+i+" hinzugefügt. Gewicht: "+singleProductWeight);
        }

        //Alle Warenhäuser erstellen
        int numberOfWarehouses = Integer.parseInt(lines[3]);
        ArrayList<Warehouse> warehouses = new ArrayList<>();
        for(int i = 0; i < numberOfWarehouses; i++){
            String whLocation = lines[4+(2*i)];
            int whX = Integer.parseInt(whLocation.split(" ")[0]);
            int whY = Integer.parseInt(whLocation.split(" ")[1]);
            Warehouse wh = new Warehouse(i, whX, whY, numberOfProducts, droneProblem);

            System.out.println("Warenhaus #"+i+" hinzugefügt. Location: "+whX+"/"+whY);
            String[] productsInWh = lines[4+(2*i)+1].split(" ");

            for(int j = 0; j < numberOfProducts; j++){
                int amt = Integer.parseInt(productsInWh[j]);
                for(int k = 0; k < amt; k++){
                    wh.addProduct(products.get(j));
                }

                //System.out.println("Produkt #"+j+" in Warenhaus "+i+" hinzugefügt. Anzahl: "+productsInWh[j]);
            }

            // TODO Anzahl an Items
            warehouses.add(wh);
        }


        int numberOfDrones = droneProblem.getNumberOfDrones();
        ArrayList<Drone> drones = new ArrayList<>();
        for(int i = 0; i < numberOfDrones; i++){
            drones.add(new Drone(i, maxPayload, droneProblem, warehouses.get(0)));
        }
        //System.out.println(numberOfDrones+" Dronen hinzugefügt.");


        int newStart = 3+(numberOfWarehouses*2)+1;

        int numberOfOrders = Integer.parseInt(lines[newStart]);
        //System.out.println(numberOfOrders+" Aufträge gefunden. Erstelle...");

        ArrayList<Order> orders = new ArrayList<>();
        int startOfOrders = newStart+1;
        for(int i = 0; i < numberOfOrders; i++){
            String orderLocation = lines[startOfOrders+(3*i)];
            int orderX = Integer.parseInt(orderLocation.split(" ")[0]);
            int orderY = Integer.parseInt(orderLocation.split(" ")[1]);
            int numberOfProductsRequested = Integer.parseInt(lines[startOfOrders+(3*i)+1]);
            Order order = new Order(i, orderX, orderY, numberOfProductsRequested, droneProblem);

            //System.out.println("Auftrag #"+i+" hinzugefügt. Location: "+orderX+"/"+orderY+"; Anzahl besteller Produkte: "+numberOfProductsRequested);
            String[] productsRequested = lines[startOfOrders+(3*i)+2].split(" ");

            for(int j = 0; j < numberOfProductsRequested; j++){
                int no = Integer.parseInt(productsRequested[j]);
                int weight = Integer.parseInt(productsWeights[no]);
                //order.addProduct(j, no);
                order.addProduct(new Product(no, weight, droneProblem));

                //System.out.println("Produkt #"+no+" in Auftrag "+i+" an Stelle "+j+" hinzugefügt.");
            }

            // TODO Anzahl an Items
            orders.add(order);
        }


        droneProblem.init(products, warehouses, drones, orders);
        System.out.println("Alle Warenhäuser, Produkte, Dronen und Bestellungen hinzugefügt.");
        System.out.println("Beginne den Lösevorgang...");


        droneProblem.solve();

    }

}
