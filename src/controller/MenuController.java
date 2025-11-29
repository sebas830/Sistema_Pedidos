package controller;

import model.Plato;
import javax.swing.*;

public class MenuController {

    public DefaultListModel<Plato> cargarMenu() {
        DefaultListModel<Plato> m = new DefaultListModel<>();

        m.addElement(new Plato("Bruschetta", 12000, 250, "Pan tostado con tomate, albahaca y aceite de oliva. Recomendación: tomar al inicio"));
        m.addElement(new Plato("Ensalada Cesar", 15000, 320, "Lechuga, pollo, croutons, salsa cesar y queso parmesano. Recomendación: plato ligero."));
        m.addElement(new Plato("Pollo a la plancha", 28000, 600, "Pechuga de pollo marinada, acompañada de vegetales. Recomendación: acompañar con arroz"));
        m.addElement(new Plato("Lasaña de carne", 32000, 900, "Capas de pasta, carne, bechamel y queso gratinado. Recomendación: plato fuerte."));
        m.addElement(new Plato("Limonada", 7000, 120, "Limonada natural con hierbabuena. Recomendación: bebida refrescante."));
        m.addElement(new Plato("Café espresso", 6000, 5, "Café oscuro, recomendado después de la comida. Recomendación: tomar con pan."));

        return m;
    }
}
