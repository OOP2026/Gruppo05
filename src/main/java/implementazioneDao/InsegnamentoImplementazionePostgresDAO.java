package implementazioneDao;

import dao.InsegnamentoDAO;
import database_connection.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concreta che realizza l'accesso persistente agli insegnamenti didattici
 * all'interno del database relazionale PostgreSQL.
 */
public class InsegnamentoImplementazionePostgresDAO implements InsegnamentoDAO {
    private Connection connection;

    /**
     * Costruttore della classe. Recupera la connessione attiva tramite il Singleton.
     */
    public InsegnamentoImplementazionePostgresDAO() {
        try {
            this.connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserisce un nuovo insegnamento, associandogli i CFU, l'anno e l'ID del docente titolare.
     *
     * @param nome               Nome del corso di insegnamento
     * @param cfu                Numero di crediti formativi attribuiti
     * @param annoCorso          Anno di corso di erogazione (es: II_ANNO)
     * @param idDocenteTitolare  ID identificativo del docente titolare (FK)
     * @throws SQLException Se si verifica un errore durante l'inserimento dei dati
     */
    @Override
    public void inserisciInsegnamentoDB(String nome, int cfu, String annoCorso, int idDocenteTitolare) throws SQLException {
        String query = "INSERT INTO public.\"Insegnamento\" (\"Nome\", \"CFU\", \"AnnoCorso\", \"FK_Docente\") VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setInt(2, cfu);
            ps.setString(3, annoCorso);
            if (idDocenteTitolare == -1) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, idDocenteTitolare);
            }
            ps.executeUpdate();
        }
    }

    /**
     * Recupera tutti gli insegnamenti registrati a database per la sincronizzazione iniziale.
     *
     * @param idInsegnamenti Lista di destinazione per gli ID dei corsi (PK)
     * @param nomi           Lista di destinazione per i nomi dei corsi
     * @param cfuList        Lista di destinazione per i CFU
     * @param anniCorso      Lista di destinazione per gli anni di corso
     * @param idDocenti      Lista di destinazione per gli ID dei docenti titolari (FK)
     * @throws SQLException Se si verifica un errore di lettura
     */
    @Override
    public void leggiInsegnamentiDB(ArrayList<Integer> idInsegnamenti, ArrayList<String> nomi, ArrayList<Integer> cfuList, ArrayList<String> anniCorso, ArrayList<Integer> idDocenti) throws SQLException {
        String query = "SELECT * FROM public.\"Insegnamento\";";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idInsegnamenti.add(rs.getInt("ID_Insegnamento"));
                nomi.add(rs.getString("Nome"));
                cfuList.add(rs.getInt("CFU"));
                anniCorso.add(rs.getString("AnnoCorso"));
                idDocenti.add(rs.getInt("FK_Docente"));
            }
        }
    }
}