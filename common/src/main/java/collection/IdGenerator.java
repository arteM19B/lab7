package collection;

/**
 * Утилитный класс для генерации уникальных идентификаторов маршрутов.
 * Реализует простой счётчик для автоматического присвоения id.
 *
 * @author artem_bahetkin
 * @version 1.0
 */
public class IdGenerator<T extends Number> {
    private long counter = 0;

    public T next() {
        return (T) Long.valueOf(++counter);
    }

    public void setCounter(long value) {
        counter = value;
    }


}
