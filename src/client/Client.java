package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;


/*
 *
 * @author Ungureanu Daniel-Robert
    Clasa client are nevoie de date pentru conectare la server.
    In primul rand vom avea adresa_ip, dupa portul asignat la alegere.
    Formatul acestora va fi adresa_ip:port la momentul introducerii.
 */
class Client implements Runnable {

    // adresa ip, poate fi localhost, 127.0.0.1 pentru local
    String adr;

    /*
        Fiecare conexiune la o adresa are nevoie de un port.
        Portul este un numar intreg, acesta nu poate avea valoarea negativa
        Valorile portului se incadreaza intre 1 si 65535, adica 2^15.
     */
    int port;

    /*
       Socket-ul este obiectul care permite conexiunea dintre client si server.
       Clientul este un program care cere anumite date in comunicarea cu server.
       Obiectul socket se poate initializa in mai multe moduri.
       In mod normal cei doi parametrii sunt adresa in format sir de caractere 
      si portul in format numar intreg.
       Ar fi o imbunatatire ca numarul intreg sa fie unsigned deoarece nu ar 
    avea nevoie de numerele negative cu mai multi parametrii de tratat in
    exceptiile care vin din alegerea unui port negativ.
       Unsigned integer ca tip de date evita aceste probleme si exceptii de 
    genul introducerii gresite a datelor.
       Initial poate fi si nul adica datele lui sunt vide.
     */
    Socket socket;

    // 4) Mesajul catre server se initializeaza ca sir de caractere.
    // Un sir de caractere ca date este format dintr-o secventa imutabila.
    // Aceasta secventa imutabila va fi sigura deoarece nu se poate modifica.
    // Uneori se poate ca mesajul receptionat sa nu fie in intregime transmis.
    // Conexiunea cu socket tcp/ip poate face o reparatie a mesajului.    
    String cerere;

    // 5) camp, mesajul de la server
    String mesaj_server = "";

    // 6) _s - server, 7)_t - tastatura
    BufferedReader intrare_s, intrare_t;

    // 8) iesire - afisare, 9) fisier_t - se scrie in fisier
    PrintWriter iesire, fisier_t;

    boolean transfer = false;

    /*
        Reprezentarea constructorilor pe clase
        Aici am reprezentat diferite functii constructor.
        Constructorii in java sunt in mare parte diferentiati de parametrii.
        Parametrii diferiti se refera la datele care vor fi adaugate in campuri.
        Campurile vor fi modificate in functie de constructorul potrivit.
        Constructorul potrivit reprezinta nevoia utilizatorului de a accesa 
       clasele 
     */
 /*
        Constructor fara parametrii, obiectul initializat nu va fi vid,
       dar campurile nu vor avea date, putand fi modificate ulterior.
     */
    Client() {
    }

    /*
        Constructorul pentru client necesita o adresa ip formatata in sir de 
       caractere, si un port formatat ca numar intreg.
        In cazul unui port, numarul intreg se afla intre valorile 1 si 65535.
        Acest interval este de numere naturale, deci portul ca numar intreg
       poate fi doar pozitiv.
     */
    Client(String _adr, int _port) {
        adr = _adr;

        /*
            Aici am facut posibilitatea prescurtarii denumirii adresei locale,
           normal aceasta ca valoarea unui tip de sir de caractere este scrisa
           localhost, pentru cazuri de testare putem permite introducerea pre-
           scurtata a acesteia pentru a reduce timpul de efectuare a testarilor
           manuale pentru a verifica functionalitatea progamului la conectarea
           catre server.
         */
        if (_adr.contains("loc") || _adr.contains("lh")
                || _adr.contains("local")) {
            adr = "localhost";
        }

        /*
            Verificam in primul rand daca portul este pozitiv.
            Portul fiind un numar intreg pozitiv, incadrandu-se in multimea
            numerelor naturale, verificam daca este pana-n valoarea 65535.
            Aici am verificat daca este strict mai mic decat 65536, portul a-
            vand valoarea maxima 65535.
            Numarul 65536 nu este luat in considerare, fiind ca un interval des-
            chis intre 1 si 65536.
         */
        if (!(port > 0) && !(port < 65536)) {
            return;
        }
        port = _port;

    }
    
    Client(Socket socket) {
        this.socket = socket;
    }

    /*
        Avem un bloc de prindere a exceptiilor
        Se incearca stabilirea conexiunii dintre client si server.
        Cererea de conexiune porneste de la programul client caruia i se 
        dau datele despre adresa ip si portul respectiv.
        Daca se reuseste instructiunea de reinitializare a obiectului de 
        tip socket cu adresa in format sir de caractere, si portul in format
        numar intreg, urmeaza o afisare.
        Afisarea ii spune celui care ruleaza programul ca stabilirea cone-
        xiunii a fost stabilita si clientul este conectat la server.
        Dovada ca este intr-adevar conectat la server se face prin functia
        socket.getInetAddress() care reda adresa data de server la clientul
        care s-a conectat cu succes, se afiseaza si portul cu functia 
        socket.getPort(), iar ambele functii fac parte din obiectul socket.
     */

