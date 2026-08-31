package mini.os.console;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsoleGUI extends JFrame {

 
    private JTextArea areaSalida;
    private JTextField campoEntrada;
    private JLabel labelPrompt;

   
    private String rutaActual = "";

    
    public interface ComandoListener {
        void onComandoIngresado(String comandoCompleto);
    }

    private ComandoListener listener;

    public ConsoleGUI() {
        
        setTitle("Simulador de Consola CMD");
        setSize(800, 500);
        setMinimumSize(new Dimension(700, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        
        Color colorFondo = Color.BLACK;
        Color colorTexto = Color.WHITE; 
        Font fuenteConsola = new Font("Consolas", Font.PLAIN, 14);

        
        areaSalida = new JTextArea();
        areaSalida.setEditable(false);
        areaSalida.setBackground(colorFondo);
        areaSalida.setForeground(colorTexto);
        areaSalida.setFont(fuenteConsola);
        areaSalida.setMargin(new Insets(10, 10, 5, 10));

      
        DefaultCaret caret = (DefaultCaret) areaSalida.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(areaSalida);
        scrollPane.setBorder(null); 


        JPanel panelEntrada = new JPanel(new BorderLayout(5, 0));
        panelEntrada.setBackground(colorFondo);
        panelEntrada.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        labelPrompt = new JLabel(getPromptText());
        labelPrompt.setForeground(colorTexto);
        labelPrompt.setFont(fuenteConsola);

        campoEntrada = new JTextField();
        campoEntrada.setBackground(colorFondo);
        campoEntrada.setForeground(colorTexto);
        campoEntrada.setCaretColor(colorTexto); 
        campoEntrada.setFont(fuenteConsola);
        campoEntrada.setBorder(null); 

        panelEntrada.add(labelPrompt, BorderLayout.WEST);
        panelEntrada.add(campoEntrada, BorderLayout.CENTER);

        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(panelEntrada, BorderLayout.SOUTH);

        
        imprimirTexto("Microsoft Windows [Versión 10.0.19045.3803]\n(c) Microsoft Corporation. Todos los derechos reservados.\n");

        
        campoEntrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = campoEntrada.getText();
                campoEntrada.setText("");

              
                imprimirTexto(getPromptText() + input);

                if (input.trim().equals("Cls")) {
                    limpiarPantalla();
                } else if (listener != null) {
            
                    listener.onComandoIngresado(input);
                } else {
                    
                    procesarComandoPrueba(input);
                }

                
                campoEntrada.requestFocusInWindow();
            }
        });
    }

    

   
    private String getPromptText() {
        return rutaActual + ">";
    }

 
    public void imprimirTexto(String texto) {
        areaSalida.append(texto + "\n");
    }

   
    public void limpiarPantalla() {
        areaSalida.setText("");
    }

   
    public void setRutaActual(String nuevaRuta) {
        this.rutaActual = nuevaRuta;
        labelPrompt.setText(getPromptText());
    }

  
    public void setComandoListener(ComandoListener listener) {
        this.listener = listener;
    }

    
    private void procesarComandoPrueba(String comando) {
        if (comando.trim().isEmpty()) return;
        imprimirTexto("-> [GUI Lista]: Esperando conexión con la lógica para el comando: \"" + comando + "\"");
    }

}