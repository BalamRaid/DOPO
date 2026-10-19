package presentation; 
 
import domain.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * Ventana principal de NeoFlix: pestañas Listar, Adicionar y Buscar.
 * <p>
 * La presentación delega el trabajo en la fachada del dominio y atiende los
 * errores: los esperados del usuario con mensajes claros, y los inesperados
 * con registro en el log + alerta, para que la aplicación siga siendo usable.
 *
 * @version ECI 2026
 */
public class NeoFlixGUI extends JFrame{


    private static final Dimension PREFERRED_DIMENSION =
                         new Dimension(700,700);

    /** Mensaje genérico para el usuario ante un error inesperado (sin detalles técnicos). */
    private static final String MSG_UNEXPECTED =
        "ALERTA: ocurrió un error inesperado y la búsqueda no pudo completarse. "
      + "El equipo técnico fue notificado. Puede seguir usando NeoFlix.";

    private NeoFlix neoFlix;

    /*List*/
    private JButton buttonList;
    private JButton buttonRestartList;
    private JTextArea textDetails;
    
    /*Add*/
    private JTextField title;
    private JTextField year;   
    private JTextField atttemps;
    private JTextField completed;
    private JTextField votes;
    private JTextField sumVotes;
    private JTextArea  episodes;
    private JButton buttonAdd;
    private JButton buttonRestartAdd;
   
    /*Search*/
    private JTextField textSearch;
    private JTextArea textResults;
    
    
    private NeoFlixGUI(){
        neoFlix=new NeoFlix();
        prepareElements();
        prepareActions();
    }


    private void prepareElements(){
        setTitle("NeoFlix. Series y episodios.");
        title = new JTextField(50);
        year = new JTextField(50);
        atttemps = new JTextField(50);
        completed = new JTextField(50);
        votes = new JTextField(50);
        sumVotes = new JTextField(50);
        episodes = new JTextArea(10, 50);
        episodes.setLineWrap(true);
        episodes.setWrapStyleWord(true);
        
        JTabbedPane etiquetas = new JTabbedPane();
        etiquetas.add("Listar",   prepareAreaList());
        etiquetas.add("Adicionar",  prepareAreaAdd());
        etiquetas.add("Buscar", prepareSearchArea());
        getContentPane().add(etiquetas);
        setSize(PREFERRED_DIMENSION);
        
    }


    private JPanel prepareAreaList(){

        textDetails = new JTextArea(10, 50);
        textDetails.setEditable(false);
        textDetails.setLineWrap(true);
        textDetails.setWrapStyleWord(true);
        JScrollPane scrollArea =
                new JScrollPane(textDetails,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                                
        JPanel  botones = new JPanel();
        buttonList = new JButton("Listar");
        buttonRestartList = new JButton("Limpiar");
        botones.add(buttonList);
        botones.add(buttonRestartList);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollArea, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
     }
     

    private JPanel prepareAreaAdd(){

        JPanel fields = new JPanel(new GridLayout(13,1));
        fields.add(new JLabel("Nombre"));
        fields.add(title);
        fields.add(new JLabel("Año (para series)"));
        fields.add(year);
        fields.add(new JLabel("Intentos de visualizacion (para episodios)"));
        fields.add(atttemps);        
        fields.add(new JLabel("Visualizacion completa (para episodios)"));
        fields.add(completed); 
        fields.add(new JLabel("Numero de votos (para episodios)"));
        fields.add(votes); 
        fields.add(new JLabel("Suma total de votos (para episodios)"));
        fields.add(sumVotes); 
        fields.add(new JLabel("Episodios (para series)"));
         
        JPanel textDetailsPanel = new JPanel();
        textDetailsPanel.setLayout(new BorderLayout());
        textDetailsPanel.add(fields, BorderLayout.NORTH);
        textDetailsPanel.add(episodes, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        buttonAdd = new JButton("Adicionar");
        buttonRestartAdd = new JButton("Limpiar");

        botones.add(buttonAdd);
        botones.add(buttonRestartAdd);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(textDetailsPanel, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    
    private JPanel prepareSearchArea(){

        Box searchLabelArea = Box.createHorizontalBox();
        searchLabelArea.add(new JLabel("Buscar", JLabel.LEFT));
        searchLabelArea.add(Box.createGlue());
        textSearch = new JTextField(50);
        Box searchArea = Box.createHorizontalBox();
        searchArea.add(searchLabelArea);
        searchArea.add(textSearch);
        
        textResults = new JTextArea(10,50);
        textResults.setEditable(false);
        textResults.setLineWrap(true);
        textResults.setWrapStyleWord(true);
        JScrollPane scrollArea = new JScrollPane(textResults,
                                     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel buttonListea = new JPanel();
        buttonListea.setLayout(new BorderLayout());
        buttonListea.add(searchArea, BorderLayout.NORTH);
        buttonListea.add(scrollArea, BorderLayout.CENTER);

        return buttonListea;
    }


    private void prepareActions(){
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev){
                setVisible(false);
                System.exit(0);
            }
        });
        
        /*List*/
        buttonList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                actionList();
            }
        });

