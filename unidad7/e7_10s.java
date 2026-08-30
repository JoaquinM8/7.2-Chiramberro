package unidad7;

import javax.swing.*;
import java.awt.*;
import unidad7.maquinaria.*;
import unidad7.personal.*;

public class e7_10s extends JPanel {
    public static void mostrarEnVentana() {
        JFrame ventana = new JFrame("Tren");
        ventana.setSize(620, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(new e7_10s());
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e7_10s::mostrarEnVentana);
    }

    public e7_10s() {
        setLayout(new BorderLayout(10, 10));
        JPanel datos = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField nombreMecanico = new JTextField();
        JTextField telefono = new JTextField();
        JTextField especialidad = new JTextField();
        JTextField nombreMaquinista = new JTextField();
        JTextField dniMaquinista = new JTextField();
        JTextField sueldo = new JTextField();
        JTextField rango = new JTextField();
        JTextField matricula = new JTextField();
        JTextField potencia = new JTextField();
        JTextField año = new JTextField();
        JTextField nombreJefe = new JTextField();
        JTextField dniJefe = new JTextField();

        datos.add(new JLabel("Nombre mecanico:")); datos.add(nombreMecanico);
        datos.add(new JLabel("Telefono mecanico:")); datos.add(telefono);
        datos.add(new JLabel("Especialidad:")); datos.add(especialidad);
        datos.add(new JLabel("Nombre maquinista:")); datos.add(nombreMaquinista);
        datos.add(new JLabel("DNI maquinista:")); datos.add(dniMaquinista);
        datos.add(new JLabel("Sueldo mensual:")); datos.add(sueldo);
        datos.add(new JLabel("Rango:")); datos.add(rango);
        datos.add(new JLabel("Matricula locomotora:")); datos.add(matricula);
        datos.add(new JLabel("Potencia:")); datos.add(potencia);
        datos.add(new JLabel("Año fabricacion:")); datos.add(año);
        datos.add(new JLabel("Nombre jefe estacion:")); datos.add(nombreJefe);
        datos.add(new JLabel("DNI jefe estacion:")); datos.add(dniJefe);

        JTextArea resultado = new JTextArea(10, 45);
        resultado.setEditable(false);
        JButton crear = new JButton("Crear tren");
        crear.addActionListener(e -> {
            try {
                Mecanico mecanico = new Mecanico(nombreMecanico.getText(), telefono.getText(), especialidad.getText());
                Maquinista maquinista = new Maquinista(nombreMaquinista.getText(), dniMaquinista.getText(), Double.parseDouble(sueldo.getText()), rango.getText());
                Locomotora locomotora = new Locomotora(matricula.getText(), Integer.parseInt(potencia.getText()), Integer.parseInt(año.getText()), mecanico);
                Tren tren = new Tren(locomotora, maquinista);
                JefeEstacion jefe = new JefeEstacion(nombreJefe.getText(), dniJefe.getText());
                resultado.setText("Tren creado\nLocomotora: " + tren.locomotora.matricula
                        + "\nMaquinista: " + tren.maquinista.nombreCompleto
                        + "\nMecanico: " + mecanico.nombreCompleto
                        + "\nJefe de estacion: " + jefe.nombreCompleto);
            } catch (NumberFormatException error) {
                resultado.setText("Sueldo, potencia y año deben ser numeros.");
            }
        });
        JPanel acciones = new JPanel();
        acciones.add(crear);
        add(datos, BorderLayout.NORTH);
        add(acciones, BorderLayout.CENTER);
        add(new JScrollPane(resultado), BorderLayout.SOUTH);
    }
}
