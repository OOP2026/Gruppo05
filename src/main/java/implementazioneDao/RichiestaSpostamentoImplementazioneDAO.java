package implementazioneDao;

import dao.RichiestaSpostamentoDAO;
import database_connection.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concreta che realizza l'accesso persistente alle richieste di spostamento
 * all'interno del database relazionale PostgreSQL.
 */
public class RichiestaSpostamentoImplementazioneDAO implements RichiestaSpostamentoDAO {
    private Connection connection;

    /**
     * Costruttore della classe. Recupera il riferimento alla connessione mediante il Singleton.
     */
    public RichiestaSpostamentoImplementazioneDAO() {
        try {
            this.connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Memorizza una nuova proposta di spostamento oraria compilata da un docente.
     *
     * @param idLezione          Identificativo della lezione originaria (FK)
     * @param giornoProposto     Giorno della settimana proposto per la modifica
     * @param oraInizioProposta  Orario di inizio proposto (formato hh:mm)
     * @param oraFineProposta    Orario di fine proposto (formato hh:mm)
     * @throws SQLException Se si verifica un errore durante l'esecuzione dell'INSERT SQL
     */
    @Override
    public void inviaRichiestaDB(int idLezione, String giornoProposto, String oraInizioProposta, String oraFineProposta) throws SQLException {
        String query = "INSERT INTO public.\"RichiestaSpostamento\" (\"FK_Lezione\", \"GiornoProposto\", \"OraInizioProposta\", \"OraFineProposta\") VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idLezione);
            ps.setString(2, giornoProposto);
            ps.setTime(3, Time.valueOf(oraInizioProposta + ":00"));
            ps.setTime(4, Time.valueOf(oraFineProposta + ":00"));
            ps.executeUpdate();
        }
    }

    /**
     * Aggiorna lo stato di valutazione di una richiesta di spostamento.
     *
     * @param idRichiesta Identificativo univoco della richiesta (PK)
     * @param nuovoStato  Nuovo stato del ticket ('APPROVATA' o 'RIFIUTATA')
     * @throws SQLException Se si verifica un errore di esecuzione dell'UPDATE SQL
     */
    @Override
    public void valutaRichiestaDB(int idRichiesta, String nuovoStato) throws SQLException {
        String query = "UPDATE public.\"RichiestaSpostamento\" SET \"Stato\" = ? WHERE \"ID_Richiesta\" = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nuovoStato);
            ps.setInt(2, idRichiesta);
            ps.executeUpdate();
        }
    }

    /**
     * Recupera l'elenco di tutte le richieste di spostamento memorizzate a database.
     *
     * @param idRichieste   Lista di destinazione per le PK delle richieste
     * @param idLezioni     Lista di destinazione per le FK delle lezioni
     * @param giorniProp    Lista di destinazione per i giorni proposti
     * @param oreInizioProp Lista di destinazione per le ore di inizio proposte
     * @param oreFineProp   Lista di destinazione per le ore di fine proposte
     * @param stati         Lista di destinazione per gli stati dei ticket (IN_ATTESA, ...)
     * @throws SQLException Se si verifica un errore in fase di lettura dati
     */
    @Override
    public void leggiRichiesteDB(ArrayList<Integer> idRichieste, ArrayList<Integer> idLezioni, ArrayList<String> giorniProp, ArrayList<String> oreInizioProp, ArrayList<String> oreFineProp, ArrayList<String> stati) throws SQLException {
        String query = "SELECT * FROM public.\"RichiestaSpostamento\";";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idRichieste.add(rs.getInt("ID_Richiesta"));
                idLezioni.add(rs.getInt("FK_Lezione"));
                giorniProp.add(rs.getString("GiornoProposto"));

                oreInizioProp.add(rs.getTime("OraInizioProposta").toString().substring(0, 5));
                oreFineProp.add(rs.getTime("OraFineProposta").toString().substring(0, 5));

                stati.add(rs.getString("Stato"));
            }
        }
    }
}