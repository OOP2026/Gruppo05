package gui;

import controller.Controller;
import model.Docente;
import model.Lezione;
import model.GiornoSettimana;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

/**
 * Finestra di Boundary dedicata ai docenti registrati a sistema.
 * <p>
 * Consente la consultazione delle cattedre assegnate e l'invio formale
 * di una richiesta di riprogrammazione (spostamento) per una lezione selezionata.
 * </p>
 */
public class DashboardDocente extends JFrame {
    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JTable tabellaOrario;
    private JComboBox<GiornoSettimana> cbGiorno;
    private JTextField txtOraInizio;
    private JTextField txtOraFine;
    private JButton btnInviaRichiesta;
    private JButton btnLogout;

    /**
     * Costruttore della classe. Inizializza i componenti grafici del form,
     * popola la tabella con le sole lezioni tenute dal docente loggato
     * e gestisce l'evento di inoltro dei dati di spostamento al Controller.
     */
    public DashboardDocente() {
        setContentPane(mainPanel);
        setTitle("Cruscotto Docente");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Docente docenteLoggato = (Docente) Controller.getInstance().getUtenteLoggato();
        lblBenvenuto.setText("Prof. " + docenteLoggato.getNome() + " " + docenteLoggato.getCognome());

        //Caricamento dinamico dei giorni nel combobox(la tabella da cui selezionare)
        for (GiornoSettimana g : GiornoSettimana.values()) {
            cbGiorno.addItem(g);
        }

        //Configurazione e caricamento della griglia orari del docente
        String[] colonne = {"Giorno", "Ora Inizio", "Ora Fine", "Materia", "Aula"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Lezione> lezioni = Controller.getInstance().getLezioniPerDocente(docenteLoggato);
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

        // Listener reattivo per l'invio della richiesta di spostamento
        btnInviaRichiesta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tabellaOrario.getSelectedRow();
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(null, "Seleziona prima una lezione dalla tabella!");
                    return;
                }

                Lezione lezioneSelezionata = lezioni.get(rigaSelezionata);
                GiornoSettimana giornoProp = (GiornoSettimana) cbGiorno.getSelectedItem();
                String oraInizioProp = txtOraInizio.getText();
                String oraFineProp = txtOraFine.getText();

                // Validazione minima dei campi in input direttamente nella GUI
                if (oraInizioProp.isEmpty() || oraFineProp.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Inserisci gli orari proposti!");
                    return;
                }

                // Inoltro della richiesta allo strato di controllo
                Controller.getInstance().inviaRichiestaSpostamento(lezioneSelezionata, giornoProp, oraInizioProp, oraFineProp);
                JOptionPane.showMessageDialog(null, "Richiesta inviata al responsabile!");

                txtOraInizio.setText("");
                txtOraFine.setText("");
            }
        });

        // Evento di Logout
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.getInstance().effettuaLogout();
                new LoginFrame().setVisible(true);
                dispose(); // Libera la memoria della finestra
            }
        });
    }

}