package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String DB_URL= "jdbc:mysql://localhost:3306/newdb";

    private final String USER = "root";
    private final String PASSWORD = "Milanista1997";

    public String getDB_URL() {
        return DB_URL;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void createTableUser() throws SQLException {

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();

        String queryCreate = ""
                + "CREATE TABLE IF NOT EXISTS user "
                + "(user_id INTEGER(10) NOT NULL AUTO_INCREMENT,"
                + " name VARCHAR(30), " +
                " saldoAccount INTEGER(10), " +
                "CONSTRAINT user_pk PRIMARY KEY (user_id) " +
                "); " ;

        statement.executeUpdate(queryCreate);
        System.out.println("QUery eseguita correttamente, tabella user creata");
        statement.close();

    }

    public void createTableFilm() throws SQLException {

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();

        String queryCreate = ""
                + "CREATE TABLE IF NOT EXISTS film "
                + "(film_id INTEGER(10) NOT NULL AUTO_INCREMENT,"
                + " prezzo INTEGER(10),"
                + " titolo VARCHAR(30), " +
                " categoria ENUM ('Commedia', 'Drammatico', 'Azione', 'Horror'), " +
                "CONSTRAINT film_pk PRIMARY KEY (film_id) " +
                "); " ;

        statement.executeUpdate(queryCreate);
        System.out.println("QUery eseguita correttamente, tabella user creata");
        statement.close();

    }

    public void createTableRental() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement statement = connection.createStatement();

        String queryCreate = ""
                + "CREATE TABLE IF NOT EXISTS rental "
                + "(rental_id INTEGER(10) NOT NULL AUTO_INCREMENT,"
                + " user_id INTEGER(10) NOT NULL,"
                +" film_id INTEGER(10) NOT NULL,"
                + " rental_date DATE,"
                + " return_date DATE, " +
                " CONSTRAINT rental_pk PRIMARY KEY (rental_id), " +
                " CONSTRAINT rental_fk_user FOREIGN KEY (user_id) REFERENCES user(user_id), " +
                " CONSTRAINT rental_fk_film FOREIGN KEY (film_id) REFERENCES film(film_id)" +
                "); " ;

        statement.executeUpdate(queryCreate);
        System.out.println("QUery eseguita correttamente, tabella user creata");
        statement.close();


    }



}
