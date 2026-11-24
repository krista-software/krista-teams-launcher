/*
 * Teams Launcher Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extension.essentials.collaboration.teams.api;

import app.krista.extension.impl.jaxrs.Header;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * This class represents the main application configuration for the Teams integration service.
 * It extends {@link Application} and provides the configuration necessary for handling incoming requests.
 */
@Header(name = "Content-Security-Policy", value = "script-src 'self' 'unsafe-inline' 'unsafe-eval' *.googleapis.com " +
        "*.krista.app;connect-src wss://*.krista.app 'self' *.googleapis.com *.nr-data.net *.krista.app;" +
        "img-src 'self' blob: data: *.googleapis.com *.gstatic.com;frame-src *.krista.app;default-src 'self';" +
        "base-uri 'self';block-all-mixed-content;font-src 'self' https: data:;form-action 'self';" +
        "frame-ancestors 'self';object-src 'none';script-src-attr 'none';style-src 'self' https: 'unsafe-inline';" +
        "upgrade-insecure-requests")
@Service
@ApplicationPath("/")
@ContractsProvided(Application.class)
public class TeamsApplication extends Application {

    /**
     * Retrieves a set of resource classes that will be available for handling requests.
     *
     * @return A set of resource classes.
     */
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(TeamsResource.class);
    }

}
