package com.soswag.aidan.wordgrab.MyStatics;

/**
 * Created by Aidan on 2016-06-10.
 */
public class MyMath {

    public static double getHeavyWeightedRandomBetween(double min, double max){
        double random = Math.sqrt(Math.random()) * (max - min) + min;
        return random;
    }

    public static double closestToZero(double n, double m){
        return signOf(n) * Math.min(Math.abs(n), Math.abs(m));
    }

    public static int signOf(double n){
        if(n < 0)
            return -1;
        if(n > 0)
            return 1;
        return 0;
    }

    public static int roundToInt(double n){
        if(n % 1 < 0.5)
            return (int)n;
        return (int)n + 1;
    }
}
