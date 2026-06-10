package collection;

import java.io.Serializable;

/**
 * Класс, представляющий координаты точки в пространстве.
 * Используется в объектах {@link Route}.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public class Coordinates implements Serializable {
    private final Long x; //Значение поля должно быть больше -248, Поле не может быть null
    private final Integer y; //Значение поля должно быть больше -448, Поле не может быть null

    public Coordinates(Long x, Integer y) {
        if (x < -248 || x == null) {
            throw new IllegalArgumentException("x is out of range");
        }
        if (y < -448 || y == null) {
            throw new IllegalArgumentException("y is out of range");
        }
        this.x = x;
        this.y = y;
    }
    public Long getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Collection.Coordinates{" + "x=" + x + ", y=" + y + '}';
    }
}
