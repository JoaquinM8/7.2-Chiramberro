import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import unidad1.c26_4_6.*;

public class TP1 extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public TP1() {
        setTitle("Unidad 1 Programs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createProgram1Panel(), "program1");
        mainPanel.add(createProgram2Panel(), "program2");
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btn1 = new JButton("Program 1 - Description");
        btn1.addActionListener(e -> new Ejercicio1_1().setVisible(true));
        
        JButton btn2 = new JButton("Program 2 - Description");
        btn2.addActionListener(e -> new Ejercicio1_2().setVisible(true));
        
        panel.add(btn1);
        panel.add(btn2);
        
        return panel;
    }
    
    private JPanel createProgram1Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        
        JLabel content = new JLabel("Program 1 Content");
        panel.add(content, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProgram2Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        
        JLabel content = new JLabel("Program 2 Content");
        panel.add(content, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TP1());
    }
}