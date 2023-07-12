/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.*;
import java.util.*;

/**
 *
 * @author diabl
 */
public class Autentificator {
    private static String[][] utilizatori = new String[0][2];
    
    protected static String[] preia_date() {
        Scanner sc = new Scanner(System.in);
        String[] date = new String[2];
        System.out.println("Dati un nume de utilizator> ");
        date[0] = sc.next();  //nume
        System.out.println("Dati o parola> ");
        date[1] = sc.next();  //parola
        System.out.println("S-au introdus date cu succes.");
        return date;
    }
    
    protected static String[][] inregistrare(
            String nume, 
            String parola, 
            String[][] tabel_utilizatori
    ) 
    {
        tabel_utilizatori = new String
                [tabel_utilizatori.length+1]
                [2];
        
        tabel_utilizatori[tabel_utilizatori.length-1][0] = nume;
        tabel_utilizatori[tabel_utilizatori.length-1][1] = parola;
        
        
        return tabel_utilizatori;
    }
    
    protected static void afiseaza_utilizatori(Object[][] tabel_utilizatori) {
        System.out.println(tabel_utilizatori.length);
        System.out.println("{");
        int rowlen = tabel_utilizatori.length;
        int collen = tabel_utilizatori[0].length;
        for (int i = 0; i < rowlen; i++) {
            for (int j = 0; j < collen;j++) {
                System.out.print(tabel_utilizatori[i][j].toString());
                System.out.print(",");
                
            }
            System.out.println("");
        }
        System.out.println("}");
    }
    
    protected static boolean autentificare(
        String nume,
            String parola,
            Object[][] utilizatori
    ) 
    {
        boolean autentifica = true;
        for (Object[] utilizatori1 : utilizatori) {
            if (nume != utilizatori1[0]) {
                autentifica = false;
            }
            if (parola != utilizatori1[1]) {
                autentifica = false;
            }
        }
        return autentifica;
    }
    
    public static void main(String[] args) {
        String[] date = new String[2];
        Scanner sc = new Scanner(System.in);
        System.out.println("Inregistrat? (d/n)>");
        if("d".equals(sc.nextLine())) {
            date = preia_date();
            autentificare(date[0],date[1],utilizatori);
        }
        else {
            System.out.println("Va inregistrati? (d/n)");
            if("d".equals(sc.nextLine())) {
                date = preia_date();
                utilizatori = (String[][]) inregistrare(date[0],date[1],utilizatori);
            }
        }
        afiseaza_utilizatori(utilizatori);
    }
    
}
