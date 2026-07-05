package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_6 extends JPanel {

    int multiplo = 0;
    int i = 0;

    public ejercicio3_6() {
        
        JLabel titleLabel = new JLabel("MÚLTIPLOS DE 7", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
      
        JTextArea resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);


        while (multiplo < 100) {
            resultArea.append(7 + " x " + i + " = " + multiplo + "\n");
            ++i;
            multiplo = 7 * i;
        } 

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
            JScrollPane scrollPane = new JScrollPane(
                resultArea, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void mostrarEnVentana() {
        JFrame frame = new JFrame("Ejercicio 3.6");
        frame.setSize(480, 222);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ejercicio3_6());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ejercicio3_6::mostrarEnVentana);
    }
}