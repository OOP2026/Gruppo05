package controller;

import model.*;
import dao.*;
import implementazioneDao.*;
import database_connection.*;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Questa classe rappresenta il nucleo decisionale dell'applicazione (strato di Controllo).
 * <p>
 * Agisce come unico punto di contatto tra le interfacce grafiche di Boundary e lo strato
 * delle entità persistenti del database (DAO,database) o non in memoria (Model).
 * Coordina i flussi di dati, la sincronizzazione iniziale e l'esecuzione della logica applicativa.
 * </p>
 */
public class Controller {

	/**
	 * Riferimento statico all'unica istanza attiva del Controller (Pattern Singleton).
	 */
	private static Controller instance;


	private List<Utente> utenti;
	private List<Aula> aule;
	private List<Insegnamento> insegnamenti;
	private List<Lezione> lezioni;
	private List<RichiestaSpostamento> richieste;


	private Utente utenteLoggato;
	private List<Docente> docenti = new ArrayList<>();


	private UtenteDAO utenteDAO;
	private AulaDAO aulaDAO;
	private InsegnamentoDAO insegnamentoDAO;
	private LezioneDAO lezioneDAO;
	private RichiestaSpostamentoDAO richiestaSpostamentoDAO;

	/**
	 * Costruttore privato della classe. Inizializza le liste di cache,
	 * istanzia le implementazioni concrete dei DAO per PostgreSQL e
	 * avvia la sincronizzazione automatica dei dati all'avvio.
	 */
	private Controller() {
		utenti = new ArrayList<>();
		aule = new ArrayList<>();
		insegnamenti = new ArrayList<>();
		lezioni = new ArrayList<>();
		richieste = new ArrayList<>();

		// Inizializzazione fisica dei connettori DAO
		utenteDAO = new UtenteImplementazionePostgresDAO();
		aulaDAO = new AulaImplementazionePostgresDAO();
		insegnamentoDAO = new InsegnamentoImplementazionePostgresDAO();
		lezioneDAO = new LezioneImplementazionePostgresDAO();
		richiestaSpostamentoDAO = new RichiestaSpostamentoImplementazioneDAO();

		// Caricamento e allineamento dati da database
		sincronizzaConDatabase();
	}

