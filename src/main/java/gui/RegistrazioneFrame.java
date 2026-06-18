package gui;

import controller.Controller;
import model.AnnoCorso;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Finestra di Boundary dedita all'inserimento e alla registrazione di nuove
 * anagrafiche utente (Studenti o Docenti) all'interno della base di dati.
 * <p>
 * Implementa la tecnica accademica della comunicazione unidirezionale tra JFrame
 * conservando il riferimento al frame chiamante.
 * </p>
 */
public class RegistrazioneFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtEmail;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRuolo;
    private JComboBox<AnnoCorso> cbAnno;
    private JButton btnSalva;
    private JButton btnAnnulla;
    private JFrame frameChiamante; // Riferimento al frame chiamante per ristabilire controllo

    /**
     * Costruttore della classe.
     *
     * @param frameChiamante Il JFrame genitore che ha originato la chiamata
     */
    public RegistrazioneFrame(JFrame frameChiamante) {
        this.frameChiamante = frameChiamante;

        setContentPane(mainPanel);
        setTitle("Registrazione Nuovo Utente");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(frameChiamante);

        // Popolamento manuale delle scelte
        cbRuolo.addItem("STUDENTE");
        cbRuolo.addItem("DOCENTE");
        cbRuolo.addItem("RESPONSABILE");

        for (AnnoCorso a : AnnoCorso.values()) {
            cbAnno.addItem(a);
        }

        cbAnno.setEnabled(true);

        // Abilitazione o disabilitazione dinamica dell'anno in base al ruolo selezionato
        cbRuolo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isStudente = cbRuolo.getSelectedItem().toString().equals("STUDENTE");
                cbAnno.setEnabled(isStudente);
            }
        });

        // Invio dei dati anagrafici per la persistenza transazionale
        btnSalva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText();
                String cognome = txtCognome.getText();
                String email = txtEmail.getText();
                String login = txtLogin.getText();
                String pwd = new String(txtPassword.getPassword());
                String ruolo = cbRuolo.getSelectedItem().toString();
                String anno = cbAnno.getSelectedItem().toString();

                // Validazione minima dei campi obbligatori
                if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || login.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Compila tutti i campi obbligatori!");
                    return;
                }

                // Chiamata al Controller per l'inserimento fisico
                boolean successo = Controller.getInstance().registraNuovoUtente(nome, cognome, email, login, pwd, ruolo, anno);
                if (successo) {
                    JOptionPane.showMessageDialog(null, "Nuovo utente registrato con successo nel Database!");
                    tornaIndietro();
                } else {
                    JOptionPane.showMessageDialog(null, "Errore: Impossibile completare la registrazione.");
                }
            }
        });

        // Evento di annullamento
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tornaIndietro();
            }
        });
    }

    /**
     * Restituisce la visibilità al frame chiamante e distrugge la finestra
     * corrente per deallocare la memoria della JVM
     */
    private void tornaIndietro() {
        frameChiamante.setVisible(true); // Restituisce visibilità alla Dashboard
        dispose(); // Dealloca l'oggetto corrente
    }

}