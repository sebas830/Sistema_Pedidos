package model;

public class PedidoItem {
    public Plato plato;
    public int cantidad;
    public double extras;
    public double subtotal;

    public PedidoItem(Plato p, int c, double extras, double subtotal) {
        this.plato = p;
        this.cantidad = c;
        this.extras = extras;
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return String.format("%s x%d -> %.0f", plato.nombre, cantidad, subtotal);
    }
}