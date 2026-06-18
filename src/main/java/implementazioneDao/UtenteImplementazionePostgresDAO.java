package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concreta che realizza l'accesso persistente alle informazioni degli utenti
 * all'interno del database relazionale PostgreSQL.
 * <p>
 * Implementa l'interfaccia {@link UtenteDAO} e si occupa di gestire la persistenza
 * dell'ereditarietà (Joined Table) tra la tabella "Utente" e le tabelle collegate
 * "Studente" e "Docente".
 * </p>
 */
public class UtenteImplementazionePostgresDAO implements UtenteDAO {
    private Connection connection;

    /**
     * Costruttore della classe. Inizializza il riferimento alla connessione
     * verso il DBMS PostgreSQL recuperando l'istanza dal Singleton ConnessioneDatabase.
     */
    public UtenteImplementazionePostgresDAO() {
        try {
            this.connection = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Esegue l'autenticazione delle credenziali nel database tramite una query
     * di selezione con i PreparedStatement per prevenire rischi di SQL Injection.
     *
     * @param login    Lo username inserito per l'autenticazione
     * @param password La password associata all'account
     * @return Stringa contenente il ruolo rilevato ('STUDENTE', 'DOCENTE', 'RESPONSABILE') se corretto, altrimenti null
     * @throws SQLException Se si verifica un errore durante l'esecuzione dell'interrogazione SQL
     */
    @Override
    public String loginDB(String login, String password) throws SQLException {
        String query = "SELECT u.\"ID_Utente\", d.\"IsResponsabile\", s.\"ID_Utente\" AS \"IsStudente\" " +
                "FROM public.\"Utente\" u " +
                "LEFT JOIN public.\"Docente\" d ON u.\"ID_Utente\" = d.\"ID_Utente\" " +
                "LEFT JOIN public.\"Studente\" s ON u.\"ID_Utente\" = s.\"ID_Utente\" " +
                "WHERE u.\"Login\" = ? AND u.\"Password\" = ?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getObject("IsStudente") != null) {
                        return "STUDENTE";
                    } else if (rs.getBoolean("IsResponsabile")) {
                        return "RESPONSABILE";
                    } else {
                        return "DOCENTE";
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recupera le informazioni anagrafiche e accademiche complete dell'utente loggato
     * eseguendo una JOIN tra la tabella Utente e le relative tabelle figlie.
     *
     * @param login          Username dell'utente da ricercare
     * @param idUtente       Lista di destinazione per la chiave primaria
     * @param nome           Lista di destinazione per il nome
     * @param cognome        Lista di destinazione per il cognome
     * @param email          Lista di destinazione per l'email
     * @param matricola      Lista di destinazione per la matricola
     * @param annoCorso      Lista di destinazione per l'anno di corso
     * @param isResponsabile Lista di destinazione per il flag amministrativo
     * @throws SQLException Se si verifica un errore durante l'esecuzione SQL
     */
    @Override
    public void leggiDatiUtenteLoggatoDB(String login, ArrayList<Integer> idUtente, ArrayList<String> nome, ArrayList<String> cognome, ArrayList<String> email, ArrayList<String> matricola, ArrayList<String> annoCorso, ArrayList<Boolean> isResponsabile) throws SQLException {
        String query = "SELECT u.*, s.\"Matricola\", s.\"AnnoCorso\", d.\"IsResponsabile\" " +
                "FROM public.\"Utente\" u " +
                "LEFT JOIN public.\"Studente\" s ON u.\"ID_Utente\" = s.\"ID_Utente\" " +
                "LEFT JOIN public.\"Docente\" d ON u.\"ID_Utente\" = d.\"ID_Utente\" " +
                "WHERE u.\"Login\" = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idUtente.add(rs.getInt("ID_Utente"));
                    nome.add(rs.getString("Nome"));
                    cognome.add(rs.getString("Cognome"));
                    email.add(rs.getString("Email"));

                    String matr = rs.getString("Matricola");
                    matricola.add(matr != null ? matr : "");

                    String anno = rs.getString("AnnoCorso");
                    annoCorso.add(anno != null ? anno : "");

                    isResponsabile.add(rs.getBoolean("IsResponsabile"));
                }
            }
        }
    }

