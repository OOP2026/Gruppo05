package model;

import model.*;
import java.time.LocalTime;

/**
 * Rappresenta una proposta di spostamento orario di una lezione inviata da un docente.
 * <p>
 * Contiene i dettagli originari della lezione, la nuova finestra oraria proposta
 * e lo stato della transazione di approvazione (in attesa, approvata o rifiutata).
 * </p>
 *
 */
public class RichiestaSpostamento {
    private int idRichiesta;
    private Lezione lezioneDaSpostare;
    private GiornoSettimana giornoProposto;
    private LocalTime oraInizioProposta;
    private LocalTime oraFineProposta;
    private StatoRichiesta stato;

    /**
     * Costruttore completo per istanziare una richiesta di riprogrammazione lezione.
     * Gli orari proposti vengono forniti come stringhe e convertiti a runtime.
     *
     * @param lezione           La {@link Lezione} di cui si richiede lo spostamento
     * @param giornoProposto    Il nuovo giorno desiderato
     * @param oraInizioProposta Stringa dell'ora d'inizio proposta (formato hh:mm)
     * @param oraFineProposta   Stringa dell'ora di fine proposta (formato hh:mm)
     */
    public RichiestaSpostamento(Lezione lezione, GiornoSettimana giornoProposto, String oraInizioProposta, String oraFineProposta) {
        this.lezioneDaSpostare = lezione;
        this.giornoProposto = giornoProposto;
        this.oraInizioProposta = LocalTime.parse(oraInizioProposta);
        this.oraFineProposta = LocalTime.parse(oraFineProposta);
        this.stato = StatoRichiesta.IN_ATTESA; // Stato di default alla creazione
    }

    /**
     * Restituisce la lezione che è oggetto della richiesta di spostamento.
     *
     * @return L'oggetto {@link Lezione} originario
     */
    public Lezione getLezioneDaSpostare() { return lezioneDaSpostare; }

    /**
     * Restituisce il giorno proposto per la riprogrammazione.
     *
     * @return Il tipo enumerativo {@link GiornoSettimana}
     */
    public GiornoSettimana getGiornoProposto() { return giornoProposto; }

    /**
     * Restituisce l'orario di inizio proposto.
     *
     * @return Oggetto {@link LocalTime} proposto
     */
    public LocalTime getOraInizioProposta() { return oraInizioProposta; }

    /**
     * Restituisce l'orario di fine proposto.
     *
     * @return Oggetto {@link LocalTime} proposto
     */
    public LocalTime getOraFineProposta() { return oraFineProposta; }

    /**
     * Restituisce lo stato attuale del processo di approvazione della richiesta.
     *
     * @return Il tipo enumerativo {@link StatoRichiesta}
     */
    public StatoRichiesta getStato() { return stato; }

    /**
     * Modifica lo stato della richiesta (chiamato dal coordinatore in fase di valutazione).
     *
     * @param stato Il nuovo stato {@link StatoRichiesta} da assegnare
     */
    public void setStato(StatoRichiesta stato) { this.stato = stato; }

    /**
     * Restituisce l'ID univoco del database associato alla richiesta.
     *
     * @return Intero che rappresenta l'ID (Chiave Primaria)
     */
    public int getIdRichiesta() { return idRichiesta; }

    /**
     * Imposta l'identificatore univoco ricavato dal database per la richiesta.
     *
     * @param idRichiesta L'ID generato dal database relazionale
     */
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
}