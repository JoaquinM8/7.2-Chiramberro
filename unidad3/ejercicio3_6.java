package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_6 extends JFrame {

    int multiplo = 0;
    int i = 0;

    public ejercicio3_6() {
        setTitle("Ejercicio 3.6");
        setSize(480,222);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
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

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ejercicio3_6());
    }
}