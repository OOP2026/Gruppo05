package implementazioneDao;

import dao.AulaDAO;
import database_connection.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concreta che realizza l'accesso persistente alle aule dell'ateneo
 * all'interno del database relazionale PostgreSQL.
 */
public class AulaImplementazionePostgresDAO implements AulaDAO {
    private Connection connection;

    /**
     * Costruttore della classe. Recupera il riferimento alla connessione
     * attiva dal Singleton ConnessioneDatabase.
     */
    public AulaImplementazionePostgresDAO() {
        try {
            this.connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Esegue l'inserimento SQL di una nuova aula all'interno del database.
     *
     * @param nome Nome identificativo dell'aula
     * @throws SQLException Se si verifica una violazione del vincolo UNIQUE sul nome o errori SQL
     */
    @Override
    public void inserisciAulaDB(String nome) throws SQLException {
        String query = "INSERT INTO public.\"Aula\" (\"Nome\") VALUES (?);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        }
    }

    /**
     * Esegue la query di selezione per recuperare tutte le aule presenti a database.
     *
     * @param idAule    Lista di destinazione per gli identificativi delle aule (PK)
     * @param nomiAule  Lista di destinazione per i nomi delle aule
     * @throws SQLException Se si verifica un errore durante l'esecuzione del SELECT SQL
     */
    @Override
    public void leggiAuleDB(ArrayList<Integer> idAule, ArrayList<String> nomiAule) throws SQLException {
        String query = "SELECT * FROM public.\"Aula\";";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idAule.add(rs.getInt("ID_Aula"));
                nomiAule.add(rs.getString("Nome"));
            }
        }
    }
}