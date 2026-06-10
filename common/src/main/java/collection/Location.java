package collection;

import java.io.Serializable;

/**
 * Класс, представляющий географическую локацию (точку).
 * Может использоваться как начальная (from) или конечная (to) точка маршрута.
 * Поле name может быть null.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public class Location implements Serializable {
    private final Float x; //Поле не может быть null
    private final double y;
    private final String name; //Строка не может быть пустой, Поле может быть null

    public Location(Float x, double y, String name) {
        if (x == null) {
            throw new IllegalArgumentException("x is null");
        }
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public String getName() { return name;}

    public Float getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String toXML() {
        if (name == null) {
            return "null";
        }
        return name + "(" + x + ", " + y + ")";
    }

    @Override
    public String toString() {
        return(name + "(" + x + ",  " + y + ")");
    }
}
