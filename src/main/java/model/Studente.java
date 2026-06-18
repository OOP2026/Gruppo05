package model;
/**
 * Rappresenta uno studente iscritto all'ateneo, abilitato alla consultazione
 * dell'orario delle lezioni relativo al proprio anno di corso.
 * <p>
 * Estende la classe generica {@link Utente} aggiungendo gli attributi specifici
 * del percorso di studi come la matricola e l'anno di corso frequentato.
 * </p>
 *
 *
 */


public class Studente extends Utente {
    private String matricola;
    private AnnoCorso anno;

    /**
     * Costruttore completo per istanziare un nuovo studente nel sistema.
     *
     * @param nome      Nome dello studente
     * @param cognome   Cognome dello studente
     * @param email     E-mail istituzionale dello studente
     * @param login     Username di accesso dello studente
     * @param password  Password associata al login
     * @param matricola Codice identificativo di matricola generato dal sistema
     * @param anno      L'anno di corso frequentato dallo studente
     */
    public Studente(String nome, String cognome, String email, String login, String password, String matricola, AnnoCorso anno) {
        super(nome, cognome, email, login, password);
        this.matricola = matricola;
        this.anno = anno;
    }
    /**
     * Restituisce la matricola univoca dello studente.
     *
     * @return String contenente la matricola (es. N86/1)
     */

    public String getMatricola() {
        return matricola;
    }
    /**
     * Restituisce l'anno di corso correntemente frequentato.
     *
     * @return Il tipo enumerativo {@link AnnoCorso} associato
     */
    public AnnoCorso getAnno() {
        return anno;
    }
}