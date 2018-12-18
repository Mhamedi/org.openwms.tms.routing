/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.tms.routing;

import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.openwms.common.comm.res.ResResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;

/**
 * A RouteServiceImpl.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService("routing")
class RouteServiceImpl implements RouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final ResResponder resResponder;
    private final RouteRepository routeRepository;
    private final RouteDetailsRepository routeDetailsRepository;
    private final InputContext in;

    RouteServiceImpl(@Autowired(required = false) ResResponder resResponder, RouteRepository routeRepository, RouteDetailsRepository routeDetailsRepository, InputContext in) {
        this.resResponder = resResponder;
        this.routeRepository = routeRepository;
        this.routeDetailsRepository = routeDetailsRepository;
        this.in = in;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToNextLocation() {
        if (resResponder == null) {
            LOGGER.warn("Please provide a bean instance of ResResponder. Default implementations are disabled. If property 'owms.osip.enabled' is set to false a custom ResResponder is expected");
            return;
        }
        Route route = in.get("route", Route.class).orElseThrow(() -> new NoRouteException("No Route information in current context, can't load the next Location from the RouteDetails"));
        String actualLocation = in.get("actualLocation", String.class).orElseThrow(() -> new NoRouteException("No information about the actual Location in the current context, can't load the next Location from the RouteDetails"));
        String asNext = routeDetailsRepository.findByRoute_RouteId_OrderByPos(route.getRouteId())
                .stream()
                .filter(r -> r.getSource().equals(actualLocation))
                .findFirst()
                .orElseThrow(() -> new NoRouteException(format("No entry in RouteDetails that matches the sourceLocation [%s]", actualLocation)))
                .getNext();
        LOGGER.debug("Sending to next Location [{}]", asNext);
        resResponder.sendToLocation(asNext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeRoute(String routeId) {
        Route route = routeRepository.findByRouteId(routeId)
                .orElseThrow(() -> new NotFoundException(format("Route with route id [%s] was not found", routeId)));
        in.put("route", route);

        String barcode = in.get("barcode", String.class).orElse("barcode was not set");
        LOGGER.debug("New route [{}] was set for barcode [{}]", routeId, barcode);
    }

}
