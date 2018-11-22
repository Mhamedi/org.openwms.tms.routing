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

import java.io.Serializable;
import java.util.Objects;

/**
 * A RouteConst.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
class RouteConst implements Route, Serializable {

    private final String routeId;

    RouteConst(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteId() {
        return this.routeId;
    }

    @Override
    public String toString() {
        return routeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || (getClass() != o.getClass() && !o.getClass().equals(Route.class)))
            return false;
        Route route = (Route) o;
        return Objects.equals(routeId, route.getRouteId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }
}
