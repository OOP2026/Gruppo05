package implementazioneDao;

import dao.LezioneDAO;
import database_connection.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concreta che realizza l'accesso persistente alle lezioni calendarizzate
 * all'interno del database relazionale PostgreSQL.
 */
public class LezioneImplementazionePostgresDAO implements LezioneDAO {
    private Connection connection;

    /**
     * Costruttore della classe. Ottiene la connessione mediante il Singleton ConnessioneDatabase.
     */
    public LezioneImplementazionePostgresDAO() {
        try {
            this.connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedula e registra una nuova lezione all'interno del database relazionale.
     *
     * @param idInsegnamento ID del corso associato (FK)
     * @param giorno         Giorno della settimana selezionato
     * @param oraInizio      Orario di inizio in formato hh:mm
     * @param oraFine        Orario di fine in formato hh:mm
     * @param idAula         ID dell'aula fisica associata (FK)
     * @throws SQLException Se si verificano conflitti fisici o violazioni dei vincoli referenziali
     */
    @Override
    public void inserisciLezioneDB(int idInsegnamento, String giorno, String oraInizio, String oraFine, int idAula) throws SQLException {
        String query = "INSERT INTO public.\"Lezione\" (\"FK_Insegnamento\", \"Giorno\", \"OraInizio\", \"OraFine\", \"FK_Aula\") VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idInsegnamento);
            ps.setString(2, giorno);
            ps.setTime(3, Time.valueOf(oraInizio + ":00"));
            ps.setTime(4, Time.valueOf(oraFine + ":00"));
            ps.setInt(5, idAula);
            ps.executeUpdate();
        }
    }

    /**
     * Recupera l'elenco complessivo delle lezioni inserite nel database.
     *
     * @param idLezioni      Lista di destinazione per le PK delle lezioni
     * @param idInsegnamenti Lista di destinazione per le FK dei corsi
     * @param giorni         Lista di destinazione per i giorni della settimana
     * @param oreInizio      Lista di destinazione per gli orari di inizio (formato hh:mm)
     * @param oreFine        Lista di destinazione per gli orari di fine (formato hh:mm)
     * @param idAule         Lista di destinazione per le FK delle aule
     * @throws SQLException Se si verifica un errore durante l'esecuzione del SELECT
     */
    @Override
    public void leggiLezioniDB(ArrayList<Integer> idLezioni, ArrayList<Integer> idInsegnamenti, ArrayList<String> giorni, ArrayList<String> oreInizio, ArrayList<String> oreFine, ArrayList<Integer> idAule) throws SQLException {
        String query = "SELECT * FROM public.\"Lezione\";";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idLezioni.add(rs.getInt("ID_Lezione"));
                idInsegnamenti.add(rs.getInt("FK_Insegnamento"));
                giorni.add(rs.getString("Giorno"));

                // Formatta l'orario TIME stringandolo senza i secondi
                oreInizio.add(rs.getTime("OraInizio").toString().substring(0, 5));
                oreFine.add(rs.getTime("OraFine").toString().substring(0, 5));

                idAule.add(rs.getInt("FK_Aula"));
            }
        }
    }

    /**
     * Aggiorna i dati temporali e logistici di una lezione a seguito di un'approvazione.
     *
     * @param idLezione Identificativo della lezione da modificare (PK)
     * @param giorno    Nuovo giorno di svolgimento
     * @param oraInizio Nuovo orario di inizio (formato hh:mm)
     * @param oraFine   Nuovo orario di fine (formato hh:mm)
     * @throws SQLException Se si verifica un errore durante l'aggiornamento SQL del record
     */
    @Override
    public void aggiornaOrarioLezioneDB(int idLezione, String giorno, String oraInizio, String oraFine) throws SQLException {
        String query = "UPDATE public.\"Lezione\" SET \"Giorno\" = ?, \"OraInizio\" = ?, \"OraFine\" = ? WHERE \"ID_Lezione\" = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, giorno);
            ps.setTime(2, Time.valueOf(oraInizio + ":00"));
            ps.setTime(3, Time.valueOf(oraFine + ":00"));
            ps.setInt(4, idLezione);
            ps.executeUpdate();
        }
    }
}