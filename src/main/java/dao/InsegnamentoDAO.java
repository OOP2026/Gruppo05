package dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interfaccia DAO dedicata alla gestione della persistenza degli insegnamenti didattici.
 *
 */
public interface InsegnamentoDAO {

    /**
     * Inserisce un nuovo insegnamento all'interno del database relazionale.
     *
     * @param nome               Nome del corso di insegnamento
     * @param cfu                Numero di crediti formativi attribuiti
     * @param annoCorso          Anno di corso in cui è erogato (I_ANNO, II_ANNO, III_ANNO)
     * @param idDocenteTitolare  Identificativo numerico del docente titolare (FK)
     * @throws SQLException Se si verifica un errore durante l'esecuzione dell'istruzione di inserimento
     */
    void inserisciInsegnamentoDB(String nome, int cfu, String annoCorso, int idDocenteTitolare) throws SQLException;

    /**
     * Recupera l'elenco complessivo di tutti gli insegnamenti registrati a database.
     *
     * @param idInsegnamenti Lista di destinazione per gli identificativi dei corsi (PK)
     * @param nomi           Lista di destinazione per i nomi dei corsi
     * @param cfuList        Lista di destinazione per i CFU
     * @param anniCorso      Lista di destinazione per gli anni di corso
     * @param idDocenti      Lista di destinazione per gli ID dei docenti titolari (FK)
     * @throws SQLException Se si verifica un errore di lettura
     */
    void leggiInsegnamentiDB(ArrayList<Integer> idInsegnamenti, ArrayList<String> nomi, ArrayList<Integer> cfuList, ArrayList<String> anniCorso, ArrayList<Integer> idDocenti) throws SQLException;
}