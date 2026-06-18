package gui;

import controller.Controller;
import model.Responsabile;
import model.RichiestaSpostamento;
import model.StatoRichiesta;

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
 * Finestra di Boundary adibita al ruolo gestionale del Responsabile (Coordinatore).
 * <p>
 * Offre una panoramica delle proposte di spostamento inviate dai docenti e
 * mette a disposizione gli eventi di approvazione o rifiuto dei ticket,
 * oltre ai collegamenti per la definizione di corsi ed aule.
 * </p>
 */
public class DashboardResponsabile extends JFrame {
    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JTable tabellaRichieste;
    private JButton btnApprova;
    private JButton btnRifiuta;
    private JButton btnLogout;
    private JButton btnRegistra;
    private JButton btnPianifica;

    private List<RichiestaSpostamento> richiesteAttive;
    private DefaultTableModel tableModel;

    /**
     * Costruttore della classe. Inizializza i componenti visivi e registra
     * gli ascoltatori degli eventi per l'approvazione, il rifiuto e per l'apertura
     * dei frame secondari di registrazione e pianificazione lezioni.
     */
    public DashboardResponsabile() {
        setContentPane(mainPanel);
        setTitle("Area Coordinatore / Responsabile Orari");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Responsabile respLoggato = (Responsabile) Controller.getInstance().getUtenteLoggato();
        lblBenvenuto.setText("Pannello Coordinatore: Prof. " + respLoggato.getNome() + " " + respLoggato.getCognome());

        // Inizializzazione griglia richieste pendenti
        String[] colonne = {"Materia", "Docente Richiedente", "Giorno Prop.", "Ora Inizio Prop.", "Ora Fine Prop."};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaRichieste.setModel(tableModel);

        aggiornaTabella();

        // 1. Apertura della finestra di Registrazione utente
        btnRegistra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrazioneFrame regFrame = new RegistrazioneFrame(DashboardResponsabile.this);
                regFrame.setVisible(true);
                setVisible(false); // Passa il controllo nascondendo il chiamante
            }
        });

        // 2. Apertura della finestra di Pianificazione nuove lezioni
        btnPianifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InserimentoLezioneFrame lezFrame = new InserimentoLezioneFrame(DashboardResponsabile.this);
                lezFrame.setVisible(true);
                setVisible(false); // Passa il controllo nascondendo il chiamante
            }
        });

        // 3. Approvazione dello spostamento orario (Slide 21)
        btnApprova.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = tabellaRichieste.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(null, "Seleziona una richiesta da approvare!");
                    return;
                }
                RichiestaSpostamento r = richiesteAttive.get(riga);
                Controller.getInstance().valutaRichiesta(r, StatoRichiesta.APPROVATA);
                JOptionPane.showMessageDialog(null, "Richiesta approvata! L'orario delle lezioni è stato aggiornato.");
                aggiornaTabella();
            }
        });

        // 4. Rifiuto della richiesta di spostamento
        btnRifiuta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int riga = tabellaRichieste.getSelectedRow();
                if (riga == -1) {
                    JOptionPane.showMessageDialog(null, "Seleziona una richiesta da rifiutare!");
                    return;
                }
                RichiestaSpostamento r = richiesteAttive.get(riga);
                Controller.getInstance().valutaRichiesta(r, StatoRichiesta.RIFIUTATA);
                JOptionPane.showMessageDialog(null, "Richiesta rifiutata.");
                aggiornaTabella();
            }
        });

        // 5. Logout di sistema
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.getInstance().effettuaLogout();
                new LoginFrame().setVisible(true);
                dispose(); // Distrugge la dashboard liberando le risorse
            }
        });
    }

    /**
     * Aggiorna a runtime la tabella delle richieste attive in sospeso recuperando
     * i dati aggiornati dallo strato di controllo.
     */
    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        richiesteAttive = Controller.getInstance().getRichiesteInAttesa();
        for (RichiestaSpostamento r : richiesteAttive) {
            tableModel.addRow(new Object[]{
                    r.getLezioneDaSpostare().getInsegnamento().getNome(),
                    r.getLezioneDaSpostare().getInsegnamento().getDocenteTitolare().getCognome(),
                    r.getGiornoProposto(),
                    r.getOraInizioProposta(),
                    r.getOraFineProposta()
            });
        }
    }

}