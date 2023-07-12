package client;

import java.util.Scanner;

class Stocuri_c {

    private static Client client = new Client();

    public static void main(String args[]) {

        /*
            Aici am utilizat o metoda care preia starea de transfer de date
            dintre server si client. Campul datei boolene din clasa nu se poate
            accesa direct, fiind privat si static, de aceea se utilizeaza o me-
            toda pentru a reda valoarea campului, aceasta este apelata cu o ne-
            gatie adaugata pentru a apela functia de conectare doar pentru cand
            respectivul client pierde conexiunea sau doreste sa se deconecteze 
            la un moment dat si sa se reconecteze mai tarziu.
         */
        while (!client.getTransfer()) {

            // se incearca o mica asteptare inainte sa reinceapa reconectarea
            // daca se iveste o problema, afisez
            try {

                Scanner sc = new Scanner(System.in);

                System.out.print("Adresa server si port : ");

                client.setSocket(sc.next(), sc.nextInt());

                new Thread(client).start();

                Thread.sleep(250);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