 /**/
    // Accesare camp al clasei dpdv 
    public void setSocket(String adresa, int _port) {
        try {
            if (("".equals(adresa) || _port == 0) || ("".equals(adresa) && _port == 0)) {
                //System.out.println("Nu s-a stabilit conexiunea.");
                return;
            }
            if (adresa.contains("loc") || adresa.contains("lh")
                    || adresa.contains("local")) {
                adresa = "localhost";
            }
            if (!(port > 0) && !(port < 65536)) {
                return;
            }
            socket = new Socket(adresa,_port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    protected Socket getSocket() {
        return this.socket;
    }

    public boolean getTransfer() {
        return transfer;
    }

    public static boolean getTransfer(Client c) {
        return c.transfer;
    }

    /*
        Aici am utilizat functia run() din obiectul care reprezinta firele de 
        executie a unui program. Thread-urile reprezinta o executie separata de
        secvente de cod care pot continua in paralel cu instructiunile care se
        afla pe rularea functiei principale.
        Thread porneste secventele de cod in mod controlat, resursele fiind alo-
        cate in timpi aleatori.
        Concurenta in cadrul rularii mai multor clienti in acelasi timp da nis-
        te rezultate mai deosebite.
     */
    @Override
    public synchronized void run() {
        /*
            Conexiunea stabilita cu succes inseamna posibilitatea de comunicare
           intre server si client.
            In comunicarea dintre client si server, cel ce este clientul sau
           reprezentantul cererii va fi cel care trimite un mesaj catre server
           pentru a putea obtine datele necesare din procesul de stocuri.
            Mesajul trimis de client este reprezentat de obiectul PrintWriter 
           care la randul lui preia mesajul clientul in forma unui vector de 
           caractere. 
            Obiectul de tip String este o data primitiva imutabila,
           totodata un vector de caractere poate fi modificat si accesat carac-
           ter cu caracter, ceea ce determina ca in utilizarea dintre cele doua
           String poate fi mai solid in transmiterea intentionata a unui mesaj.
            Vectorul de caractere poate fi restabilit, adica la primirea unui 
           vector incomplet de caractere, se poate determina o anume insemnatate
           din receptionarea partiala a mesajului.
            Urmatorul bloc try-catch este incercarea de a stabili un mesaj de la
           client catre server prin obiectele de tip PrintWriter.
            Primul obiect este fisier_t care scrie date intr-un fisier. 
            Al doilea obiect intrare_t reprezinta mesajul preluat de la tastatu-
           ra de catre utilizator care va fi trimis catre server.
            Al treilea obiect intrare_s reprezinta mesajul care va fi receptio-
           nat de la server prin intermediul metodei getInputStream().
            Metoda getInputStream() preia mesajul ca sir de caractere care va
           fi afisat.
            Al patrulea obiect este iesire de tip PrintWriter care va capta 
           mesajul introdus de utilizator si va transmite ceea ce a introdus 
           utilizatorul de la obiectul intrare_t catre server prin metoda 
           getOutputStream() a obiectului de tip socket.
         */

        // try-catch ca programul sa nu redea erori mari
        try {
            setSocket(adr, port);
            System.out.println("Conectat la serverul "
                    + socket.getInetAddress()
                    + ":" + socket.getPort());
            // fisier_t pentru citirea din fisier
            fisier_t = new PrintWriter(new FileWriter("date.txt"));

            // intrare_t pentru citirea de la tastatura
            intrare_t = new BufferedReader(new InputStreamReader(
                    System.in));

            // intrare_s pentru citirea raspunsului de la server
            intrare_s = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            // iesire pentru trimiterea cererii catre server
            iesire = new PrintWriter(socket.getOutputStream(), true);

            // comunicarea cu serverul 
            transfer = (socket != null);

            // bucla atata timp cat serverul n-a raspuns
            while (transfer) {
                // daca mesajul este vid atunci se opreste comunicarea
                if (intrare_s == null) {
                    transfer = false;
                }

                // se citeste mesajul de la server
                mesaj_server = intrare_s.readLine();

                // se afiseaza in consola mesajul de la server
                if (mesaj_server == null) {
                    System.out.println("Nu mai ramane mesaj de la server.");

                    transfer = false;
                    break;
                }

                System.out.println("Server: " + mesaj_server);
                System.out.println();

                // se intrerupe daca serverul trasmite ceva continand erori
                if (mesaj_server != null && mesaj_server.contains("xcept")) {
                    System.out.println("Server: " + mesaj_server);

                    transfer = false;
                    break;
                }

                // se citeste cererea care va fi transmisa catre server
                cerere = intrare_t.readLine();

                // daca nu se transmite o cerere, intrerupe comunicarea
                if (cerere == null) {
                    transfer = false;
                    break;
                }

                // se face transmiterea cererii catre server
                iesire.println(cerere);

                // Clientul opreste comunicarea cand ajunge la un rezultat
                //transfer = false;
            }
        } catch (SocketException e) {
            // Se prinde o exceptie cand facem oprirea buclei
            // Aceasta exceptie se intampla deoarece serverul initiaza 
            // intreruperea comunicatiei o data ce s-a terminat transferul
            // 
            System.out.println("\n S-a terminat transferul de date. \n");
            
        } catch (IOException e1) {
            try {
                socket.close();
            } catch (NumberFormatException nfe) {
                System.out.println("Nu introduceti numarul gresit : " + nfe);
                nfe.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            // Exceptia cand la un moment dat se pierd datele si 
            System.out.println("Exceptia 1 "
                    + "(inchis de la erori de comunicatie) : " + e1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            socket.close();
            System.out.println("\n =========================== \n + "
                    + "S-a oprit transferul! \n =========================== \n");
            System.out.println();
        } catch (SocketException e) {
            System.out.println("\n S-a intrerupt comunicatia. \n");
        } catch (Exception e) {
            System.out.println("La inchidere comunicatiei : " + e);
            e.printStackTrace();
        }
    }

}
