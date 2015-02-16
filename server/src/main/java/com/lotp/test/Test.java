package com.lotp.test;

import jskills.*;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

import java.util.Collection;
import java.util.Map;

/**
 * User: jitse
 * Date: 12/26/13
 * Time: 6:48 PM
 */
public class Test {

    public static void main(String[] args){
        String s = "https://lh4.googleusercontent.com/-D8kX_PGUVTc/AAAAAAAAAAI/AAAAAAAAzCI/odnsVyvbpJo/photo.jpg?sz=50";
        if (s.contains("sz=")) {
            String newString = s.substring(0, s.indexOf("sz=") + 3);
            System.out.println(newString);
        }
    }


}
