package ui;

import controller.MenuController;
import controller.PedidoController;
import model.PedidoItem;
import model.Plato;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class MainApp extends JFrame {

    // Controladores
    MenuController menuCtrl = new MenuController();
    PedidoController pedidoCtrl = new PedidoController();

    // Modelos
    DefaultListModel<Plato> modeloMenu = new DefaultListModel<>();
    DefaultListModel<String> modeloPedidoList = new DefaultListModel<>();
    DefaultListModel<PedidoItem> itemsPedidoModel = new DefaultListModel<>();
    DefaultTableModel modeloHistorial =
            new DefaultTableModel(new String[]{"N° Pedido", "Fecha", "Total"}, 0);

    // UI (vinculados desde el UI Form)
    private JPanel mainPanel;
    private JComboBox<String> comboCategorias;
    private JList<Plato> listaMenu;
    private JList<String> listaPedido;
    private JLabel lblInfo;
    private JLabel lblSubtotal;
    private JLabel lblTotal;
    private JLabel lblHover;
    private JSpinner spinnerCantidad;
    private JCheckBox chkQueso;
    private JCheckBox chkSalsa;
    private JCheckBox chkPapas;
    private JTextField txtPromo;
    private JTable tablaHistorial;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnConfirmar;

    // Estado inicial
    double totalPedido = 0.0;
    int numeroPedido = 1;
    double descuento = 0.0;


    public MainApp() {
        setContentPane(mainPanel);
        setTitle("Restaurante");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cargar menú
        modeloMenu = menuCtrl.cargarMenu();
        listaMenu.setModel(modeloMenu);

        // Cargar tabla historial
        tablaHistorial.setModel(modeloHistorial);

        initUI();
    }

    // Inicializar UI
    void initUI() {

        spinnerCantidad.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        // Categorías
        comboCategorias.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filtrarCategorias(comboCategorias.getSelectedItem().toString());
            }
        });

        // Selección de menú
        listaMenu.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Plato p = listaMenu.getSelectedValue();
                if (p != null) {
                    lblInfo.setText("Precio: " + p.precio + " | Calorías: " + p.calorias);
                    actualizarSubtotal();
                }
            }
        });

        // Hover
        listaMenu.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int idx = listaMenu.locationToIndex(e.getPoint());
                if (idx != -1) {
                    lblHover.setText("Seleccionado: " + modeloMenu.getElementAt(idx).nombre);
                } else {
                    lblHover.setText("Seleccionado: -");
                }
            }
        });

        // Dobleclic descripción
        listaMenu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Plato p = listaMenu.getSelectedValue();
                    if (p != null) {
                        JOptionPane.showMessageDialog(MainApp.this,
                                p.descripcion, p.nombre,
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        // Recalcular subtotal
        spinnerCantidad.addChangeListener(e -> actualizarSubtotal());
        chkQueso.addItemListener(e -> actualizarSubtotal());
        chkSalsa.addItemListener(e -> actualizarSubtotal());
        chkPapas.addItemListener(e -> actualizarSubtotal());

        // Promo
        txtPromo.addActionListener(e -> {
            String code = txtPromo.getText().trim();
            if (code.equalsIgnoreCase("SEBAS15")) {
                descuento = 0.15;
                actualizarTotalPedido();
                JOptionPane.showMessageDialog(this, "Código válido: 15% aplicado");
            } else {
                JOptionPane.showMessageDialog(this, "Código inválido");
            }
        });

        // Agregar pedido
        btnAgregar.addActionListener(e -> {
            Plato p = listaMenu.getSelectedValue();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un plato.");
                return;
            }

            int cant = (int) spinnerCantidad.getValue();

            double extras = pedidoCtrl.calcularExtras(
                    chkQueso.isSelected(),
                    chkSalsa.isSelected(),
                    chkPapas.isSelected()
            );

            double subtotal = pedidoCtrl.calcularSubtotal(p, cant, extras);

            PedidoItem item = new PedidoItem(p, cant, extras, subtotal);
            itemsPedidoModel.addElement(item);
            modeloPedidoList.addElement(item.toString());
            listaPedido.setModel(modeloPedidoList);

            actualizarTotalPedido();
            actualizarTotalesUI();
        });

        // Eliminar del pedido
        btnEliminar.addActionListener(e -> {
            int idx = listaPedido.getSelectedIndex();
            if (idx != -1) {
                itemsPedidoModel.remove(idx);
                modeloPedidoList.remove(idx);
                actualizarTotalPedido();
                actualizarTotalesUI();
            }
        });

        // Confirmar pedido
        btnConfirmar.addActionListener(e -> {
            if (itemsPedidoModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay items.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            int caloriasTot = 0;

            for (int i = 0; i < itemsPedidoModel.size(); i++) {
                PedidoItem it = itemsPedidoModel.get(i);
                sb.append(it.toString()).append("\n");
                caloriasTot += it.plato.calorias * it.cantidad;
            }

            sb.append("\nCalorías totales: ").append(caloriasTot);
            sb.append("\nValor final: ").append(String.format("%.0f", totalPedido));

            JOptionPane.showMessageDialog(this, sb.toString(), "Resumen", JOptionPane.INFORMATION_MESSAGE);

            modeloHistorial.addRow(new Object[]{
                    numeroPedido++,
                    LocalDate.now().toString(),
                    String.format("%.0f", totalPedido)
            });

            itemsPedidoModel.clear();
            modeloPedidoList.clear();
            descuento = 0.0;
            txtPromo.setText("");

            actualizarTotalPedido();
            actualizarTotalesUI();
        });

        actualizarSubtotal();
        actualizarTotalesUI();
    }

    // Filtrar categorías
    void filtrarCategorias(String cat) {
        DefaultListModel<Plato> temp = new DefaultListModel<>();

        for (int i = 0; i < modeloMenu.size(); i++) {
            Plato p = modeloMenu.get(i);

            switch (cat) {
                case "Entradas":
                    if (p.nombre.equalsIgnoreCase("Bruschetta") ||
                            p.nombre.equalsIgnoreCase("Ensalada Cesar"))
                        temp.addElement(p);
                    break;

                case "Platos Fuertes":
                    if (p.nombre.equalsIgnoreCase("Pollo a la plancha") ||
                            p.nombre.equalsIgnoreCase("Lasaña de carne"))
                        temp.addElement(p);
                    break;

                case "Bebidas":
                    if (p.nombre.equalsIgnoreCase("Limonada") ||
                            p.nombre.equalsIgnoreCase("Café espresso"))
                        temp.addElement(p);
                    break;

                default:
                    temp.addElement(p);
            }
        }

        listaMenu.setModel(temp);
    }

    // Actualizar subtotal
    void actualizarSubtotal() {
        Plato p = listaMenu.getSelectedValue();
        if (p == null) {
            lblSubtotal.setText("Subtotal: 0");
            return;
        }

        int cant = (int) spinnerCantidad.getValue();

        double extras = pedidoCtrl.calcularExtras(
                chkQueso.isSelected(),
                chkSalsa.isSelected(),
                chkPapas.isSelected()
        );

        double subtotal = pedidoCtrl.calcularSubtotal(p, cant, extras);

        lblSubtotal.setText("Subtotal: " + String.format("%.0f", subtotal));
    }

    // Actualizar total pedido
    void actualizarTotalPedido() {
        totalPedido = pedidoCtrl.recalcularTotal(itemsPedidoModel, descuento);
    }

    // Actualizar totales en UI
    void actualizarTotalesUI() {
        lblTotal.setText("Total: " + String.format("%.0f", totalPedido));
        actualizarSubtotal();
    }
}