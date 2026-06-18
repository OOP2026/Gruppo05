package model;
/**
 * Rappresenta il docente coordinatore del corso di laurea, delegato
 * alla definizione delle aule, dei corsi e alla valutazione delle richieste.
 * <p>
 * Estende direttamente la classe {@link Docente}, ereditando tutte le
 * funzionalità didattiche ma distinguendosi dal punto di vista gestionale.
 * </p>
 *
 */

public class Responsabile extends Docente {
    /**
     * Costruttore per istanziare la figura del coordinatore responsabile degli orari.
     *
     * @param nome     Nome del responsabile
     * @param cognome  Cognome del responsabile
     * @param email    E-mail istituzionale
     * @param login    Username amministrativo
     * @param password Password amministrativa
     */
    public Responsabile(String nome, String cognome, String email, String login, String password) {
        super(nome, cognome, email, login, password);
    }


}