    /**
     * Recupera l'elenco completo di tutti i docenti inseriti a database
     * eseguendo una JOIN tra Utente e Docente.
     *
     * @param idDocenti      Lista di destinazione per l'ID dei docenti
     * @param nomi           Lista di destinazione per i nomi
     * @param cognomi        Lista di destinazione per i cognomi
     * @param email          Lista di destinazione per le email
     * @param logins         Lista di destinazione per gli username
     * @param password       Lista di destinazione per le password
     * @param isResponsabile Lista di destinazione per i flag amministrativi
     * @throws SQLException Se si verifica un errore nell'interrogazione del database
     */
    @Override
    public void leggiTuttiIDocentiDB(ArrayList<Integer> idDocenti, ArrayList<String> nomi, ArrayList<String> cognomi, ArrayList<String> email, ArrayList<String> logins, ArrayList<String> password, ArrayList<Boolean> isResponsabile) throws SQLException {
        String query = "SELECT u.*, d.\"IsResponsabile\" FROM public.\"Utente\" u JOIN public.\"Docente\" d ON u.\"ID_Utente\" = d.\"ID_Utente\";";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idDocenti.add(rs.getInt("ID_Utente"));
                nomi.add(rs.getString("Nome"));
                cognomi.add(rs.getString("Cognome"));
                email.add(rs.getString("Email"));
                logins.add(rs.getString("Login"));
                password.add(rs.getString("Password"));
                isResponsabile.add(rs.getBoolean("IsResponsabile"));
            }
        }
    }

    /**
     * Registra un nuovo utente. Esegue l'inserimento in modo transazionale prima sulla
     * tabella padre e poi, tramite la chiave generata, sulla tabella figlia corrispondente.
     *
     * @param nome      Nome dell'utente
     * @param cognome   Cognome dell'utente
     * @param email     E-mail dell'utente
     * @param login     Username per l'autenticazione
     * @param password  Password associata
     * @param ruolo     Il ruolo dell'account ('STUDENTE', 'DOCENTE', 'RESPONSABILE')
     * @param annoCorso L'anno di corso (solo per lo studente)
     * @throws SQLException Se si verifica un errore durante l'inserimento o la transazione SQL
     */
    @Override
    public void registraUtenteDB(String nome, String cognome, String email, String login, String password, String ruolo, String annoCorso) throws SQLException {
        String queryUtente = "INSERT INTO public.\"Utente\" (\"Nome\", \"Cognome\", \"Email\", \"Login\", \"Password\") VALUES (?, ?, ?, ?, ?) RETURNING \"ID_Utente\";";

        try (PreparedStatement psUtente = connection.prepareStatement(queryUtente)) {
            psUtente.setString(1, nome);
            psUtente.setString(2, cognome);
            psUtente.setString(3, email);
            psUtente.setString(4, login);
            psUtente.setString(5, password);

            try (ResultSet rs = psUtente.executeQuery()) {
                if (rs.next()) {
                    int idUtente = rs.getInt("ID_Utente");

                    if (ruolo.equalsIgnoreCase("STUDENTE")) {
                        String queryStudente = "INSERT INTO public.\"Studente\" (\"ID_Utente\", \"AnnoCorso\") VALUES (?, ?);";
                        try (PreparedStatement psStudente = connection.prepareStatement(queryStudente)) {
                            psStudente.setInt(1, idUtente);
                            psStudente.setString(2, annoCorso);
                            psStudente.executeUpdate();
                        }
                    } else {
                        boolean isResp = ruolo.equalsIgnoreCase("RESPONSABILE");
                        String queryDocente = "INSERT INTO public.\"Docente\" (\"ID_Utente\", \"IsResponsabile\") VALUES (?, ?);";
                        try (PreparedStatement psDocente = connection.prepareStatement(queryDocente)) {
                            psDocente.setInt(1, idUtente);
                            psDocente.setBoolean(2, isResp);
                            psDocente.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}