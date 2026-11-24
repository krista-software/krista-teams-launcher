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

package app.krista.extension.essentials.collaboration.teams.aps;

import app.krista.aps.Publication;
import app.krista.aps.PublicationProvider;
import app.krista.extension.essentials.collaboration.teams.client.TeamsAppClient;
import app.krista.extension.essentials.collaboration.teams.util.Constants;
import app.krista.extension.essentials.collaboration.teams.util.ValidationHelper;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.ksdk.context.RequestContext;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.GLOBAL_SERVICE_URL;

/**
 * This class provides utility methods to interact with the Teams Application Provider Service (APS) using the Krista platform.
 * It allows adding and removing invoker entries to/from the Publication Provider for Teams APS integration.
 */
@Service
public class TeamsAPSHelperService {

    private final Invoker invoker;
    private final PublicationProvider publicationProvider;
    private final RequestContext requestContext;


    /**
     * Constructs a new TeamsAPSHelperService instance.
     *
     * @param invoker             The Invoker instance for interacting with the Krista platform.
     * @param publicationProvider The PublicationProvider instance for managing publications.
     * @param requestContext      Contains request context
     */
    @SuppressWarnings("unused")
    @Inject
    public TeamsAPSHelperService(Invoker invoker, PublicationProvider publicationProvider, RequestContext requestContext) {
        this.invoker = invoker;
        this.publicationProvider = publicationProvider;
        this.requestContext = requestContext;
    }

    /**
     * Adds an entry for the current Invoker to the Publication Provider associated with Teams APS.
     * This entry includes the Invoker's routing information.
     */
    public void addTeamsEntryToPublicationProvider() {
        System.out.println("Adding invoker entry to ALS server");
        String routingURL = invoker
                .getRoutingInfo()
                .getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE);
        String invokerId = invoker.getInvokerId();

        try {
            Publication publication = this.publicationProvider.getPublication(Constants.PUBLICATION_ID);
            publication.addEntry(invokerId, routingURL);
            System.out.println("Invoker entry is added to ALS server");
        } catch (IOException | IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
    }

    /**
     * Removes the entry of the current Invoker from the Publication Provider associated with Teams APS.
     */
    public void removeTeamsEntryToPublicationProvider() {
        System.out.println("Removing invoker entry from ALS server");
        String invokerId = invoker.getInvokerId();
        try {
            Publication publication = this.publicationProvider.getPublication(Constants.PUBLICATION_ID);
            publication.removeEntries(invokerId);

            notifyInvokerRemovedToTeamsBot(invokerId);
        } catch (IOException | IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
    }

    /**
     * Notifies the Teams Bot about the removal of an invoker.
     *
     * @param invokerId The ID of the removed invoker.
     */
    private void notifyInvokerRemovedToTeamsBot(String invokerId) {
        String globalServiceURL = invoker.getAttributes().getOrDefault(GLOBAL_SERVICE_URL, "").toString().trim();
        if (globalServiceURL.isBlank()) {
            return;
        }

        try {
            ValidationHelper.validateURL(globalServiceURL);
        } catch (MalformedURLException cause) {
            String errorMessage = "{\"error\": \"" + cause.getMessage() + "\"}";
            postMessageToTeamsBot(errorMessage);
            return;
        }

        String message = "{\"invokerId\": \"" + invokerId + "\"}";

        postMessageToTeamsBot(message);
    }

    /**
     * Posts a message to the Teams Bot.
     *
     * @param message The message to be posted.
     */
    private void postMessageToTeamsBot(String message) {
        TeamsAppClient teamsAppClient = new TeamsAppClient(requestContext, invoker.getRoutingInfo());
        teamsAppClient.postInvokerRemovedMessage(message);
    }
}
