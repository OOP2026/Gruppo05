package dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza delle informazioni degli utenti.
 * <p>
 * Questa interfaccia definisce i "contratti" per le operazioni di login,
 * registrazione e recupero anagrafico su database, gestendo in modo trasparente
 * l'ereditarietà tra la tabella padre e quelle figlie.
 * </p>
 *
 */
public interface UtenteDAO {

    /**
     * Registra un nuovo utente nel database, inserendo in modo transazionale
     * i dati nella tabella padre "Utente" e, successivamente, nella tabella
     * figlia corrispondente ("Studente" o "Docente") in base al ruolo proposto.
     *
     * @param nome      Nome dell'utente
     * @param cognome   Cognome dell'utente
     * @param email     E-mail dell'utente (vincolo UNIQUE)
     * @param login     Username per l'autenticazione (vincolo UNIQUE)
     * @param password  Password associata all'account
     * @param ruolo     Ruolo proposto ('STUDENTE', 'DOCENTE' o 'RESPONSABILE')
     * @param annoCorso Anno di corso frequentato (solo nel caso di ruolo 'STUDENTE')
     * @throws SQLException Se si verifica un errore durante l'esecuzione delle query SQL
     */
    void registraUtenteDB(String nome, String cognome, String email, String login, String password, String ruolo, String annoCorso) throws SQLException;

    /**
     * Verifica la corrispondenza delle credenziali fornite in input sul database.
     *
     * @param login    Username inserito dall'utente
     * @param password Password inserita dall'utente
     * @return Una stringa contenente il ruolo ('STUDENTE', 'DOCENTE', 'RESPONSABILE') se l'autenticazione ha successo, altrimenti {@code null}
     * @throws SQLException Se si verifica un errore durante l'interrogazione del database
     */
    String loginDB(String login, String password) throws SQLException;

    /**
     * Recupera le informazioni anagrafiche e accademiche complete dell'utente
     * loggato, popolando le liste passate come parametro (pattern ad ArrayList di appoggio).
     *
     * @param login          Username dell'utente da ricercare
     * @param idUtente       Lista di destinazione per contenere l'ID utente (PK)
     * @param nome           Lista di destinazione per contenere il nome
     * @param cognome        Lista di destinazione per contenere il cognome
     * @param email          Lista di destinazione per contenere l'e-mail
     * @param matricola      Lista di destinazione per contenere la matricola (solo se studente)
     * @param annoCorso      Lista di destinazione per contenere l'anno di corso (solo se studente)
     * @param isResponsabile Lista di destinazione per contenere il flag amministrativo (solo se docente)
     * @throws SQLException Se si verifica un errore di connessione o di esecuzione SQL
     */
    void leggiDatiUtenteLoggatoDB(String login, ArrayList<Integer> idUtente, ArrayList<String> nome, ArrayList<String> cognome, ArrayList<String> email, ArrayList<String> matricola, ArrayList<String> annoCorso, ArrayList<Boolean> isResponsabile) throws SQLException;

    /**
     * Recupera l'elenco completo di tutti i docenti registrati a sistema
     * per consentirne la selezione e l'assegnamento in fase di inserimento degli insegnamenti.
     *
     * @param idDocenti      Lista di destinazione per gli ID dei docenti
     * @param nomi           Lista di destinazione per i nomi
     * @param cognomi        Lista di destinazione per i cognomi
     * @param email          Lista di destinazione per le email
     * @param logins         Lista di destinazione per gli username
     * @param password       Lista di destinazione per le password
     * @param isResponsabile Lista di destinazione per i flag di coordinamento
     * @throws SQLException Se si verifica un errore nell'interrogazione del database
     */
    void leggiTuttiIDocentiDB(ArrayList<Integer> idDocenti, ArrayList<String> nomi, ArrayList<String> cognomi, ArrayList<String> email, ArrayList<String> logins, ArrayList<String> password, ArrayList<Boolean> isResponsabile) throws SQLException;
}