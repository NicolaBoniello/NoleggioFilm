package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class User {

    private String name;
    private int saldoAccount;

    static boolean flag = true;
   private static  String DB_URL= "jdbc:mysql://localhost:3306/newdb";

    private static String USER = "root";
    private static String PASSWORD = "Milanista1997";
    Film film;


    public User(){};


    public User(String name){

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSaldoAccount() {
        return saldoAccount;
    }

    public void setSaldoAccount(int saldoAccount) {
        this.saldoAccount = saldoAccount;
    }

    public static void startApp() throws SQLException {

        System.out.println("Benvenuto nell'applicazione del noleggio :");
        System.out.println("Per usufruire dell'applicazione prima però dovrai identificarti oppure registrarti");
        do {
            System.out.println("Seleziona la scelta che vuoi eseguire: ");
            System.out.println("1] Accedi: ");
            System.out.println("2] Registrati: ");
            System.out.println("Inserisci il numero di operazione: ");
            Scanner scan = new Scanner(System.in);
            int input = scan.nextInt();

            switch (input){

                case 1: accedi();
                    break;
                case 2: registrati();
                    break;
                default:
                    System.out.println("operazione non consentita");

            }
        } while (flag);


    }

    public static void accedi() throws SQLException {

        System.out.println("Inserisci la tua email ");
        Scanner scan = new Scanner(System.in);
        String inputA = scan.nextLine();
        System.out.println("Inserisci la password");
        String inputB = scan.nextLine();

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM user WHERE Email = ? AND Password = ?");
        checkUser.setString(1, inputA);
        checkUser.setString(2, inputB);
        ResultSet rs = checkUser.executeQuery();

        if (!rs.next()) {
            System.out.println("Utente non trovato");
            return;
        } else {
            System.out.println("Autenticazione effettuata con successo!");
            menuIniziale();
        }
    }

    public static void menuIniziale() throws SQLException {





            do {

                System.out.println(" \n Seleziona la scelta che vuoi eseguire: ");
                System.out.println("2 ] Ricarica saldo account");
                System.out.println("3 ] Visualizza saldo account");
                System.out.println("4 ] Menu Film");
                System.out.println("5 ] Visualizza storico noleggio ");
                System.out.println("9 ] Esci");
                System.out.println("Inserisci il numero di operazione ");

                int input = new Scanner(System.in).nextInt();

                switch (input){


                    case 2 : ricaricaSaldo();
                        break;
                    case 3 : visualizzaSaldo();
                        break;
                    case 4 : Film film = new Film();
                             film.ricaricaAccount();

                        break;
                    case 5: filmNoleggiati();
                    break;
                    case 9 : esci();
                        break;
                    default :
                        System.out.println("Operazione non esistente");


                }




            }while(flag);



        }











    public static void registrati() throws SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserisci il nome dell'utente: ");
        String name = scanner.nextLine();

        System.out.print("Inserisci l'email: ");
        String email = scanner.nextLine();

        System.out.println("Inserisci la password");
        String password = scanner.nextLine();

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user (name, Email, Password) VALUES (?, ?, ?)"
        );
        statement.setString(1, name);
        statement.setString(2, email);
        statement.setString(3, password);
        statement.executeUpdate();
        statement.close();


        System.out.println("Vuoi aggiungere dei fondi al tuo account adesso?");
        System.out.println("0] Si");
        System.out.println("1] No");

        System.out.println("Inserisci la risposta: ");
        int input = scanner.nextInt();

        if (input == 0){

            System.out.println("Inserisci quanto vuoi ricaricare");
            int saldo = scanner.nextInt();

            Connection connection2 = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement statement2 = connection.prepareStatement(
                    "INSERT INTO user (saldoAccount) VALUES (?)"
            );

            statement2.setInt(1, saldo);
            statement2.executeUpdate();
            statement2.close();
            connection.close();

            } else {

            System.out.println("Hai scelto di non ricaricare ora l'account");
        }

        System.out.println("Account creato con successo");

                
    }

    public static void ricaricaSaldo() throws SQLException {

        Scanner scan = new Scanner(System.in);

        //step 1 : acquisire l'id dell'utente tramite scanner;
        System.out.print("Inserisci l'ID dell'utente");
        int userID = scan.nextInt();

        //step 2 : Verificare se l'ID corrisponde a un utente esistente esistente nel db
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkUserID = connection.prepareStatement("SELECT * FROM user WHERE user_id=?"
        );
        checkUserID.setInt(1, userID);
        ResultSet resultSet = checkUserID.executeQuery();

        if (!resultSet.next()){

            System.out.println("L'utente con l'id " + userID + " non esiste nel database ");
            return;
        }

        //Step 3: Se l'utente esiste, acquisire l'importo della ricarica tramite scanner.
        System.out.println("Inserisci l'importo da ricaricare: ");
        int ricaricaImporto = scan.nextInt();

        //Step 4: Aggiornare il saldo dell'utente nel database sommando l'importo corrente con l'importo della ricarica
        int currenteBalance = resultSet.getInt("saldoAccount");
        int newBalance = currenteBalance + ricaricaImporto;
        PreparedStatement updateBalanceStatement = connection.prepareStatement(
                "UPDATE user SET saldoAccount = ? WHERE user_id = ?"
        );

        updateBalanceStatement.setInt(1,newBalance);
        updateBalanceStatement.setInt(2, userID);
        updateBalanceStatement.executeUpdate();
        System.out.println("Il saldo dell'utente con id " + userID + " è stato ricaricato di " + ricaricaImporto + " euro");





    }

    public static void visualizzaSaldo() throws SQLException {

        Scanner scan = new Scanner(System.in);

        // Step 1 : Acquisire l'id dell'utente tramite scanner
        System.out.println("Inserisci l'id dell'utente");
        int userID = scan.nextInt();

        // Step 2 : Verificare se l'id corrisponde a un utente esistente nel database
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkUserStatment = connection.prepareStatement(
                "SELECT * FROM user WHERE user_id = ?"
        );

        checkUserStatment.setInt(1, userID);
        ResultSet resultSet = checkUserStatment.executeQuery();

        if (!resultSet.next()){

            System.out.println("L'utente con id " + userID + " non esiste nel database ");
            return;
        }

        //Step 3 : Se l'utente esiste, recuperare il suo saldo dal database e mostrarlo a video
        int currentBalance = resultSet.getInt("saldoAccount");
        System.out.println("Il saldo dell'utente con l'id " +userID +  " è di " + currentBalance + " euro");


    };

    public static void filmNoleggiati() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleziona il tuo id: ");
        int input = scanner.nextInt();

        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        PreparedStatement checkUser = conn.prepareStatement("SELECT film.titolo, rental.rental_date, rental.return_date " +
                                                                 "FROM film " +
                                                                 "JOIN rental ON film.film_id = rental.film_id " +
                                                                  "WHERE rental.user_id = ?");

        checkUser.setInt(1, input);
        ResultSet rs = checkUser.executeQuery();

        if (!rs.next()){
            System.out.println("L'utente con id" + input + " non è stato trovato.");
            return;
        }

        PreparedStatement filmNoleggiatiStatement = conn.prepareStatement("SELECT film.titolo, rental.rental_date, rental.return_date " +
                                                                                "FROM film " +
                                                                                "JOIN rental " +
                                                                                "ON film.film_id = rental.film_id " +
                                                                                "WHERE rental.user_id = ?");

        filmNoleggiatiStatement.setInt(1, input);
        ResultSet rs1 = filmNoleggiatiStatement.executeQuery();

        if (!rs1.next()){
            System.out.println("L'utente con id " + input + " non ha noleggiato alcun film");
        }


        System.out.println("I film noleggiati dall'utente con id " + input + " sono: ");
        do {
            String titolo = rs1.getNString("titolo");
            LocalDate rentalDate = rs1.getDate("rental_date").toLocalDate();
            LocalDate returnDate = rs1.getDate("return_date").toLocalDate();
            System.out.println(" - " + titolo + " dal " + rentalDate + " al " + returnDate);

        } while (rs1.next());
    }

    static void esci(){
        System.out.println("Arrivederci!");
        flag = false;

    }


}
