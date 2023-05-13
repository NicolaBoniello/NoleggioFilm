package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Film {


    static boolean flag = true;

    private final String DB_URL= "jdbc:mysql://localhost:3306/newdb";

    private final String USER = "root";
    private final String PASSWORD = "Milanista1997";
    public void ricaricaAccount() throws SQLException {






        do {

            System.out.println("Benvenuto nel menu film");
            System.out.println(" \n Seleziona la scelta che vuoi eseguire: ");
            System.out.println("1 ] Visualizza la lista completa dei film da noleggiare");
            System.out.println("2 ] Visualizza le informazioni del costo del noleggio");
            System.out.println("3 ] Info penale");
            System.out.println("4 ] Stampa fattura");
            System.out.println("9] Esci");
            System.out.println("Inserisci il numero di operazione ");

            int input = new Scanner(System.in).nextInt();

            switch (input){

                case 1 : visualizzaFilm();
                break;
                case 2 : informazioniNoleggio();
                break;
                case 3: informazioniPenale();
                break;
                case 4: stampaFattura();
                break;
                case 9: exit();
                break;


            }




        }while(flag);



    }

    public void visualizzaFilm() throws SQLException {

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();

        String query = "Select * From film";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()){

            int id = resultSet.getInt("film_id");
            String titolo = resultSet.getNString("titolo");
            String categoria = resultSet.getNString("categoria");
            int quantità = resultSet.getInt("quantità");

            System.out.println("ID: " + id);
            System.out.println("titolo: " + titolo);
            System.out.println("categoria: " + categoria);
            System.out.println("quantità: " + quantità);
            System.out.println();

            }
        statement.close();
        connection.close();
    }

    public void informazioniNoleggio() throws SQLException {

        System.out.println("Digita il nome del film per verificarne la disponibilità ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        System.out.println("Digita il nome della categoria");
        String input2 = scan.nextLine();

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkNameAndCategory = connection.prepareStatement("SELECT * FROM film WHERE titolo=? AND categoria =?    "
        );


        checkNameAndCategory.setString(1, input);
        checkNameAndCategory.setString(2, input2);


        ResultSet resultSet = checkNameAndCategory.executeQuery();



        PreparedStatement checkQuantità = connection.prepareStatement("SELECT Quantità FROM film WHERE titolo = ?");
        checkQuantità.setString(1, input);

        ResultSet rs = checkQuantità.executeQuery();


        if (resultSet.next()){

            System.out.println("Titolo inserito correttamente");

       } else {
            int prezzo = resultSet.getInt("prezzo");
            System.out.println("è disponibile");
            System.out.println("Il prezzo per noleggiare il film " + input + " è di " + prezzo  + " euro al giorno");
            System.out.println("vuoi noleggiarlo?");
        }

        if (!rs.next() || rs.getInt("Quantità") <= 0) {
            System.out.println("Ma il  film non è disponibile");
            return;

        }

        do {
            System.out.println("1] per proceder con il noleggio;");
            System.out.println("2] per uscire!");

            int inputUser = new Scanner(System.in).nextInt();

            switch (inputUser){

                case 1: noleggio();
                break;
                case 2: exit();
                break;

            }

        } while(flag);









    }

    public static void exit(){
        System.out.println("Arrivederci!");
        flag = false;

    }

    public void noleggio() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci il titolo del film da noleggiare:");
        String titolo = scan.nextLine();

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkFilm = connection.prepareStatement("SELECT * FROM film WHERE titolo = ?  ");
        checkFilm.setString(1, titolo);
        ResultSet rsFilm = checkFilm.executeQuery();


        if (!rsFilm.next() ) {
            System.out.println("Il film non è disponibile");
            return;
        }



        int idFilm = rsFilm.getInt("film_id");
        int prezzoNoleggio = rsFilm.getInt("prezzo");

        System.out.println("Inserisci l'id dell'utente che effettua il noleggio");
        int idUser = scan.nextInt();

        PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM user WHERE user_id = ?");
        checkUser.setInt(1, idUser);
        ResultSet rsUser = checkUser.executeQuery();

        if (!rsUser.next()) {
            System.out.println("Utente non trovato");
            return;
        }

        int saldo = rsUser.getInt("saldoAccount");

        if (saldo < prezzoNoleggio) {
            System.out.println("Saldo insufficiente per effettuare il noleggio");
            return;
        }

        LocalDate dataNoleggio = LocalDate.now();
        LocalDate scadenzaNoleggio = dataNoleggio.plusDays(7);

        PreparedStatement noleggioStatement = connection.prepareStatement(
                "INSERT INTO rental(user_id, film_id, rental_date, return_date) VALUES (?, ?, ?, ?)");
        noleggioStatement.setInt(1, idUser);
        noleggioStatement.setInt(2, idFilm);
        noleggioStatement.setDate(3, java.sql.Date.valueOf(dataNoleggio));
        noleggioStatement.setDate(4, java.sql.Date.valueOf(scadenzaNoleggio));
        noleggioStatement.executeUpdate();

        System.out.println("Noleggio effettuato con successo");

        PreparedStatement updateQuantita = connection.prepareStatement(
                "UPDATE film SET quantità = quantità - 1 WHERE film_id = ?");
        updateQuantita.setInt(1, idFilm);
        updateQuantita.executeUpdate();

        PreparedStatement updateSaldo = connection.prepareStatement(
                "UPDATE user SET saldoAccount = saldoAccount - ? WHERE user_id = ?");
        updateSaldo.setInt(1, prezzoNoleggio * 7);
        updateSaldo.setInt(2, idUser);
        updateSaldo.executeUpdate();
    }

    public void informazioniPenale(){
        System.out.println("Benvenuto nella pagina per le informazioni sulla penale: ");
        System.out.println("Quando si noleggia un film viene registrata in automatico la data di inizio del noleggio " +
                           " e settata la data di fine noleggio a 7 giorni dall'inizio del noleggio.");
        System.out.println("Qualora la riconsegna del film non dovesse avvenire dopo 7 giorni, viene applicata una "+
                           "penale base per ogni giorni di ritardo. ");
        System.out.println("il valore della penale  va in base alla categoria di film scelto");


    };

    public void stampaFattura() throws SQLException {

        System.out.println("Inserisci il tuo id ");
        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM user WHERE user_id = ?");
        checkUser.setInt(1, input);
        ResultSet rs =  checkUser.executeQuery();

        if (!rs.next()){
            System.out.println("Utente non trovato!");
            return;
        }




    };


}
