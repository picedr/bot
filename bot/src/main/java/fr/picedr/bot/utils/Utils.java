package fr.picedr.bot.utils;

import java.util.Random;

public class Utils {


    /**
     * Renvoie une valeur entre 0 et max (exclu)
     * @param max
     * @return
     */
    public static int rand(int max){
        Random r = new Random();
        return r.nextInt(max);
    }



}
