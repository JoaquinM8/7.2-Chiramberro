package unidad3;

import javax.swing.*;
import java.awt.*;

public class ejercicio3_7 extends JFrame {

    int producto = 1;
    int num = 1;
    int i = 0;

    public ejercicio3_7() {
        setTitle("Ejercicio 3.7");
        setSize(480,222);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel titleLabel = new JLabel("PRODUCTO DE NÚMEROS IMPARES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel opLabel = new JLabel("1 x 3 x 5 x 7 x 9 x 11 x 13 x 15 x 17 x 19", SwingConstants.CENTER);
        opLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        JLabel resultLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 25));


        for (i=1 ; i <= 10 ; ++i) {
            producto = producto * num;
            num += 2;
        } 
        
        resultLabel.setText(resultLabel.getText() + producto);

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
                resultPanel.add(opLabel, BorderLayout.NORTH);
                resultPanel.add(resultLabel, BorderLayout.CENTER);

            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(resultPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ejercicio3_7());
    }
}