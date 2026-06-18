package gui;

import controller.Controller;
import model.Lezione;
import model.Studente;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

/**
 * Finestra di Boundary dedicata alla consultazione dell'orario settimanale
 * delle lezioni da parte degli studenti.
 * <p>
 * Carica in memoria i dati richiesti interrogando il Controller e popola
 * un componente JTable non modificabile direttamente dall'utente.
 * </p>
 */
public class DashboardStudente extends JFrame {
    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JTable tabellaOrario;
    private JButton btnLogout;

    /**
     * Costruttore della classe. Recupera lo studente attualmente autenticato,
     * configura la visualizzazione tabellare dell'orario filtrato per il suo anno
     * di corso e registra il listener per l'operazione di logout.
     */
    public DashboardStudente() {
        setContentPane(mainPanel);
        setTitle("Dashboard Studente - Orario Lezioni");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Recupero i dati dell'utente dal Controller
        Studente studenteLoggato = (Studente) Controller.getInstance().getUtenteLoggato();
        lblBenvenuto.setText("Benvenuto, " + studenteLoggato.getNome() + " " + studenteLoggato.getCognome() + " (" + studenteLoggato.getMatricola() + ")");

        // Creazione del modello della tabella non modificabile
        String[] colonne = {"Giorno", "Ora Inizio", "Ora Fine", "Materia", "Aula"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impedisce modifiche dirette della griglia
            }
        };

        // Popolamento dei dati transienti in tabella
        List<Lezione> lezioni = Controller.getInstance().getLezioniPerStudente(studenteLoggato);
        for (Lezione l : lezioni) {
            model.addRow(new Object[]{
                    l.getGiorno(),
                    l.getOraInizio(),
                    l.getOraFine(),
                    l.getInsegnamento().getNome(),
                    l.getAula().getNome()
            });
        }
        tabellaOrario.setModel(model);

        // Listener per la disconnessione sicura e ritorno a LoginFrame
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.getInstance().effettuaLogout();
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                dispose(); // Rilascia le risorse di memoria di questa dashboard
            }
        });
    }

}