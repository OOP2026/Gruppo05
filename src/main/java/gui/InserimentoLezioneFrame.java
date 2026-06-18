package gui;

import controller.Controller;
import model.Aula;
import model.Docente;
import model.Insegnamento;
import model.AnnoCorso;
import model.GiornoSettimana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Finestra di Boundary adibita all'inserimento e alla pianificazione delle lezioni.
 * <p>
 * Offre la possibilità di creare aule ed insegnamenti
 * tramite l'uso di comode finestre modali di tipo JOptionPanel
 * </p>
 */
public class InserimentoLezioneFrame extends JFrame {
    private JPanel mainPanel;
    private JComboBox<Insegnamento> cbInsegnamento;
    private JComboBox<GiornoSettimana> cbGiorno;
    private JTextField txtOraInizio;
    private JTextField txtOraFine;
    private JComboBox<Aula> cbAula;
    private JButton btnSalva;
    private JButton btnAnnulla;
    private JButton btnNuovoInsegnamento;
    private JButton btnNuovaAula;
    private JFrame frameChiamante;

    /**
     * Costruttore della classe. Inizializza i componenti grafici del form,
     * popola i menu a tendina interrogando lo strato di controllo e implementa
     * i listener per l'inserimento dinamico di aule e corsi.
     *
     * @param frameChiamante Il JFrame genitore
     */
    public InserimentoLezioneFrame(JFrame frameChiamante) {
        this.frameChiamante = frameChiamante;

        setContentPane(mainPanel);
        setTitle("Pianifica Nuova Lezione");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(frameChiamante);

        // Caricamento dei dati dai ComboBox
        ricaricaInsegnamenti();
        ricaricaAule();

        for (GiornoSettimana g : GiornoSettimana.values()) {
            cbGiorno.addItem(g);
        }

        // Inserimento Aula  tramite Finestra Modale di Dialogo
        btnNuovaAula.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeAula = JOptionPane.showInputDialog(null, "Inserisci il nome della nuova Aula (es: Aula A3):");
                if (nomeAula != null && !nomeAula.trim().isEmpty()) {
                    boolean ok = Controller.getInstance().inserisciNuovaAula(nomeAula.trim());
                    if (ok) {
                        JOptionPane.showMessageDialog(null, "Aula registrata con successo!");
                        ricaricaAule(); // Aggiorna a runtime il ComboBox
                    } else {
                        JOptionPane.showMessageDialog(null, "Errore: Aula già esistente.");
                    }
                }
            }
        });

        // Inserimento Insegnamento "on the fly" con Dialog strutturati
        btnNuovoInsegnamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeInsegnamento = JOptionPane.showInputDialog(null, "Inserisci il nome del nuovo Insegnamento:");
                if (nomeInsegnamento == null || nomeInsegnamento.trim().isEmpty()) return;

                String cfuStr = JOptionPane.showInputDialog(null, "Inserisci i CFU:");
                if (cfuStr == null || cfuStr.trim().isEmpty()) return;
                int cfu;
                try {
                    cfu = Integer.parseInt(cfuStr.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "I CFU devono essere un numero intero!");
                    return;
                }

                // Scelta dell'anno di corso tramite ComboBox integrato in un Dialog (Slide 26)
                AnnoCorso anno = (AnnoCorso) JOptionPane.showInputDialog(
                        null, "Seleziona l'anno di corso:", "Anno Corso",
                        JOptionPane.QUESTION_MESSAGE, null, AnnoCorso.values(), AnnoCorso.I_ANNO
                );
                if (anno == null) return;

                List<Docente> docentiDisponibili = Controller.getInstance().getDocenti();
                if (docentiDisponibili.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Errore: Non ci sono docenti nel sistema.");
                    return;
                }

                // Scelta del docente titolare tramite ComboBox integrato in un Dialog
                Object[] docentiArr = docentiDisponibili.toArray();
                Docente docenteSel = (Docente) JOptionPane.showInputDialog(
                        null, "Seleziona il docente titolare:", "Docente Titolare",
                        JOptionPane.QUESTION_MESSAGE, null, docentiArr, docentiArr[0]
                );
                if (docenteSel == null) return;

                // Richiesta di inserimento fisico sul DB tramite Controller
                boolean ok = Controller.getInstance().inserisciNuovoInsegnamento(nomeInsegnamento.trim(), cfu, anno, docenteSel);
                if (ok) {
                    JOptionPane.showMessageDialog(null, "Insegnamento registrato con successo!");
                    ricaricaInsegnamenti(); // Aggiorna a runtime il ComboBox
                } else {
                    JOptionPane.showMessageDialog(null, "Errore durante l'inserimento.");
                }
            }
        });

        // Salvataggio formale della lezione pianificata
        btnSalva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Insegnamento insegnamento = (Insegnamento) cbInsegnamento.getSelectedItem();
                GiornoSettimana giorno = (GiornoSettimana) cbGiorno.getSelectedItem();
                String oraInizio = txtOraInizio.getText();
                String oraFine = txtOraFine.getText();
                Aula aula = (Aula) cbAula.getSelectedItem();

                if (oraInizio.isEmpty() || oraFine.isEmpty() || insegnamento == null || aula == null) {
                    JOptionPane.showMessageDialog(null, "Compila tutti i campi obbligatori!");
                    return;
                }

                boolean successo = Controller.getInstance().inserisciNuovaLezione(insegnamento, giorno, oraInizio, oraFine, aula);
                if (successo) {
                    JOptionPane.showMessageDialog(null, "Lezione pianificata con successo nel Database!");
                    tornaIndietro();
                } else {
                    JOptionPane.showMessageDialog(null, "Errore: Impossibile completare la pianificazione.");
                }
            }
        });

        btnAnnulla.addActionListener(e -> tornaIndietro());
    }

    /**
     * Ricarica e aggiorna a runtime la lista dei corsi di insegnamento disponibili nel ComboBox.
     */
    private void ricaricaInsegnamenti() {
        cbInsegnamento.removeAllItems();
        for (Insegnamento ins : Controller.getInstance().getInsegnamenti()) {
            cbInsegnamento.addItem(ins);
        }
    }

    /**
     * Ricarica e aggiorna a runtime la lista delle aule disponibili nel ComboBox.
     */
    private void ricaricaAule() {
        cbAula.removeAllItems();
        for (Aula a : Controller.getInstance().getAule()) {
            cbAula.addItem(a);
        }
    }

    /**
     * Restituisce la visibilità alla Dashboard chiamante e distrugge la finestra
     * corrente per svuotare la memoria
     */
    private void tornaIndietro() {
        frameChiamante.setVisible(true); // Restituisce visibilità alla Dashboard (
        dispose(); // Dealloca l'oggetto corrente
    }

}