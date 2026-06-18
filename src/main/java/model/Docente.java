package model;
import java.util.ArrayList;
import java.util.List;



/**
 * Rappresenta un docente presente nell'ambito del nostro database.
 * <p>
 * Estende la classe {@link Utente} e mantiene l'associazione bidirezionale "uno-a-molti"
 * con gli insegnamenti di cui detiene la cattedra didattica.
 * </p>
 *
 */



public class Docente extends Utente {
    // Implementazione dell'associazione 1 a molti verso Insegnamento
    private List<Insegnamento> insegnamentiTitolare = new ArrayList<>();

    /**
     * Costruttore per istanziare un docente nel sistema.
     *
     * @param nome     Nome del docente
     * @param cognome  Cognome del docente
     * @param email    E-mail del docente
     * @param login    Username di accesso del docente
     * @param password Password del docente
     */



    public Docente(String nome, String cognome, String email, String login, String password) {
        super(nome, cognome, email, login, password);
    }
    /**
     * Aggiunge un insegnamento alla lista di quelli presieduti dal docente.
     *
     * @param insegnamento L'oggetto {@link Insegnamento} da assegnare al docente
     */
    public void addInsegnamento(Insegnamento insegnamento) {
        this.insegnamentiTitolare.add(insegnamento);
    }
    /**
     * Restituisce l'elenco di tutti gli insegnamenti di cui il docente è titolare.
     *
     * @return Una {@link List} di oggetti {@link Insegnamento}
     */
    public List<Insegnamento> getInsegnamentiTitolare() { return insegnamentiTitolare; }
    /**
     * Rappresentazione testuale del docente personalizzata per i menu di scelta della GUI.
     *
     * @return Stringa formattata con il titolo e l'anagrafica del docente
     */
    @Override
    public String toString() {
        return "Prof. " + getNome() + " " + getCognome();
    }

}