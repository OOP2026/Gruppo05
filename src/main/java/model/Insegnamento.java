package model;


import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un corso accademico caratterizzato da CFU e anno di erogazione,
 * associato in modo esclusivo a un docente titolare.
 * <p>
 * Mantiene una relazione di composizione forte con le lezioni in cui si articola.
 * </p>
 *
 */
public class Insegnamento {
    private int idInsegnamento;
    private String nome;
    private int cfu;
    private AnnoCorso annoDiCorso;
    private Docente docenteTitolare;
    private List<Lezione> lezioni = new ArrayList<>();

    /**
     * Costruttore per inizializzare un nuovo corso di insegnamento.
     *
     * @param nome            Nome del corso (es. Programmazione a Oggetti)
     * @param cfu             Crediti Formativi Universitari associati alla materia
     * @param annoDiCorso     Anno del percorso di studi in cui viene erogato
     * @param docenteTitolare Oggetto {@link Docente} incaricato di tenere il corso
     */
    public Insegnamento(String nome, int cfu, AnnoCorso annoDiCorso, Docente docenteTitolare) {
        this.nome = nome;
        this.cfu = cfu;
        this.annoDiCorso = annoDiCorso;
        this.docenteTitolare = docenteTitolare;
        this.docenteTitolare.addInsegnamento(this); // Sincronizza l'associazione bidirezionale
    }

    /**
     * Associa una nuova lezione programmata a questo insegnamento.
     *
     * @param lezione L'oggetto {@link Lezione} da aggiungere al piano del corso
     */
    public void addLezione(Lezione lezione) {
        this.lezioni.add(lezione);
    }

    /**
     * Restituisce il nome dell'insegnamento.
     *
     * @return String contenente il nome del corso
     */
    public String getNome() { return nome; }

    /**
     * Restituisce il docente titolare della cattedra del corso.
     *
     * @return L'oggetto {@link Docente} associato
     */
    public Docente getDocenteTitolare() { return docenteTitolare; }

    /**
     * Restituisce l'anno di corso dell'insegnamento.
     *
     * @return Il tipo enumerativo {@link AnnoCorso}
     */
    public AnnoCorso getAnnoDiCorso() { return annoDiCorso; }

    /**
     * Restituisce l'ID univoco del database associato all'insegnamento.
     *
     * @return Intero che rappresenta l'ID (Chiave Primaria)
     */
    public int getIdInsegnamento() { return idInsegnamento; }

    /**
     * Imposta l'identificatore univoco ricavato dal database per l'insegnamento.
     *
     * @param idInsegnamento L'ID generato dal database relazionale
     */
    public void setIdInsegnamento(int idInsegnamento) { this.idInsegnamento = idInsegnamento; }

    /**
     * Rappresentazione testuale del corso ottimizzata per i menu grafici della GUI.
     *
     * @return Stringa formattata con nome e anno di corso associato
     */
    @Override
    public String toString() {
        return nome + " (" + annoDiCorso + ")";
    }
}