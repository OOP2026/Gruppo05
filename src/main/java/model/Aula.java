package model;

import java.util.ArrayList;
import java.util.List;


/**
 * Rappresenta un'aula fisica dell'ateneo adibita allo svolgimento delle lezioni.
 * <p>
 * Gestisce l'aggregazione con le lezioni ospitate e si occupa di verificare
 * se lo spazio è occupato o libero in una determinata finestra temporale.
 * </p>
 *
 */


public class Aula {
    private String nome;
    private List<Lezione> lezioniOspitate = new ArrayList<>();
    private int idAula;

    /**
     * Costruttore per creare un'aula con il suo identificativo fisico.
     *
     * @param nome Nome o sigla dell'aula (es. Aula A1)
     */

    public Aula(String nome) {
        this.nome = nome;
    }


    /**
     * Associa una nuova lezione programmata all'aula.
     *
     * @param lezione L'oggetto {@link Lezione} da ospitare nell'aula
     */

    public void addLezione(Lezione l) {
        this.lezioniOspitate.add(l);
    }



    /**
     * Verifica la disponibilità dell'aula in un determinato intervallo orario.
     * Evita sovrapposizioni e conflitti di pianificazione fisica.
     *
     * @param giorno    Giorno della settimana da controllare
     * @param inizio    Orario di inizio proposto
     * @param fine      Orario di fine proposto
     * @return {@code true} se l'aula è libera da impegni, {@code false} se si sovrappone a lezioni esistenti
     */

    public boolean isLibera(model.GiornoSettimana giorno, java.time.LocalTime inizio, java.time.LocalTime fine) {
        for (Lezione l : lezioniOspitate) {
            if (l.getGiorno() == giorno) {
                if (inizio.isBefore(l.getOraFine()) && fine.isAfter(l.getOraInizio())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Restituisce l'ID univoco del database associato all'aula.
     *
     * @return Intero che rappresenta l'ID (Chiave Primaria)
     */

    public int getIdAula() { return idAula; }

    /**
     * Imposta l'identificatore univoco ricavato dal database per l'aula.
     *
     * @param idAula L'ID generato dal database relazionale
     */

    public void setIdAula(int idAula) { this.idAula = idAula; }

    /**
     * Restituisce il nome dell'aula.
     *
     * @return String contenente la sigla dell'aula
     */
    public String getNome() { return nome; }

    /**
     * Rappresentazione testuale dell'aula per l'interfaccia grafica.
     *
     * @return Stringa contenente il nome dell'aula
     */

    @Override
    public String toString() {
        return nome;
    }
}