package model;
/**
 * Classe astratta che definisce la struttura anagrafica e le credenziali
 * di accesso comuni a tutti gli utenti che operano nell'ambito del sistema che andiamo a realizzare.
 * <p>
 * Questa classe si pone da superclasse ,volendo generalizzare specializzazioni concrete delle classi concernenti
 * il dominio (Studenti,Docenti,Responsabili) e  a tal fine si impone di centralizzare la gestione dei dati personali
 * di coloro che sarranno gli utenti della nostra applicazione.
 * </p>
 *
 */

public abstract class Utente {
    private String nome;
    private String cognome;
    private String email;
    private String login;
    private String password;
    private int idUtente;

    /**
     * Costruttore che inizializza un utente generico con i suoi dati identificativi.
     *
     * @param nome     Nome di battesimo dell'utente
     * @param cognome  Cognome dell'utente
     * @param email    Indirizzo e-mail per le notifiche di sistema
     * @param login    Username univoco utilizzato per la fase di login
     * @param password Password associata all'account
     */

    public Utente(String nome, String cognome, String email, String login, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.login = login;
        this.password = password;
    }
    /**
     * Restituisce il nome dell'utente.
     *
     * @return String contenente il nome
     */
    public String getNome() { return nome; }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return String contenente il cognome
     */

    public String getCognome() { return cognome; }
    /**
     * Restituisce lo username utilizzato per l'accesso.
     *
     * @return String contenente il login
     */

    public String getLogin() { return login; }
    /**
     * Restituisce la password dell'utente.
     *
     * @return String contenente la password
     */

    public String getPassword() { return password; }
    /**
     * Restituisce l'identificatore univoco del database associato all'utente.
     *
     * @return Intero che rappresenta l'ID utente (Chiave Primaria)
     */

    public int getIdUtente() { return idUtente; }
    /**
     * Imposta l'identificatore univoco ricavato dal database a seguito della persistenza.
     *
     * @param idUtente L'ID generato dal database relazionale
     */
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

}
