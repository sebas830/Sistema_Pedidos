package model;

public class Plato {
    public String nombre;
    public double precio;
    public int calorias;
    public String descripcion;

    public Plato(String nombre, double precio, int calorias, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.calorias = calorias;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}