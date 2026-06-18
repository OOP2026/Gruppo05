package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe di utilità finalizzata alla gestione centralizzata e thread-safe
 * della connessione JDBC verso il DBMS PostgreSQL.
 * <p>
 * Implementa il pattern creazionale <b>Singleton</b> al fine di garantire l'esistenza
 * di una sola istanza di connessione attiva per l'intera durata dell'applicazione,
 * ottimizzando l'occupazione di memoria e prevenendo anomalie di concorrenza.
 * </p>
 */
public class ConnessioneDatabase {

    /**
     * L'unica istanza statica della classe ConnessioneDatabase (come da Pattern Singleton
     */
    private static ConnessioneDatabase instance;


    public Connection connection = null;


    private String nome = "postgres";


    private String password = "unina";


    private String url = "jdbc:postgresql://localhost:5432/OrarioLezioniDB";


    private String driver = "org.postgresql.Driver";

    /**
     * Costruttore privato che impedisce l'istanziazione diretta della classe da parte
     * di altri componenti del software.
     * <p>
     * Si occupa di caricare dinamicamente in memoria il driver JDBC e di stabilire
     * la connessione fisica con i parametri predefiniti di login.
     * </p>
     *
     * @throws SQLException Se si verifica un errore durante il tentativo di connessione al database
     */
    private ConnessioneDatabase() throws SQLException {
        try {
            Class.forName(driver); // Caricamento dinamico del driver a runtime
            this.connection = DriverManager.getConnection(url, nome, password);
        } catch (ClassNotFoundException ex) {
            System.err.println("Errore: Driver PostgreSQL non trovato nel progetto.");
            ex.printStackTrace();
        }
    }

    /**
     * Fornisce il punto di accesso globale e univoco all'istanza della connessione.
     * <p>
     * Se l'istanza è nulla (primo avvio) o se la connessione è stata precedentemente chiusa,
     * provvede a istanziare un nuovo oggetto ConnessioneDatabase aprendo una nuova sessione.
     * </p>
     *
     * @return L'unica istanza attiva di ConnessioneDatabase
     * @throws SQLException Se si verifica un errore durante l'ispezione dello stato della connessione
     */
    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnessioneDatabase();
        } else if (instance.connection.isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }


    public Connection getConnection() {
        return connection;
    }
}