        buttonRestartList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                textDetails.setText("");
            }
        });
        
        /*Add*/
        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev){
                actionAdd();                    
            }
        });
        
        buttonRestartAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev){
                clearAddForm();
            }
        });
        
        /*Search*/
        textSearch.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent ev){
                actionSearch();
            }
           
            public void insertUpdate(DocumentEvent ev){
                actionSearch();
            }
            
            public void removeUpdate(DocumentEvent ev){
                actionSearch();
            }
        });
    }    

    
    private void actionList(){
        textDetails.setText(neoFlix.toString());
    }
    
    /**
     * Atiende el botón [Adicionar]: decide si es episodio (sin episodios
     * escritos) o serie, y pide al dominio adicionarlo. Da retroalimentación
     * al usuario: mensaje de confirmación y limpieza del formulario si todo
     * sale bien, o el mensaje del error de validación si no. Los errores
     * esperados del usuario no se registran en el log ni se propagan.
     */
    private void actionAdd(){
        try {
            if (episodes.getText().trim().isEmpty()) {
                neoFlix.addEpisode(title.getText(), "", atttemps.getText(),
                        completed.getText(), votes.getText(), sumVotes.getText());
            } else {
                neoFlix.addSeries(title.getText(), year.getText(), episodes.getText());
            }
            JOptionPane.showMessageDialog(this, "Contenido adicionado",
                    "NeoFlix", JOptionPane.INFORMATION_MESSAGE);
            clearAddForm();
        } catch (NeoFlixException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "NeoFlix", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Limpia todos los campos del formulario de adicionar.
     */
    private void clearAddForm(){
        title.setText("");
        year.setText("");
        atttemps.setText("");
        completed.setText("");
        votes.setText("");
        sumVotes.setText("");
        episodes.setText("");
    }

    /**
     * Atiende la búsqueda por patrón: se dispara con cada tecla del campo
     * (DocumentListener). Los errores esperados no existen aquí; un error
     * INESPERADO (un defecto) se registra en el log para el equipo técnico y
     * se informa al usuario con una alerta en el área de resultados, sin
     * detalles internos y sin diálogo modal (se ejecuta por cada tecla); la
     * aplicación continúa funcionando.
     */
    private void actionSearch(){
        String patronBusqueda=textSearch.getText();
        String answer = "";
        if(patronBusqueda.length() > 0) {
            try {
                answer = neoFlix.search(patronBusqueda);
            } catch (Exception e) {            // inesperada: p. ej. un defecto del dominio
                Log.record(e);                 // para el equipo técnico
                answer = MSG_UNEXPECTED;       // para el usuario, sin detalles
            }
        }
        textResults.setText(answer);
    } 
    
    /**
     * Punto de entrada: instala la red de seguridad global (cualquier error no
     * capturado de los hilos pasa por handleUnexpected) y muestra la ventana.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String args[]){
        Thread.setDefaultUncaughtExceptionHandler(
            new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable error) {
                    handleUnexpected(error);
                }
            });
        NeoFlixGUI gui=new NeoFlixGUI();
        gui.setVisible(true);
    }

    /**
     * Red de seguridad global: registra el incidente para el equipo técnico,
     * avisa al usuario con un mensaje sin detalles internos y, si el error es
     * un Error de la JVM (p. ej. OutOfMemoryError), termina la aplicación con
     * estado 1; para una Exception la aplicación continúa.
     *
     * @param error error no capturado (Exception o Error)
     */
    private static void handleUnexpected(Throwable error){
        Log.record(error instanceof Exception ? (Exception) error : new Exception(error));
        JOptionPane.showMessageDialog(null, MSG_UNEXPECTED, "NeoFlix",
                                      JOptionPane.ERROR_MESSAGE);
        if (error instanceof Error) System.exit(1);   // política: Error de la JVM -> terminar
    }
}
