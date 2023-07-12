package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDate;

// Avem clasa de fire de executie si comunicatie
class Conexiune implements Runnable {

    int portul;
    static int con = 0;		// se tine cont de conexiuni
    Socket cs = null;		// socket pentru client

    // Clasele din java.io pentru comunicare prin mesaj
    // camp 4 si 5
    BufferedReader intrare;
    PrintWriter scriere;

    String mesaj;   		// 6 sirul care preia mesajul de la client

    // 7 utilizam date referitoare la clasa Stocuri din Stocuri.java
    Stocuri serie_stoc;

    public Conexiune(Socket client) throws IOException {

        cs = client;
        intrare = new BufferedReader(new InputStreamReader(
                cs.getInputStream()));
        scriere = new PrintWriter(new OutputStreamWriter(
                new DataOutputStream(
                        cs.getOutputStream())), true);

    }

    public boolean logare() {
        Scanner sc = new Scanner(System.in);
        scriere.println("Inregistrat? (d/n)");

        if (!"d".equals(sc.next())) {
            return false;
        }

        scriere.println("Nume utilizator > ");

        if ("".equals(sc.next())) {
            return false;
        }

        scriere.println("Parola > ");

        if ("".equals(sc.next())) {
            return false;
        }

        return true;
    }

    // metoda run are comunicarea la client
    @Override
    public synchronized void run() {

        con++;	// numaram fiecare conexiune activa

        // blocul try-catch unde este stabilita conexiunea server-client
        try {
            // se accepta conexiunea de catre server	
            System.out.println("Conexiune " + con + " acceptata : "
                    + cs.getInetAddress() + " : "
                    + cs.getPort());

            // bucla unde se face schimbul de date
            while (true) {

                /*
                    De ajustat niste trimiteri de mesaje pentru comunicare lina.
                 */
                scriere.println(LocalDate.now() + " Server: Cate elemente aveti in contabilitate? ");
                mesaj = intrare.readLine();
                System.out.println("Mesajul :" + mesaj);
                if (mesaj == null) {
                    scriere.println(LocalDate.now() + " Server: Mesajul nul. Oprire! ");
                    break;
                }

                try {
                    Integer m = Integer.parseInt(mesaj);
                    serie_stoc = new Stocuri(m);

                    for (int i = 0; i < m; i++) {
                        scriere.println(
                                "Server: Dati numele stocului > ");
                        
                        mesaj = intrare.readLine();

                        if (mesaj == null) {
                            break;
                        }

                        try {
                            serie_stoc.nume.add(mesaj);
                        } catch (Exception e) {
                            System.out.println("\n Exceptia la denumire : " + e);
                            scriere.println("Server : Exceptia la denumire " + e);
                            cs.close();
                        }
                        
                        scriere.println("Server: Dati pretul stocului > ");
                        
                        mesaj = intrare.readLine();

                        if (mesaj == null) {
                            break;
                        }

                        try {
                            serie_stoc.pret.add(
                                    Double.parseDouble(mesaj));
                        } catch (Exception e) {
                            System.out.println("\n Nu este double (4.15, 5.1, 16.15) sau " + e);
                            scriere.println("Server : Prea multe virgule / puncte sau " + e);
                            cs.close();
                        }
                    }

                    /* de facut datele serializate cu functionala */
                    // apelam metode si campurile obiectului de tip Stocuri

                    serie_stoc.media = serie_stoc.medie(serie_stoc.pret);		
                    serie_stoc.serial(serie_stoc);

                    String[] date;
                    date = serie_stoc.deserial(serie_stoc);

                    scriere.println(date[0] + date[1] + date[2]);
                    
                } catch (Exception e) {
                    scriere.println("Nu ati completat casuta " + e);
                    scriere.println("Eroare de intrare de date : " + e);
                    cs.close();
                    System.out.println("Eroare de intrare de date : " + e);
                }
            }

        } catch (SocketException e) {
            // LocalDate.now() pentru ziua curenta
            System.out.println("\n Conexiune inchisa de clientul " + con);
        } catch (IOException e) {
            System.out.println("\n Exceptia (intrare/iesire) : " + e);
        }

        try {
            cs.close();
        } catch (Exception e) {
            System.out.println("\n Eroare la inchidere : " + e);
        }

        con--;		// scoatem conexiunile terminate
    }
}

public class Stocuri_s {

    public static void main(String[] args) throws Exception {
        Socket cs1;
        Scanner sc = new Scanner(System.in);
        System.out.print("Port : ");

        //System.out.println();
        ServerSocket ss = new ServerSocket(sc.nextInt());

        System.out.println("Serverul este activ la portul "
                + ss.getLocalPort());

        while (true) {
            cs1 = ss.accept();

            System.out.println("Client nou : " + cs1.getInetAddress());

            new Thread(new Conexiune(cs1)).start();
        }

    }
}
