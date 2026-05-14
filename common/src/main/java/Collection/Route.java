package Collection;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс, представляющий маршрут в коллекции.
 * Содержит информацию о маршруте: название, координаты, начальную и конечную точки,
 * расстояние и дату создания.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public class Route<T extends Number> implements Comparable<Route>, Serializable {
    private T id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private final java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Location from; //Поле может быть null
    private final Location to; //Поле может быть null
    private final long distance; //Значение поля должно быть больше 1

    public Route(T id, String name, Coordinates coordinates, Location from, Location to, long distance) {
        this(id, name, coordinates, from, to, distance, LocalDate.now());
    }

    public Route(T id, String name, Coordinates coordinates, Location from, Location to, long distance, LocalDate creationDate) {
        if (coordinates == null) {
            throw new IllegalArgumentException("координаты не могут быть null");
        }
        if (distance <= 1) {
            throw new IllegalArgumentException("дистанция должна быть больше 1 (>1)");
        }
        if (name == null) {
            throw new IllegalArgumentException("имя маршрута не может быть null");
        }
        this.id = id;
        this.coordinates = coordinates;
        this.name = name;
        this.creationDate = creationDate == null ? LocalDate.now() : creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Route(String name, Coordinates coordinates, Location from, Location to, long distance) {
        if (coordinates == null) {
            throw new IllegalArgumentException("координаты не могут быть null");
        }
        if (distance <= 1) {
            throw new IllegalArgumentException("дистанция должна быть больше 1 (>1)");
        }
        if (name == null) {
            throw new IllegalArgumentException("имя маршрута не может быть null");
        }
//        this.id = (T) main.java.main.CollectionManager.getInstance().generateNextId();
        this.coordinates = coordinates;
        this.name = name;
        this.creationDate = LocalDate.now();
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    @Override
    public int compareTo(Route o) {
        return Long.compare(this.distance, o.distance);
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("  <route>\n");
        sb.append("    <id>").append(id).append("</id>\n");
        sb.append("    <name>").append(name).append("</name>\n");
        sb.append("    <coordinates>\n");
        sb.append("      <x>").append(coordinates.getX()).append("</x>\n");
        sb.append("      <y>").append(coordinates.getY()).append("</y>\n");
        sb.append("    </coordinates>\n");
        sb.append("    <creationDate>").append(creationDate).append("</creationDate>\n");
        sb.append("    <from>").append(from != null ? from.toXML() : "null").append("</from>\n");
        sb.append("    <to>").append(to != null ? to.toXML() : "null").append("</to>\n");
        sb.append("    <distance>").append(distance).append("</distance>\n");
        sb.append("  </route>");
        return sb.toString();
    }

    public T getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public Location getFrom() {
        return from;
    }
    public Location getTo() {
        return to;
    }
    public long getDistance() {
        return distance;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }
    public void setId(T id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return(name + "; " + coordinates + "; " + from + " -> " + to + "; " + distance + "; Дата создания: " + creationDate + "; ID маршрута: " + id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return id == route.id && Objects.equals(name, route.name) && coordinates == route.coordinates && creationDate == route.creationDate &&
                from == route.from && to == route.to && distance == route.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, from, to, distance);
    }
}
