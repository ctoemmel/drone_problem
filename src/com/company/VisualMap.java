package com.company;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
/**
 * Created by Christian on 02.07.2017.
 */
public class VisualMap {
    private static int PIXEL_PER_SINGLE_FIELD = 40;
    private int width;
    private int height;
    private ArrayList<Order> orders;
    private ArrayList<Warehouse> warehouses;
    private ArrayList<Drone> drones;



    public VisualMap(int x, int y, ArrayList<Order> orders, ArrayList<Warehouse> warehouses, ArrayList<Drone> drones){
        width = x*PIXEL_PER_SINGLE_FIELD;
        height = y*PIXEL_PER_SINGLE_FIELD;
        this.orders = orders;
        this.warehouses = warehouses;
        this.drones = drones;
    }


        public void draw() throws Exception {
            try {

                // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
                // into integer pixels
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D ig2 = bi.createGraphics();


                Font font = new Font("TimesRoman", Font.BOLD, 20);
                ig2.setFont(font);
                ig2.setBackground(Color.white);
                ig2.setPaint(Color.white);
                int x = 0;
                int y = 0;
                int newx = 0;
                int newy = 0;
                int wFields = (int) (width/PIXEL_PER_SINGLE_FIELD);
                int hFields = (int) (height/PIXEL_PER_SINGLE_FIELD);
                for(int i = 0; i < wFields; i++){
                    x = i*PIXEL_PER_SINGLE_FIELD;
                    ig2.drawLine(x, 0, x, height);
                }

                for(int i = 0; i < hFields; i++){
                    y = i*PIXEL_PER_SINGLE_FIELD;
                    ig2.drawLine(0,y, width, y);
                }

                ig2.setPaint(Color.red);

                //Alle Aufträge zeichnen
                for(int i = 0; i < orders.size(); i++){
                    x = PIXEL_PER_SINGLE_FIELD * orders.get(i).getX();
                    y = PIXEL_PER_SINGLE_FIELD * orders.get(i).getY();
                    //ig2.drawRect(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD);

                    ig2.fill(new Rectangle2D.Double(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD));
                }

                ig2.setPaint(Color.yellow);
                //Alle Warenhäuser zeichnen
                for(int i = 0; i < warehouses.size(); i++){
                    x = PIXEL_PER_SINGLE_FIELD * warehouses.get(i).getX();
                    y = PIXEL_PER_SINGLE_FIELD * warehouses.get(i).getY();
                    //ig2.drawRect(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD);

                    ig2.fill(new Rectangle2D.Double(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD));
                }

                //Alle Dronen zeichnen
                /*  Nicht wirklich sinnvoll?
                for(int i = 0; i < warehouses.size(); i++){
                    x = PIXEL_PER_SINGLE_FIELD * warehouses.get(i).getX();
                    y = PIXEL_PER_SINGLE_FIELD * warehouses.get(i).getY();
                    //ig2.drawRect(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD);

                    ig2.fill(new Rectangle2D.Double(x, y, PIXEL_PER_SINGLE_FIELD, PIXEL_PER_SINGLE_FIELD));
                }
                */

                ImageIO.write(bi, "PNG", new File("this.PNG"));
                //ImageIO.write(bi, "JPEG", new File("c:\\yourImageName.JPG"));
                //ImageIO.write(bi, "gif", new File("c:\\yourImageName.GIF"));
                //ImageIO.write(bi, "BMP", new File("c:\\yourImageName.BMP"));

            } catch (IOException ie) {
                ie.printStackTrace();
            }

        }

}