	/**
	 * Restituisce l'unica istanza attiva del Controller. Se non esiste,
	 * provvede a crearla garantendo l'unicità dello stato per tutta l'applicazione.
	 *
	 * @return L'istanza singleton del Controller
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	/**
	 * Gestisce la fase di verifica delle credenziali inserite dall'utente.
	 * <p>
	 * Se l'autenticazione sul database ha successo, recupera i dati anagrafici associati,
	 * istanzia l'oggetto corretto nel Model (Studente, Docente o Responsabile) e lo
	 * imposta come utente attivo della sessione corrente.
	 * </p>
	 *
	 * @param login    Lo username inserito nella maschera di login
	 * @param password La password inserita (protetta)
	 * @return {@code true} se le credenziali corrispondono a un account valido, altrimenti {@code false}
	 */
	public boolean effettuaLogin(String login, String password) {
		try {
			// Interrogazione diretta sul database per la verifica dei dati
			String ruolo = utenteDAO.loginDB(login, password);
			if (ruolo != null) {
				ArrayList<Integer> idUtente = new ArrayList<>();
				ArrayList<String> nome = new ArrayList<>();
				ArrayList<String> cognome = new ArrayList<>();
				ArrayList<String> email = new ArrayList<>();
				ArrayList<String> matricola = new ArrayList<>();
				ArrayList<String> annoCorso = new ArrayList<>();
				ArrayList<Boolean> isResponsabile = new ArrayList<>();

				utenteDAO.leggiDatiUtenteLoggatoDB(login, idUtente, nome, cognome, email, matricola, annoCorso, isResponsabile);

				if (!idUtente.isEmpty()) {

					if (ruolo.equals("STUDENTE")) {
						AnnoCorso anno = AnnoCorso.valueOf(annoCorso.get(0));
						Studente s = new Studente(nome.get(0), cognome.get(0), email.get(0), login, password, matricola.get(0), anno);
						s.setIdUtente(idUtente.get(0));
						this.utenteLoggato = s;
					} else if (ruolo.equals("RESPONSABILE")) {
						Responsabile r = new Responsabile(nome.get(0), cognome.get(0), email.get(0), login, password);
						r.setIdUtente(idUtente.get(0));
						this.utenteLoggato = r;
					} else {
						Docente d = new Docente(nome.get(0), cognome.get(0), email.get(0), login, password);
						d.setIdUtente(idUtente.get(0));
						this.utenteLoggato = d;
					}
					return true;
				}
			}
		} catch (SQLException e) {
			System.err.println("Errore durante la procedura di login sul DB.");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Termina la sessione dell'utente corrente rimuovendone il riferimento in memoria.
	 */
	public void effettuaLogout() {
		this.utenteLoggato = null;
	}

	/**
	 * Restituisce l'istanza dell'utente correntemente autenticato a sistema.
	 *
	 * @return L'oggetto {@link Utente} loggato, o {@code null} se non vi sono sessioni attive
	 */
	public Utente getUtenteLoggato() {
		return utenteLoggato;
	}

	/**
	 * Restituisce la lista filtrata delle lezioni associate all'anno di corso dello studente.
	 *
	 * @param studente Lo studente di cui filtrare il piano orario
	 * @return Una {@link List} di lezioni corrispondenti all'anno di studi dello studente
	 */
	public List<Lezione> getLezioniPerStudente(Studente studente) {
		List<Lezione> risultato = new ArrayList<>();
		for (Lezione l : lezioni) {
			if (l.getInsegnamento().getAnnoDiCorso() == studente.getAnno()) {
				risultato.add(l);
			}
		}
		return risultato;
	}

	/**
	 * Restituisce la lista delle lezioni associate alle cattedre presiedute da un docente.
	 *
	 * @param docente Il docente di cui filtrare l'orario didattico
	 * @return Una {@link List} di lezioni assegnate a quel docente
	 */
	public List<Lezione> getLezioniPerDocente(Docente docente) {
		List<Lezione> risultato = new ArrayList<>();
		for (Lezione l : lezioni) {
			if (l.getInsegnamento().getDocenteTitolare().getIdUtente() == docente.getIdUtente()) {
				risultato.add(l);
			}
		}
		return risultato;
	}

	/**
	 * Restituisce l'elenco di tutte le proposte di spostamento orario in attesa di valutazione.
	 *
	 * @return Una {@link List} di richieste aventi stato 'IN_ATTESA'
	 */
	public List<RichiestaSpostamento> getRichiesteInAttesa() {
		List<RichiestaSpostamento> risultato = new ArrayList<>();
		for (RichiestaSpostamento r : richieste) {
			if (r.getStato() == StatoRichiesta.IN_ATTESA) {
				risultato.add(r);
			}
		}
		return risultato;
	}

	/**
	 * Gestisce la creazione e l'inoltro di una nuova proposta di spostamento orario.
	 * <p>
	 * Effettua prima la scrittura fisica sul database tramite lo strato DAO. In caso di successo,
	 * istanzia l'oggetto RichiestaSpostamento e lo accoda alla cache in memoria per l'aggiornamento
	 * immediato dei dati visualizzati a schermo.
	 * </p>
	 *
	 * @param lezione      La lezione da spostare
	 * @param nuovoGiorno  Il nuovo giorno proposto per lo svolgimento
	 * @param nuovaOraInizio Orario d'inizio proposto (formato hh:mm)
	 * @param nuovaOraFine   Orario di fine proposto (formato hh:mm)
	 */
	public void inviaRichiestaSpostamento(Lezione lezione, GiornoSettimana nuovoGiorno, String nuovaOraInizio, String nuovaOraFine) {
		try {
			// 1. Persistenza fisica su PostgreSQL
			richiestaSpostamentoDAO.inviaRichiestaDB(lezione.getIdLezione(), nuovoGiorno.toString(), nuovaOraInizio, nuovaOraFine);

			// 2. Allineamento dei dati transitori in memoria volatile
			RichiestaSpostamento nuovaRichiesta = new RichiestaSpostamento(lezione, nuovoGiorno, nuovaOraInizio, nuovaOraFine);
			richieste.add(nuovaRichiesta);
			System.out.println("LOG: Richiesta salvata nel DB e sincronizzata in memoria.");
		} catch (SQLException e) {
			System.err.println("Errore nell'invio della richiesta di spostamento sul DB.");
			e.printStackTrace();
		}
	}

	/**
	 * Gestisce il processo decisionale del coordinatore sulle richieste pendenti.
	 * <p>
	 * Se lo stato è approvato, il sistema esegue in modo transazionale l'UPDATE dell'orario
	 * della lezione sia sul database (tramite DAO) sia in memoria (aggiornando l'oggetto del Model),
	 * garantendo la coerenza complessiva dello schema.
	 * </p>
	 *
	 * @param richiesta  La richiesta da valutare
	 * @param nuovoStato Lo stato da assegnare ('APPROVATA' o 'RIFIUTATA')
	 */
	public void valutaRichiesta(RichiestaSpostamento richiesta, StatoRichiesta nuovoStato) {
		try {
			// 1. Aggiornamento dello stato della richiesta nel database
			richiestaSpostamentoDAO.valutaRichiestaDB(richiesta.getIdRichiesta(), nuovoStato.toString());

			// 2. Se approvata, allinea la lezione reale sia su DB che nel Model
			if (nuovoStato == StatoRichiesta.APPROVATA) {
				Lezione lezione = richiesta.getLezioneDaSpostare();

				// Aggiornamento su DB
				lezioneDAO.aggiornaOrarioLezioneDB(lezione.getIdLezione(), richiesta.getGiornoProposto().toString(), richiesta.getOraInizioProposta().toString(), richiesta.getOraFineProposta().toString());

				// Aggiornamento nel Model
				lezione.setGiorno(richiesta.getGiornoProposto());
				lezione.setOraInizio(richiesta.getOraInizioProposta());
				lezione.setOraFine(richiesta.getOraFineProposta());
			}

			// 3. Modifica dello stato in memoria
			richiesta.setStato(nuovoStato);
		} catch (SQLException e) {
			System.err.println("Errore durante la valutazione della richiesta sul DB.");
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * All'avvio dell'applicazione, interroga in sequenza le tabelle di PostgreSQL per caricare
	 * i record grezzi e ricostruisce in modo coerentgli oggetti in memoria
	 *.
	 * </p>
	 */
	private void sincronizzaConDatabase() {
		try {
			System.out.println("LOG: Avvio sincronizzazione iniziale da PostgreSQL...");

			// Pulisce le liste per evitare dati orfani o duplicazioni in caso di ricaricamento
			docenti.clear();
			aule.clear();
			insegnamenti.clear();
			lezioni.clear();
			richieste.clear();

			// 1. Caricamento Docenti
			ArrayList<Integer> idDocenti = new ArrayList<>();
			ArrayList<String> nomiDoc = new ArrayList<>();
			ArrayList<String> cognomiDoc = new ArrayList<>();
			ArrayList<String> emailDoc = new ArrayList<>();
			ArrayList<String> loginDoc = new ArrayList<>();
			ArrayList<String> passDoc = new ArrayList<>();
			ArrayList<Boolean> isRespDoc = new ArrayList<>();

			((UtenteImplementazionePostgresDAO) utenteDAO).leggiTuttiIDocentiDB(idDocenti, nomiDoc, cognomiDoc, emailDoc, loginDoc, passDoc, isRespDoc);
			Map<Integer, Docente> mapDocenti = new HashMap<>();
			for (int i = 0; i < idDocenti.size(); i++) {
				Docente d;
				if (isRespDoc.get(i)) {
					d = new Responsabile(nomiDoc.get(i), cognomiDoc.get(i), emailDoc.get(i), loginDoc.get(i), passDoc.get(i));
				} else {
					d = new Docente(nomiDoc.get(i), cognomiDoc.get(i), emailDoc.get(i), loginDoc.get(i), passDoc.get(i));
				}
				d.setIdUtente(idDocenti.get(i));

				docenti.add(d);
				mapDocenti.put(idDocenti.get(i), d);
			}

			// 2. Caricamento Aule
			ArrayList<Integer> idAule = new ArrayList<>();
			ArrayList<String> nomiAule = new ArrayList<>();
			aulaDAO.leggiAuleDB(idAule, nomiAule);
			Map<Integer, Aula> mapAule = new HashMap<>();
			for (int i = 0; i < idAule.size(); i++) {
				Aula a = new Aula(nomiAule.get(i));
				a.setIdAula(idAule.get(i));
				aule.add(a);
				mapAule.put(idAule.get(i), a);
			}

			// 3. Caricamento Insegnamenti
			ArrayList<Integer> idInsegnamenti = new ArrayList<>();
			ArrayList<String> nomiInsegn = new ArrayList<>();
			ArrayList<Integer> cfuList = new ArrayList<>();
			ArrayList<String> anniCorso = new ArrayList<>();
			ArrayList<Integer> fkDocenti = new ArrayList<>();

			insegnamentoDAO.leggiInsegnamentiDB(idInsegnamenti, nomiInsegn, cfuList, anniCorso, fkDocenti);
			Map<Integer, Insegnamento> mapInsegnamenti = new HashMap<>();
			for (int i = 0; i < idInsegnamenti.size(); i++) {
				Docente d = mapDocenti.get(fkDocenti.get(i));
				Insegnamento ins = new Insegnamento(nomiInsegn.get(i), cfuList.get(i), AnnoCorso.valueOf(anniCorso.get(i)), d);
				ins.setIdInsegnamento(idInsegnamenti.get(i));
				insegnamenti.add(ins);
				mapInsegnamenti.put(idInsegnamenti.get(i), ins);
			}

			// 4. Caricamento Lezioni
			ArrayList<Integer> idLezioni = new ArrayList<>();
			ArrayList<Integer> fkInsegnamenti = new ArrayList<>();
			ArrayList<String> giorni = new ArrayList<>();
			ArrayList<String> oreInizio = new ArrayList<>();
			ArrayList<String> oreFine = new ArrayList<>();
			ArrayList<Integer> fkAule = new ArrayList<>();

			lezioneDAO.leggiLezioniDB(idLezioni, fkInsegnamenti, giorni, oreInizio, oreFine, fkAule);
			Map<Integer, Lezione> mapLezioni = new HashMap<>();
			for (int i = 0; i < idLezioni.size(); i++) {
				Insegnamento ins = mapInsegnamenti.get(fkInsegnamenti.get(i));
				Aula a = mapAule.get(fkAule.get(i));
				Lezione lez = new Lezione(ins, GiornoSettimana.valueOf(giorni.get(i)), LocalTime.parse(oreInizio.get(i)), LocalTime.parse(oreFine.get(i)), a);
				lez.setIdLezione(idLezioni.get(i));
				lezioni.add(lez);
				mapLezioni.put(idLezioni.get(i), lez);
			}

			// 5. Caricamento Richieste
			ArrayList<Integer> idRichieste = new ArrayList<>();
			ArrayList<Integer> fkLezioni = new ArrayList<>();
			ArrayList<String> giorniProp = new ArrayList<>();
			ArrayList<String> oreInizioProp = new ArrayList<>();
			ArrayList<String> oreFineProp = new ArrayList<>();
			ArrayList<String> stati = new ArrayList<>();

			richiestaSpostamentoDAO.leggiRichiesteDB(idRichieste, fkLezioni, giorniProp, oreInizioProp, oreFineProp, stati);
			for (int i = 0; i < idRichieste.size(); i++) {
				Lezione lez = mapLezioni.get(fkLezioni.get(i));
				RichiestaSpostamento req = new RichiestaSpostamento(lez, GiornoSettimana.valueOf(giorniProp.get(i)), oreInizioProp.get(i), oreFineProp.get(i));
				req.setIdRichiesta(idRichieste.get(i));
				req.setStato(StatoRichiesta.valueOf(stati.get(i)));
				richieste.add(req);
			}

			System.out.println("LOG: Sincronizzazione iniziale completata con successo!");

		} catch (SQLException e) {
			System.err.println("Errore fatale: Impossibile sincronizzare i dati all'avvio dal Database.");
			e.printStackTrace();
		}
	}

	/**
	 * Registra un nuovo utente nel database tramite DAO. In caso di successo,
	 * riesegue la sincronizzazione per caricare l'utente appena creato nel modello di memoria.
	 *
	 * @param nome      Nome dell'utente
	 * @param cognome   Cognome dell'utente
	 * @param email     E-mail dell'utente
	 * @param login     Username per il login
	 * @param password  Password associata
	 * @param ruolo     Ruolo proposto ('STUDENTE', 'DOCENTE', 'RESPONSABILE')
	 * @param annoCorso Anno di corso dell'utente (solo se studente)
	 * @return {@code true} se l'inserimento sul DB ha successo, altrimenti {@code false}
	 */
	public boolean registraNuovoUtente(String nome, String cognome, String email, String login, String password, String ruolo, String annoCorso) {
		try {
			utenteDAO.registraUtenteDB(nome, cognome, email, login, password, ruolo, annoCorso);
			sincronizzaConDatabase();
			return true;
		} catch (SQLException e) {
			System.err.println("Errore durante la registrazione dell'utente nel DB.");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Restituisce la cache delle aule registrate in memoria.
	 *
	 * @return Una {@link List} di oggetti {@link Aula}
	 */
	public List<Aula> getAule() {
		return aule;
	}

	/**
	 * Restituisce la cache degli insegnamenti registrati in memoria.
	 *
	 * @return Una {@link List} di oggetti {@link Insegnamento}
	 */
	public List<Insegnamento> getInsegnamenti() {
		return insegnamenti;
	}

	/**
	 * Restituisce la cache dei docenti registrati in memoria.
	 *
	 * @return Una {@link List} di oggetti {@link Docente}
	 */
	public List<Docente> getDocenti() {
		return docenti;
	}

	/**
	 * Inserisce una nuova aula sul database PostgreSQL e ne forza
	 * l'allineamento all'interno dello strato transitorio in memoria.
	 *
	 * @param nome Il nome o sigla della nuova aula da registrare
	 * @return {@code true} se l'inserimento ha avuto successo sul database, altrimenti {@code false}
	 */
	public boolean inserisciNuovaAula(String nome) {
		try {
			aulaDAO.inserisciAulaDB(nome);
			sincronizzaConDatabase();
			return true;
		} catch (SQLException e) {
			System.err.println("Errore inserimento Aula sul DB.");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Inserisce un nuovo corso di insegnamento a database e ne forza
	 * l'allineamento all'interno dello strato transitorio in memoria.
	 *
	 * @param nome    Nome del corso di insegnamento
	 * @param cfu     Numero di crediti formativi attribuiti
	 * @param anno    L'anno del percorso di studi
	 * @param docente Il docente titolare incaricato del corso
	 * @return {@code true} se la scrittura su database ha successo, altrimenti {@code false}
	 */
	public boolean inserisciNuovoInsegnamento(String nome, int cfu, AnnoCorso anno, Docente docente) {
		try {
			int idDocente = (docente != null) ? docente.getIdUtente() : -1;
			insegnamentoDAO.inserisciInsegnamentoDB(nome, cfu, anno.toString(), idDocente);
			sincronizzaConDatabase();
			return true;
		} catch (SQLException e) {
			System.err.println("Errore inserimento Insegnamento sul DB.");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Registra una nuova lezione nel database relazionale e ne forza
	 * la sincronizzazione all'interno della cache transitoria in memoria.
	 *
	 * @param insegnamento L'insegnamento di riferimento
	 * @param giorno       Il giorno della settimana stabilito
	 * @param oraInizio    Orario di inizio della lezione
	 * @param oraFine      Orario di fine della lezione
	 * @param aula         L'aula fisica adibita allo svolgimento
	 * @return {@code true} se la scrittura su database ha successo, altrimenti {@code false}
	 */
	public boolean inserisciNuovaLezione(Insegnamento insegnamento, GiornoSettimana giorno, String oraInizio, String oraFine, Aula aula) {
		try {
			lezioneDAO.inserisciLezioneDB(insegnamento.getIdInsegnamento(), giorno.toString(), oraInizio, oraFine, aula.getIdAula());
			sincronizzaConDatabase();
			System.out.println("LOG: Nuova lezione inserita sul DB e sincronizzata in memoria.");
			return true;
		} catch (SQLException e) {
			System.err.println("Errore durante l'inserimento della lezione sul DB.");
			e.printStackTrace();
			return false;
		}
	}
}