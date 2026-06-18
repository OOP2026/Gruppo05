package model;

import model.GiornoSettimana;
import java.time.LocalTime;
/**
 * Rappresenta un singolo blocco didattico orario schedulato in calendario.
 * <p>
 * Una lezione è strettamente collegata ad un insegnamento (da cui dipende),
 * ad un'aula fisica e si svolge in una determinata finestra temporale.
 * </p>
 *
 */

public class Lezione {
    private Insegnamento insegnamento;
    private GiornoSettimana giorno;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Aula aula;
    private int idLezione;


    /**
     * Costruttore completo per istanziare e schedulare una nuova lezione.
     *
     * @param insegnamento L'oggetto {@link Insegnamento} di appartenenza
     * @param giorno       Il giorno della settimana prescelto
     * @param oraInizio    Orario di inizio della lezione
     * @param oraFine      Orario di termine della lezione
     * @param aula         L'oggetto {@link Aula} in cui si tiene la lezione
     */
    public Lezione(Insegnamento insegnamento, GiornoSettimana giorno, LocalTime oraInizio, LocalTime oraFine, Aula aula) {
        this.insegnamento = insegnamento;
        this.giorno = giorno;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.aula = aula;

        // Collega automaticamente l'oggetto bidirezionalmente
        // a Insegnamento e Aula
        this.insegnamento.addLezione(this);
        this.aula.addLezione(this);
    }
    /**
     * Restituisce l'ID univoco del database associato alla lezione.
     *
     * @return Intero che rappresenta l'ID (Chiave Primaria)
     */
    public int getIdLezione() { return idLezione; }

    /**
     * Imposta l'identificatore univoco ricavato dal database per la lezione.
     *
     * @param idLezione L'ID generato dal database relazionale
     */
    public void setIdLezione(int idLezione) { this.idLezione = idLezione; }

    /**
     * Restituisce l'insegnamento di riferimento della lezione.
     *
     * @return L'oggetto {@link Insegnamento} associato
     */
    public Insegnamento getInsegnamento() { return insegnamento; }
    /**
     * Restituisce il giorno della settimana in cui si tiene la lezione.
     *
     * @return Il tipo enumerativo {@link GiornoSettimana}
     */
    public GiornoSettimana getGiorno() { return giorno; }
    /**
     * Restituisce l'orario di inizio della lezione.
     *
     * @return Oggetto {@link LocalTime} di inizio
     */
    public LocalTime getOraInizio() { return oraInizio; }
    /**
     * Restituisce l'orario di termine della lezione.
     *
     * @return Oggetto {@link LocalTime} di fine
     */
    public LocalTime getOraFine() { return oraFine; }
    /**
     * Restituisce l'aula in cui si tiene la lezione.
     *
     * @return L'oggetto {@link Aula} associato
     */
    public Aula getAula() { return aula; }

    /**
     * Aggiorna il giorno della lezione (chiamato a seguito di riprogrammazione).
     *
     * @param giorno Il nuovo giorno della settimana
     */

    public void setGiorno(GiornoSettimana giorno) { this.giorno = giorno; }

    /**
     * Aggiorna l'orario di inizio della lezione (chiamato a seguito di riprogrammazione).
     *
     * @param oraInizio Il nuovo orario di inizio
     */
    public void setOraInizio(LocalTime oraInizio) { this.oraInizio = oraInizio; }
    /**
     * Aggiorna l'orario di fine della lezione (chiamato a seguito di riprogrammazione).
     *
     * @param oraFine Il nuovo orario di fine
     */
    public void setOraFine(LocalTime oraFine) { this.oraFine = oraFine; }
}