import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TP2 extends JFrame {

    public TP2() {
        setTitle("TP 2");
        setSize(720,480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(Menu());
        setVisible(true);
    }

    private Border crearBordeDefault(Integer top, Integer left, Integer bottom, Integer right) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(top,left,bottom,right),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), 
                BorderFactory.createEmptyBorder(top,left,bottom,right)
                )
            );
    }

    private JPanel Menu() {

        JPanel panel = new JPanel(new BorderLayout());

        CardLayout navegador = new CardLayout();
        JPanel contenedor = new JPanel(navegador);

        contenedor.add(inicio(), "Inicio");

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(crearBordeDefault(10,10,10,10));

        JLabel lblTitulo = new JLabel("PROGRAMAS", SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel buttonPanel = new JPanel(new GridLayout(3,3,10,5));

        JButton ex1Button = new JButton("Detector de pares / impares");
        JButton ex2Button = new JButton("Comparador de números");
        JButton ex3Button = new JButton("Comparador 2.0");
        JButton ex4Button = new JButton("Comparador 3.0");
        JButton ex5Button = new JButton("Casi cero");
        JButton ex6Button = new JButton("Comparador 4.0");
        JButton ex7Button = new JButton("Ordenar números");
        JButton ex8Button = new JButton("Clasificar notas");
        JButton ex9Button = new JButton("Validar fecha");

        buttonPanel.add(ex1Button);
        buttonPanel.add(ex2Button);
        buttonPanel.add(ex3Button);
        buttonPanel.add(ex4Button);
        buttonPanel.add(ex5Button);
        buttonPanel.add(ex6Button);
        buttonPanel.add(ex7Button);
        buttonPanel.add(ex8Button);
        buttonPanel.add(ex9Button);

        ex1Button.addActionListener(e -> {contenedor.add(parImpar(), "ParImpar"); navegador.show(contenedor, "ParImpar");});
        ex2Button.addActionListener(e -> {contenedor.add(comparadorV1(), "ComparadorV1"); navegador.show(contenedor, "ComparadorV1");});
        ex3Button.addActionListener(e -> {contenedor.add(comparadorV2(), "ComparadorV2"); navegador.show(contenedor, "ComparadorV2");});
        ex4Button.addActionListener(e -> {contenedor.add(comparadorV3(), "ComparadorV3"); navegador.show(contenedor, "ComparadorV3");});
        ex5Button.addActionListener(e -> {contenedor.add(casiCero(), "CasiCero"); navegador.show(contenedor, "CasiCero");});
        ex6Button.addActionListener(e -> {contenedor.add(comparadorV4(), "ComparadorV4"); navegador.show(contenedor, "ComparadorV4");});
        ex7Button.addActionListener(e -> {contenedor.add(ordenarNumeros(), "OrdenarNumeros"); navegador.show(contenedor, "OrdenarNumeros");});
        ex8Button.addActionListener(e -> {contenedor.add(clasificarNotas(), "ClasificarNota"); navegador.show(contenedor, "ClasificarNota");});
        ex9Button.addActionListener(e -> {contenedor.add(validarFecha(), "ValidarFecha"); navegador.show(contenedor, "ValidarFecha");});

        menuPanel.add(lblTitulo, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(menuPanel, BorderLayout.NORTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(contenedor, BorderLayout.CENTER);

        navegador.show(contenedor, "Inicio");

        return panel;
    }

    private JPanel inicio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JPanel wordPanel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Seleccione un progama", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 50));
        wordPanel.setBackground(Color.gray);


        wordPanel.add(lblTitulo, BorderLayout.CENTER);
        panel.add(wordPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel parImpar() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Detector de pares o impares", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JTextField numField = new JTextField(5);
            numField.setFont(new Font("Arial", Font.PLAIN, 20));
            numField.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton calcButton = new JButton("Calcular");

            inputPanel.add(numField);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 120));

            resultPanel.add(resultLabel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int num = Integer.parseInt(numField.getText());
                if (num%2==0) {
                    resultLabel.setText("PAR");
                    resultLabel.setForeground(Color.green.darker());
                } else {
                    resultLabel.setText("IMPAR");
                    resultLabel.setForeground(Color.ORANGE);               
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel comparadorV1() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Comparador 1.0", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(2,1,5,5));

                JTextField numField1 = new JTextField(5);
                numField1.setFont(new Font("Arial", Font.PLAIN, 20));
                numField1.setHorizontalAlignment(SwingConstants.CENTER);
                
                JTextField numField2 = new JTextField(5);
                numField2.setFont(new Font("Arial", Font.PLAIN, 20));
                numField2.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(numField1);
                textPanel.add(numField2);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 75));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int num1 = Integer.parseInt(numField1.getText());
                int num2 = Integer.parseInt(numField2.getText());
                if (num1 == num2) {
                    resultLabel.setText("IGUALES");
                    resultLabel.setForeground(Color.green.darker());
                } else {
                    resultLabel.setText("DISTINTOS");     
                    resultLabel.setForeground(Color.ORANGE);     
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel comparadorV2() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Comparador 2.0", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(2,1,5,5));

                JTextField numField1 = new JTextField(5);
                numField1.setFont(new Font("Arial", Font.PLAIN, 20));
                numField1.setHorizontalAlignment(SwingConstants.CENTER);
                
                JTextField numField2 = new JTextField(5);
                numField2.setFont(new Font("Arial", Font.PLAIN, 20));
                numField2.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(numField1);
                textPanel.add(numField2);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 75));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int num1 = Integer.parseInt(numField1.getText());
                int num2 = Integer.parseInt(numField2.getText());
                if (num1 > num2) {
                    resultLabel.setText(num1 + " > " + num2);
                } else if (num1 < num2) {
                    resultLabel.setText(num1 + " < " + num2);
                } else {
                    JOptionPane.showMessageDialog(panel, "Función no disponible en esta versión", "Información", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel comparadorV3() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Comparador 3.0", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(2,1,5,5));

                JTextField numField1 = new JTextField(5);
                numField1.setFont(new Font("Arial", Font.PLAIN, 20));
                numField1.setHorizontalAlignment(SwingConstants.CENTER);
                
                JTextField numField2 = new JTextField(5);
                numField2.setFont(new Font("Arial", Font.PLAIN, 20));
                numField2.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(numField1);
                textPanel.add(numField2);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 75));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int num1 = Integer.parseInt(numField1.getText());
                int num2 = Integer.parseInt(numField2.getText());
                if (num1 > num2) {
                    resultLabel.setText(num1 + " > " + num2);
                } else if (num1 < num2) {
                    resultLabel.setText(num1 + " < " + num2);
                } else {
                    resultLabel.setText(num1 + " = " + num2);
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel casiCero() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Detector Casi cero", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JTextField numField = new JTextField(5);
            numField.setFont(new Font("Arial", Font.PLAIN, 20));
            numField.setHorizontalAlignment(SwingConstants.CENTER);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(numField);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("Casi cero: ", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 50));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                double num = Double.parseDouble(numField.getText());
                if (num == 0) {
                    resultLabel.setText("Casi cero: 0");
                    resultLabel.setForeground(Color.black);     
                } else
                if (-1 < num && num < 1) {
                    resultLabel.setText("Casi cero: SI");
                } else {
                    resultLabel.setText("Casi cero: NO");        
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("Casi cero: ");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel comparadorV4() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JPanel tituloPanel = new JPanel(new BorderLayout());

            JLabel tituloLabel = new JLabel("Comparador 4.0", SwingConstants.CENTER);
            tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

            JLabel detalleLabel = new JLabel("Con operador ternario", SwingConstants.CENTER);
            detalleLabel.setFont(new Font("Arial", Font.ITALIC, 12));

            tituloPanel.add(tituloLabel, BorderLayout.NORTH);
            tituloPanel.add(detalleLabel, BorderLayout.SOUTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(2,1,5,5));

                JTextField numField1 = new JTextField(5);
                numField1.setFont(new Font("Arial", Font.PLAIN, 20));
                numField1.setHorizontalAlignment(SwingConstants.CENTER);
                
                JTextField numField2 = new JTextField(5);
                numField2.setFont(new Font("Arial", Font.PLAIN, 20));
                numField2.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(numField1);
                textPanel.add(numField2);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 75));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {
                int num1 = Integer.parseInt(numField1.getText());
                int num2 = Integer.parseInt(numField2.getText());
                String result = (num1 == num2) ? num1 + " = " + num2 : (num1 > num2) ? num1 + " > " + num2 : num1 + " < " + num2;
                resultLabel.setText(result);
            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel ordenarNumeros() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Ordenar Números", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(3,1,5,5));

                JTextField numField1 = new JTextField(5);
                numField1.setFont(new Font("Arial", Font.PLAIN, 20));
                numField1.setHorizontalAlignment(SwingConstants.CENTER);
                
                JTextField numField2 = new JTextField(5);
                numField2.setFont(new Font("Arial", Font.PLAIN, 20));
                numField2.setHorizontalAlignment(SwingConstants.CENTER);

                JTextField numField3 = new JTextField(5);
                numField3.setFont(new Font("Arial", Font.PLAIN, 20));
                numField3.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(numField1);
                textPanel.add(numField2);
                textPanel.add(numField3);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 75));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int num1 = Integer.parseInt(numField1.getText());
                int num2 = Integer.parseInt(numField2.getText());
                int num3 = Integer.parseInt(numField3.getText());

                if (num1 > num2) {
                    if (num2 > num3) {
                        resultLabel.setText(num1 + " > " + num2 + " > " + num3);
                    } else if (num1 == num3) {
                        resultLabel.setText(num1 + " = " + num3 + " > " + num2);
                    } else if (num2 == num3) {
                        resultLabel.setText(num1 + " > " + num2 + " = " + num3);
                    } else {
                        resultLabel.setText(num3 + " > " + num1 + " > " + num2);
                    }
                } else if (num1 < num2) {
                    if (num1 > num3) {
                        resultLabel.setText(num2 + " > " + num1 + " > " + num3);
                    } else if (num1 == num3) {
                        resultLabel.setText(num2 + " > " + num1 + " = " + num3);
                    } else if (num2 == num3) {
                        resultLabel.setText(num2 + " = " + num3 + " > " + num1);
                    } else {
                        resultLabel.setText(num3 + " > " + num2 + " > " + num1);
                    }
                } else {
                    if (num1 > num3) {
                        resultLabel.setText(num1 + " = " + num2 + " > " + num3);
                    } else if (num1 < num3) {
                        resultLabel.setText(num3 + " > " + num1 + " = " + num2);
                    } else {
                        resultLabel.setText(num1 + " = " + num2 + " = " + num3);
                    }
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel clasificarNotas() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Clasificar Nota", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JTextField notaField = new JTextField(5);
            notaField.setFont(new Font("Arial", Font.PLAIN, 20));
            notaField.setHorizontalAlignment(SwingConstants.CENTER);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(notaField);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 50));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                double nota = Double.parseDouble(notaField.getText());
                
                if (nota < 0 || nota > 10) {
                    resultLabel.setText("");
                    JOptionPane.showMessageDialog(panel, "La nota debe estar entre 0 y 10", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (nota < 5) {
                    resultLabel.setText("Insuficiente");
                    resultLabel.setForeground(Color.red.darker());
                } else if (nota < 6) {
                    resultLabel.setText("Suficiente");
                    resultLabel.setForeground(Color.ORANGE);
                } else if (nota < 7) {
                    resultLabel.setText("Bueno");
                    resultLabel.setForeground(Color.green);
                } else if (nota < 9) {
                    resultLabel.setText("Notable");
                    resultLabel.setForeground(Color.green.darker());
                } else {
                    resultLabel.setText("Sobresaliente");
                    resultLabel.setForeground(Color.cyan.darker());
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel validarFecha() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBordeDefault(10, 10, 10, 10));

        JLabel tituloLabel = new JLabel("Validar fecha", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
            JPanel textPanel = new JPanel(new GridLayout(3,2,5,5));

                JLabel diaLabel = new JLabel("Día:", SwingConstants.CENTER);
                diaLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JLabel mesLabel = new JLabel("Mes:", SwingConstants.CENTER);
                mesLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JLabel añoLabel = new JLabel("Año:", SwingConstants.CENTER);
                añoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JTextField diaField = new JTextField(5);
                diaField.setFont(new Font("Arial", Font.PLAIN, 15));
                diaField.setHorizontalAlignment(SwingConstants.CENTER);

                JTextField mesField = new JTextField(5);
                mesField.setFont(new Font("Arial", Font.PLAIN, 15));
                mesField.setHorizontalAlignment(SwingConstants.CENTER);

                JTextField añoField = new JTextField(5);
                añoField.setFont(new Font("Arial", Font.PLAIN, 15));
                añoField.setHorizontalAlignment(SwingConstants.CENTER);

                textPanel.add(diaLabel);
                textPanel.add(diaField);
                textPanel.add(mesLabel);
                textPanel.add(mesField);
                textPanel.add(añoLabel);
                textPanel.add(añoField);

            JButton calcButton = new JButton("Calcular");

            inputPanel.add(textPanel);
            inputPanel.add(calcButton);


        JPanel resultPanel = new JPanel(new BorderLayout());

            JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 20));

            resultPanel.add(resultLabel, BorderLayout.CENTER);
            
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 20);
        centerPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(resultPanel, gbc);



        calcButton.addActionListener(e -> {

            try {

                int dia = Integer.parseInt(diaField.getText());
                int mes = Integer.parseInt(mesField.getText());
                int año = Integer.parseInt(añoField.getText());

                if (mes == 2 && dia > 28) {
                    resultLabel.setText("Febrero no puede tener más de 28 días.");
                    resultLabel.setFont(new Font("Arial", Font.ITALIC, 15));
                    resultLabel.setForeground(Color.red);
                } else if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
                    resultLabel.setText("El mes seleccionado no puede tener más de 30 días.");
                    resultLabel.setFont(new Font("Arial", Font.ITALIC, 15));
                    resultLabel.setForeground(Color.red);
                } else if (dia < 1 || dia > 31 || mes < 1 || mes > 12) {
                    resultLabel.setText("Día o mes fuera de rango.");
                    resultLabel.setFont(new Font("Arial", Font.ITALIC, 15));
                    resultLabel.setForeground(Color.red);
                } else {
                    resultLabel.setText("Fecha válida: " + dia + "/" + mes + "/" + año);
                    resultLabel.setFont(new Font("Arial", Font.BOLD, 25));
                    resultLabel.setForeground(Color.black);
                }


            } catch (NumberFormatException ex) {
                resultLabel.setText("");
                JOptionPane.showMessageDialog(panel, "Error garrafal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TP2());
    }

}