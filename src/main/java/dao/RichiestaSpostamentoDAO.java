package dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interfaccia DAO dedicata alla gestione della persistenza dei ticket di spostamento delle lezioni.
 */
public interface RichiestaSpostamentoDAO {

    /**
     * Memorizza una nuova proposta di spostamento oraria compilata da un docente.
     * Lo stato di partenza del ticket inserito sarà impostato di default come 'IN_ATTESA'.
     *
     * @param idLezione          Identificativo della lezione originaria da spostare (FK)
     * @param giornoProposto     Giorno della settimana proposto per la modifica
     * @param oraInizioProposta  Orario di inizio proposto (formato hh:mm)
     * @param oraFineProposta    Orario di fine proposto (formato hh:mm)
     * @throws SQLException Se si verifica un errore durante l'esecuzione dell'INSERT SQL
     */
    void inviaRichiestaDB(int idLezione, String giornoProposto, String oraInizioProposta, String oraFineProposta) throws SQLException;

    /**
     * Aggiorna lo stato di valutazione di un ticket di spostamento.
     *
     * @param idRichiesta Identificativo univoco della richiesta da aggiornare (PK)
     * @param nuovoStato  Nuovo stato del ticket ('APPROVATA' o 'RIFIUTATA')
     * @throws SQLException Se si verifica un errore di esecuzione SQL
     */
    void valutaRichiestaDB(int idRichiesta, String nuovoStato) throws SQLException;

    /**
     * Recupera l'elenco di tutte le richieste di spostamento memorizzate a database.
     *
     * @param idRichieste    Lista di destinazione per le PK delle richieste
     * @param idLezioni      Lista di destinazione per le FK delle lezioni associate
     * @param giorniProp     Lista di destinazione per i giorni proposti
     * @param oreInizioProp  Lista di destinazione per le ore di inizio proposte
     * @param oreFineProp    Lista di destinazione per le ore di fine proposte
     * @param stati          Lista di destinazione per gli stati dei ticket (IN_ATTESA, ...)
     * @throws SQLException Se si verifica un errore in fase di lettura dati
     */
    void leggiRichiesteDB(ArrayList<Integer> idRichieste, ArrayList<Integer> idLezioni, ArrayList<String> giorniProp, ArrayList<String> oreInizioProp, ArrayList<String> oreFineProp, ArrayList<String> stati) throws SQLException;
}