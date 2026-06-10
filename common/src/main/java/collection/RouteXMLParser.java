package collection;

import java.util.Scanner;

public final class RouteXMLParser {
    private RouteXMLParser() {
    }

    public static Route<Long> parse(Scanner scanner) {
        StringBuilder xml = new StringBuilder();
        boolean insideRoute = false;
        boolean routeClosed = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.startsWith("<route")) {
                insideRoute = true;
            }

            if (insideRoute) {
                xml.append(line).append('\n');
            }

            if (insideRoute && line.endsWith("</route>")) {
                routeClosed = true;
                break;
            }
        }

        if (!insideRoute) {
            throw new IllegalArgumentException("route XML block was not found");
        }
        if (!routeClosed) {
            throw new IllegalArgumentException("route XML block is not closed");
        }

        return parseXmlString(xml.toString());
    }

    private static Route<Long> parseXmlString(String xmlString) {
        String name = null;
        Coordinates coordinates = null;
        Location from = null;
        Location to = null;
        Long distance = null;

        try (Scanner scanner = new Scanner(xmlString)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.equals("<coordinates>")) {
                    coordinates = parseCoordinates(scanner);
                } else if (line.startsWith("<name>")) {
                    name = getTagValue(line);
                } else if (line.startsWith("<from>")) {
                    from = parseLocation(line);
                } else if (line.startsWith("<to>")) {
                    to = parseLocation(line);
                } else if (line.startsWith("<distance>")) {
                    distance = Long.parseLong(getTagValue(line));
                }
            }
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("route name is empty");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("route coordinates are missing");
        }
        if (distance == null) {
            throw new IllegalArgumentException("route distance is missing");
        }

        return new Route<>(name, coordinates, from, to, distance);
    }

    private static Coordinates parseCoordinates(Scanner scanner) {
        Long x = null;
        Integer y = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("</coordinates>")) {
                break;
            }
            if (line.startsWith("<x>")) {
                x = Long.parseLong(getTagValue(line));
            } else if (line.startsWith("<y>")) {
                y = Integer.parseInt(getTagValue(line));
            }
        }

        if (x == null || y == null) {
            throw new IllegalArgumentException("coordinates x or y are missing");
        }

        return new Coordinates(x, y);
    }

    private static Location parseLocation(String line) {
        String value = getTagValue(line);

        if (value.isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }

        int open = value.lastIndexOf('(');
        int close = value.lastIndexOf(')');
        if (open < 0 || close <= open) {
            throw new IllegalArgumentException("invalid location format: " + value);
        }

        String name = value.substring(0, open).trim();
        String[] coordinates = value.substring(open + 1, close).split(",");
        if (coordinates.length != 2) {
            throw new IllegalArgumentException("invalid location coordinates: " + value);
        }

        float x = Float.parseFloat(coordinates[0].trim());
        double y = Double.parseDouble(coordinates[1].trim());
        return new Location(x, y, name.isEmpty() ? null : name);
    }

    private static String getTagValue(String line) {
        return line.replaceAll("<[^>]+>", "").trim();
    }
}
