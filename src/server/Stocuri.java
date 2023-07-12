
package server;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
    Clasa de baza pentru sistem informatic stocuri.
*/

public class Stocuri implements Serializable {

    // date referitoare la stocuri

    public List<Double> pret;   // lista numere virgula obiect double java
    public List<String> nume;   // lista sir de caractere specific obiect java

    public double media;	// stocam media preturilor

    Stocuri() {};

    /* 
        Un constructor pentru determinarea numarului de elemente in
        stoc la un transfer de date.
    */

    Stocuri(int n) {
        /*
            Nu este necesara precizarea tipului de date.
            Tipul de date este deja stabilit la initializarea campu-
            lui din clasa.
        */
        // 
        pret = new ArrayList<>(n);
        nume = new ArrayList<>(n);
    } 


    public double suma (List<Double> pret) {
        return pret.stream().collect(
                Collectors.summingDouble(Double::doubleValue));
    } 

    public double medie(List<Double> pret) {
        
        double medie_preturi = suma(pret) / pret.size();
        String format = String.format(Locale.ENGLISH,"%.2f", medie_preturi);
        medie_preturi = Double.parseDouble(format);
        return medie_preturi;
    }

    // luam suma elementelor din lista preturilor

    public double suma(double[] lista) {
            double s = 0;
            int nr_elemente = lista.length;

            for(int i = 0; i < nr_elemente; i++) {
                    s += lista[i];	// insumam elementele din lista prin s
            }

            return s;
    }

    // functie de calcul a mediei intre stocuri, primeste lista ca parametru

    public double medie(double[] a) {

            // luam numarul de elemente

            int nr_elemente = a.length;

            // suma elementelor din lista impartita la numarul de elemente
            
            return suma(a) / nr_elemente;
    }
    
    // Verifica daca dimensiunile celor doua sunt subunitare
    public boolean check_size() {
        return  (pret.size() < 1 || nume.size() < 1) || 
                (pret == null || nume == null)          ;
    }
    
    // Reda mesaj, intreaba de numarul de stocuri si reda acesta.
    public int prompt() {
        
        System.out.println("Produse cu pret " + pret.size());
        System.out.println("Produse cu nume " + nume.size());
        
        while(true) {  
            Scanner sc = new Scanner(System.in); 
            try {
                System.out.print("Cate elemente aveti in contabilitate? > ");
                String numar = sc.next();
                if("".equals(numar)) { return 0;}
                
                int m = Integer.parseInt(numar);
                
                if(m < 0) {
                    break;
                }
                
                pret = new ArrayList<>(m);
                nume = new ArrayList<>(m);
                return m;
            } catch (Exception e) {
                System.out.println("Dati un numar intreg 0,253,961.");
            }
        }
        return 0;
    }

    public synchronized void intro_date() {   
        int m = 0;
        Scanner sc = new Scanner(System.in); 
        
        if (check_size()) {

            m = prompt();
            
            // Programul se inchide daca m ramane 0.
            if (m==0) {
                System.exit(0);
            }
        }

            
        for(int i = 0; i < m; i++) {
            System.out.println("Dati numele stocului> ");

            this.nume.add(sc.nextLine());
            //this.nume[i] = sc.nextLine();


            System.out.println("Dati pretul> ");
            
            double pretul = Double.parseDouble(sc.nextLine());
            
            String format = String.format(Locale.ENGLISH,"%.2f", pretul);
            
            pretul = Double.parseDouble(format);
            // se face o rotunjire la doua zecimale
            this.pret.add(pretul);
            //this.pret[i] = Double.parseDouble(sc.nextLine());
        }

        media = medie(pret);
    }

