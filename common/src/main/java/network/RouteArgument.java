package network;

import collection.Route;

public class RouteArgument implements CommandArgument {
    private final Route<Long> route;

    public RouteArgument(Route<Long> route) {
        this.route = route;
    }

    public Route<Long> getRoute() {
        return route;
    }
}
