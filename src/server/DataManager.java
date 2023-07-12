/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author diabl
 */
public class DataManager {
    
    private static String clasa_driver = "oracle.jdbc.driver.OracleDriver";
    private static String db   = "jdbc:oracle:thin:@localhost:1521:xe";
    
    
    public static void print(Object s) {
        System.out.println(String.format("%s",s));
    }
    
    // contorizarea coloanelor dintr-o interogare
    private static int numarColoane(String interogare) {
        int ncol = 0;
        for (int i = 0; i < interogare.length(); i++) {
            // "" este pentru string, iar '' este pentru char
            if(interogare.charAt(i) == ',') {
                ncol++;
            } else {}
        }
        ncol++;
        return ncol;
    }
    
    private static void printValues2(ResultSet rs, String coloane) {
        
        int i = 0;  //randuri
        int j = 1;  //coloane
        int numar_coloane = numarColoane(coloane);
        //print("Numar de coloane: "+numar_coloane);
        try {
            Object data;
            
            while(rs.next()) { 
                //i = 0;
                //j=1;
                
                while(j < numar_coloane) {
                    data = rs.getObject(j);
                    
                    print(data);
                    j++;
                }
                j=1;
            }
            print("S-a terminat cu succes!");
        } catch (SQLException sqle) {
            print(String.format("%s", "randul: "+ i +", coloana: "+j));
            
            sqle.printStackTrace();
            
            System.exit(-1);
        }
    }
    
    
    private static void afisareUtilizatori(String user,String pass) {
        String schema=user;
        if (user != "") {
            schema = user + ".";
        }
        try {
            Class.forName(clasa_driver);
            Class.forName("java.awt.Component");
        
            Connection con = DriverManager.getConnection(db,user,pass);
            
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("select * from "+ schema +"utilizatori");
            
            //int i = 1;
            while(rs.next()){
                
                System.out.println(rs.getObject(0).toString());
                System.out.println(rs.getObject(1).toString());
                System.out.println(rs.getObject(2).toString());
            }
            
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Nu aveti clasa " + clasa_driver);
            System.out.println("sau " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("\n Cauza: ");
            sqle.printStackTrace();
        }
    }
    
    
    private static void queryTest2() {
        String clasa_driver = "oracle.jdbc.driver.OracleDriver";
        String db   = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "Ungureanu";
        String pass = "dan123";
        String coloane = "idelement,denumire,cod_cont,unitate_masura,numar,pret_buc";
        
        try {
            Class.forName(clasa_driver);
            Class.forName("java.awt.Component");
        
            Connection con = DriverManager.getConnection(db,user,pass);
            
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("select " + coloane
                    + " from Ungureanu.stocuri");
            
            printValues2(rs, coloane);
            
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Nu aveti clasa " + clasa_driver);
            System.out.println("sau " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("\n Cauza: ");
            sqle.printStackTrace();
        }
        
    }
    
    
    protected String getDriver(){
        return this.clasa_driver;
    }
    
    protected String getDB(){
        return this.db;
    }
    
    
    protected static void afisareBD(String coloane) {
        String user = "Ungureanu";
        String pass = "dan123";
        if(coloane=="") {
            
            coloane = "idelement,denumire,cod_cont,unitate_masura,numar,pret_buc";
        }
        
        try {
            Class.forName(clasa_driver);
            Class.forName("java.awt.Component");
        
            Connection con = DriverManager.getConnection(db,user,pass);
            
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("select " + coloane
                    + " from Ungureanu.stocuri");
            
            printValues2(rs, coloane);
            
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Nu aveti clasa " + clasa_driver);
            System.out.println("sau " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("\n Cauza: ");
            sqle.printStackTrace();
        }
        
    }
    
    
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // TODO code application logic here
        //afisareBD("");
        afisareUtilizatori("Ungureanu","dan123");
    }
}
