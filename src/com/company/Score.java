package com.company;

/**
 * Created by Christian on 02.07.2017.
 */
public class Score {

    public Score(){

    }

    public int getScore(int turnsForCompletion, int turnsTotal){
        return (int) Math.ceil(((turnsTotal - turnsForCompletion) / turnsTotal)*100);
    }
}