    public synchronized void serial(Stocuri s) {

            String nume_fisier = "Stocuri.st";

            // Serializarea 
            try
            {   
                    // Salvarea obiectului in fisier
                    FileOutputStream fisier = new FileOutputStream(nume_fisier);
                    ObjectOutputStream out = new ObjectOutputStream(fisier);

                    // Metoda pentru serializarea primei serii de stocuri
                    out.writeObject(s);

                    out.close();
                    fisier.close();

                    System.out.println
                    ("Obiectul cu datele stocurilor a fost serializat.");

            } catch(NotSerializableException e)
            {
                    System.out.println
                    ("Exceptie de serializare este prinsa : " + e);
            } catch(IOException e)
            {
                    System.out.println
                    ("Exceptie de date intrare si iesire este prinsa : " + e);
            }
    }
    
    
    public void scrie_html(String[] date) {
        
            String nume_documt = "Stocuri.html";
            String cale_fisier = "E:\\Javatohtml\\";
            String scriere = cale_fisier + nume_documt;
            String[] continut_document = {  "<html><title>Stocuri</title>\n",
                                            "<body>\n",
                                            "<ul><h1>Stocuri</h1><ul>\n",
                                            "<li> Nume: ", 
                                            " </li>\n",
                                            "<li> Pret: ", 
                                            " </li>\n",
                                            "<li> Media: ",
                                            " </li>\n",
                                            "</body>\n",
                                            "</html"};
        
            try {
                int i=0,j=i+3;
                
                while(i < 3) {
                    continut_document[j] += date[i];
                    i+=1;
                    j+=2;
                }
                
                // Pentru scriere la html
                FileOutputStream fisierdoc = new FileOutputStream(scriere);
                PrintWriter scrieredoc = new PrintWriter(fisierdoc);

                for(String elemente: continut_document) {
                    scrieredoc.write(elemente);
                }
                scrieredoc.close();
                fisierdoc.close();
            } catch(Exception e) {
                System.out.println("La scrierea html: " + e);
            }
    }

    public synchronized String[] deserial(Stocuri s) {
            String[] deserial = new String[3];
            String nume_fisier = "Stocuri.st";

            // Deserializare 
            try
            {   
                    
                    // Citirea obiectului din fisier
                    FileInputStream fisier = new FileInputStream(nume_fisier);
                    ObjectInputStream intrare = new ObjectInputStream(fisier);

                    // Method for deserialization of object
                    s = (Stocuri)intrare.readObject();

                    intrare.close();
                    fisier.close();
                    
                    deserial[0] = s.nume.toString();
                    deserial[1] = s.pret.toString();
                    deserial[2] = String.format("%.2f", media);
                    scrie_html(deserial);

                    // Afisam datele deserializate utilizand metoda toString

                    System.out.println
                        ("Obiectul stocurilor a fost deserializat \n"
                            + "Datele obiectului : ");

                    deserial = new String[3];
                    deserial[0] = "Denumirea stocurilor : " 
                            + s.nume.toString() + "\n";
                    deserial[1] = "Pretul stocurilor    : " 
                            + s.pret.toString() + "\n";
                    deserial[2] = "Media preturilor     : " 
                            + String.format("%.2f",s.media) + "\n";
                    
                    System.out.println(deserial[0]);
                    System.out.println(deserial[1]);
                    System.out.println(deserial[2]);
                    
                    
                    
            } catch(WriteAbortedException e)
            {
                    System.out.println(
                            "Exceptie de intrare sau afisare prinsa : " + e);
            } catch(IOException e)
            {
                    System.out.println(
                            "Exceptie de intrare sau afisare prinsa : " + e);
            } catch(ClassNotFoundException e)
            {
                    System.out.println
                            ("Exceptie referitoare la clasa"
                        + " ( ClassNotFoundException ) a fost prinsa : " + e);
            }

            return deserial;
    }
    
    public void write_excel() {
        
    }
    
    public static void main(String[] args) {
        Stocuri s1 = new Stocuri(0);
        Stocuri s2 = new Stocuri(4);
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=-----------------------------------=");
        System.out.println("=SMS - Sistem de administrat stocuri=");
        System.out.println("=-----------------------------------=");
        
        s1.intro_date();
        
        //s1.print();
        
        s1.serial(s1);
        s1.deserial(s1);
        
    }

}