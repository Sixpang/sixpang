package org.sixpang.hubservice.domain.repository;

import org.sixpang.routeservice.domain.model.Route;

public interface RouteRepository {
    Route save(Route route);
}
