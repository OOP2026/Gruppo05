package dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interfaccia DAO per la gestione della persistenza delle lezioni schedulate.
 * <p>
 * Gestisce l'inserimento, il recupero e l'aggiornamento a seguito di riprogrammazione
 * delle singole lezioni all'interno del database.
 * </p>
 *
 * @author Renato Milo
 * @version 1.0
 */
public interface LezioneDAO {

    /**
     * Registra una nuova lezione all'interno del calendario accademico sul database.
     *
     * @param idInsegnamento Identificativo del corso collegato (FK)
     * @param giorno         Giorno della settimana (LUNEDI, ..., VENERDI)
     * @param oraInizio      Orario di inizio (formattato come hh:mm)
     * @param oraFine        Orario di fine (formattato come hh:mm)
     * @param idAula         Identificativo dell'aula fisica ospitante (FK)
     * @throws SQLException Se si verificano conflitti fisici o di integrità referenziale
     */
    void inserisciLezioneDB(int idInsegnamento, String giorno, String oraInizio, String oraFine, int idAula) throws SQLException;

    /**
     * Recupera tutte le lezioni memorizzate a database.
     *
     * @param idLezioni       Lista di destinazione per le PK delle lezioni
     * @param idInsegnamenti  Lista di destinazione per le FK dei corsi collegati
     * @param giorni          Lista di destinazione per i giorni della settimana
     * @param oreInizio       Lista di destinazione per gli orari di inizio (formato hh:mm)
     * @param oreFine         Lista di destinazione per gli orari di fine (formato hh:mm)
     * @param idAule          Lista di destinazione per le FK delle aule associate
     * @throws SQLException Se si verifica un errore durante l'esecuzione del SELECT
     */
    void leggiLezioniDB(ArrayList<Integer> idLezioni, ArrayList<Integer> idInsegnamenti, ArrayList<String> giorni, ArrayList<String> oreInizio, ArrayList<String> oreFine, ArrayList<Integer> idAule) throws SQLException;

    /**
     * Esegue l'UPDATE dei parametri temporali e spaziali di una singola lezione a seguito
     * di una delibera di approvazione di una richiesta di spostamento.
     *
     * @param idLezione Identificativo della lezione da modificare (PK)
     * @param giorno    Nuovo giorno stabilito per lo svolgimento
     * @param oraInizio Nuovo orario di inizio lezione (formato hh:mm)
     * @param oraFine   Nuovo orario di fine lezione (formato hh:mm)
     * @throws SQLException Se si verifica un errore durante l'aggiornamento del record
     */
    void aggiornaOrarioLezioneDB(int idLezione, String giorno, String oraInizio, String oraFine) throws SQLException;
}