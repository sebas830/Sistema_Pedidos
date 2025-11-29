package controller;

import model.PedidoItem;
import model.Plato;
import javax.swing.*;

public class PedidoController {

    public double calcularExtras(boolean queso, boolean salsa, boolean papas) {
        double extra = 0;
        if (queso) extra += 3000;
        if (salsa) extra += 2000;
        if (papas) extra += 2500;
        return extra;
    }

    public double calcularSubtotal(Plato p, int cantidad, double extras) {
        return p.precio * cantidad + extras;
    }

    public double recalcularTotal(DefaultListModel<PedidoItem> items, double descuento) {
        double total = 0;
        for (int i = 0; i < items.size(); i++)
            total += items.get(i).subtotal;

        if (descuento > 0) total *= (1 - descuento);
        return total;
    }
}
