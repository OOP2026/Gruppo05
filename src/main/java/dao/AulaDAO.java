package dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interfaccia DAO dedicata alla persistenza delle aule fisiche dell'ateneo.
 *
 */
public interface AulaDAO {

    /**
     * Inserisce una nuova aula fisica all'interno del database.
     *
     * @param nome Nome identificativo dell'aula (es. "Aula A1")
     * @throws SQLException Se si verifica una violazione del vincolo UNIQUE sul nome o errori SQL
     */
    void inserisciAulaDB(String nome) throws SQLException;

    /**
     * Recupera l'elenco di tutte le aule presenti a sistema, popolando
     * le liste fornite in input.
     *
     * @param idAule    Lista di destinazione per gli identificativi delle aule (PK)
     * @param nomiAule  Lista di destinazione per i nomi delle aule
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query
     */
    void leggiAuleDB(ArrayList<Integer> idAule, ArrayList<String> nomiAule) throws SQLException;
}