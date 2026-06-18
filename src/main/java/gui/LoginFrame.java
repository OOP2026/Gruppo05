package gui;

import controller.Controller;
import model.Docente;
import model.Responsabile;
import model.Studente;
import model.Utente;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * Classe di Boundary che rappresenta la finestra principale di accesso al sistema.
 * <p>
 * Consente l'autenticazione degli utenti mediante login e password,
 * delegando la validazione al Controller ed effettuando il reindirizzamento
 * corretto in base al ruolo dell'utente loggato.
 * </p>
 */
public class LoginFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblErrore;
    private JButton btnRegistra;

    /**
     * Costruttore della classe. Inizializza i parametri del JFrame, imposta
     * il pannello principale associato al file .form e registra gli ActionListener
     * per gestire i click sui pulsanti di autenticazione e di registrazione.
     */
    public LoginFrame() {
        setContentPane(mainPanel);
        setTitle("Sistema Orario Lezioni - Login");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo

        // Listener reattivo per la gestione dell'evento di login (Slide 21)
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = txtLogin.getText();
                String password = new String(txtPassword.getPassword());

                // Delega l'elaborazione dell'autenticazione al Controller (Slide 34)
                boolean successo = Controller.getInstance().effettuaLogin(login, password);

                if (successo) {
                    lblErrore.setText("");
                    Utente loggato = Controller.getInstance().getUtenteLoggato();

                    // Reindirizzamento dinamico basato sul ruolo dell'utente loggato
                    if (loggato instanceof Studente) {
                        DashboardStudente dashboard = new DashboardStudente();
                        dashboard.setVisible(true);
                        dispose(); // Dealloca il login per risparmiare memoria (Slide 36)
                    } else if (loggato instanceof Responsabile) {
                        DashboardResponsabile dashboardResponsabile = new DashboardResponsabile();
                        dashboardResponsabile.setVisible(true);
                        dispose();
                    } else if (loggato instanceof Docente) {
                        DashboardDocente dashboardDocente = new DashboardDocente();
                        dashboardDocente.setVisible(true);
                        dispose();
                    }
                } else {
                    lblErrore.setText("Credenziali errate! Riprova.");
                }
            }
        });

        // Listener per aprire la finestra di registrazione (Slide 30)
        btnRegistra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrazioneFrame regFrame = new RegistrazioneFrame(LoginFrame.this);
                regFrame.setVisible(true);
                setVisible(false); // Rende invisibile il frame chiamante (Slide 30)
            }
        });
    }

    /**
     * Punto di ingresso principale  per l'avvio dell'intera applicazione.
     *
     * @param args Argomenti passati da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            }
        });
    }

}