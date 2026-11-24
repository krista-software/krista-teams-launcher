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

package app.krista.extension.essentials.collaboration.teams;

import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.essentials.collaboration.teams.aps.TeamsAPSHelperService;
import app.krista.extension.essentials.collaboration.teams.client.TeamsAppClient;
import app.krista.extension.essentials.collaboration.teams.client.MessageSenderImpl;
import app.krista.extension.essentials.collaboration.teams.util.Constants;
import app.krista.extension.essentials.collaboration.teams.util.ValidationHelper;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.*;
import app.krista.extension.util.ServiceRegistry;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.RequestContext;
import app.krista.ksdk.context.RuntimeContext;
import app.krista.ksdk.workspace.Conversation;
import app.krista.omni.sdk.medium.api.MediumService;
import app.krista.omni.sdk.medium.interactive.InteractiveMediumService;
import app.krista.omni.sdk.medium.interactive.message.InteractiveMediumMessageSender;
import app.krista.omni.sdk.medium.text.util.CommonInvokerParameters;
import app.krista.omni.sdk.spi.impl.services.ConversationsSearch;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.*;

/**
 * The TeamsExtension class represents an extension for interacting with Microsoft Teams.
 * It provides various methods for handling Teams-related functionality.
 */
@SuppressWarnings({"unused", "deprecation"})
@Java(version = Java.Version.JAVA_21)
@Field.Text(name = GLOBAL_SERVICE_URL)
@Field.Text(name = LAUNCHER_BUTTON_TEXT)
@Field.Text(name = LAUNCHER_BUTTON_DESCRIPTION)
@Field.Text(name = CommonInvokerParameters.GUEST_ROLE, required = false)
@Field(name = CommonInvokerParameters.WELCOME_CONVERSATION, type = "Text")
@Extension(implementingDomainIds = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        version = "1.0.2",
        jaxrsId = Constants.JAX_RS_ID,
        requireWorkspaceAdminRights = true,
        supportingModes = app.krista.extension.request.ExecutionMode.LIVE,
        name = "Teams Krista Launcher"
  )
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "e66100ba-c77b-478e-adee-cff7169ec1b6")
@StaticResource(path = "docs", file = "docs")
public class TeamsExtension {
    private final TeamsAPSHelperService teamsAPSHelperService;
    private final RuntimeContext runtimeContext;
    private final RequestContext requestContext;
    private final ConversationsSearch conversationsSearch;
    private final SessionManager sessionManager;
    private final KeyValueSessionStore keyValueSessionStore;

    /**
     * Constructs a TeamsExtension object with the required dependencies.
     *
     * @param teamsAPSHelperService The service for interacting with Teams App Service.
     * @param runtimeContext        The request runtime context.
     * @param requestContext        The request context.
     * @param invoker               The invoker instance.
     */
    @Inject
    public TeamsExtension(TeamsAPSHelperService teamsAPSHelperService, RuntimeContext runtimeContext,
                          RequestContext requestContext, Invoker invoker, SessionManager sessionManager, KeyValueSessionStore keyValueSessionStore) {
        this.teamsAPSHelperService = teamsAPSHelperService;
        this.runtimeContext = runtimeContext;
        this.requestContext = requestContext;
        this.sessionManager = sessionManager;
        this.keyValueSessionStore = keyValueSessionStore;
        MediumService mediumMessageService = InteractiveMediumService.getInstance();
        this.conversationsSearch = ConversationsSearch.create(ServiceRegistry.getInstance());

        TeamsAppClient teamsAppClient = new TeamsAppClient(this.requestContext, invoker.getRoutingInfo());
        InteractiveMediumMessageSender messageSender = new MessageSenderImpl(teamsAppClient);
        mediumMessageService.registerSender(messageSender);
        System.out.println("Registration completed for global service and message sender");
    }


    /**
     * Validates the attributes provided for the extension.
     *
     * @param attributes The attributes to validate, including the global service URL.
     */
    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> attributes) {
        String globalServiceURL = attributes.get(GLOBAL_SERVICE_URL).toString();
        try {
            String welcomeConversation = (String) attributes.get(CommonInvokerParameters.WELCOME_CONVERSATION);
            isConversationFound(welcomeConversation);
            ValidationHelper.validateURL(globalServiceURL);
        } catch (MalformedURLException cause) {
            throw new RuntimeException(cause.getMessage());
        }
    }

    /**
     * Provides custom tabs for the extension.
     *
     * @return A map containing custom tab information, such as documentation links.
     */
    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of(DOCUMENTATION, DOCS_PATH);
    }

    /**
     * Tests the connection to the extension.
     */
    @SuppressWarnings("EmptyMethod")
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
    }

    /**
     * Handles actions to be performed when the invoker is loaded.
     */
    @SuppressWarnings("EmptyMethod")
    @InvokerRequest(InvokerRequest.Type.INVOKER_LOADED)
    public void onInvokerLoad() {
    }

    /**
     * Handles actions to be performed when the invoker is installed.
     */
    @InvokerRequest(InvokerRequest.Type.INVOKER_INSTALLED)
    public void onInvokerInstall() {
        teamsAPSHelperService.addTeamsEntryToPublicationProvider();
    }

    /**
     * Handles actions to be performed when the invoker is removed.
     */
    @InvokerRequest(InvokerRequest.Type.INVOKER_REMOVED)
    public void onInvokerRemoved() {
        teamsAPSHelperService.removeTeamsEntryToPublicationProvider();
    }

    /**
     * Retrieves an authenticator for handling authentication requests.
     *
     * @return A request authenticator for use with the extension.
     */
    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getKristaAuthenticator() {
        return new TeamsRequestAuthenticator(runtimeContext, sessionManager, keyValueSessionStore);
    }

    /**
     * Validates that a conversation with the specified name exists and is unique within the current execution mode.
     * <p>
     * This method searches for conversations matching the given name and filters them by the current
     * execution mode. It ensures exactly one matching conversation exists.
     * </p>
     *
     * @param conversationName the name of the conversation to validate
     * @throws IllegalArgumentException if no conversation is found with the specified name,
     *                                  or if multiple conversations exist with the same name
     *                                  in the current execution mode
     */
    private void isConversationFound(String conversationName) {
        List<Conversation> conversations = conversationsSearch.getConversations(conversationName);
        List<Conversation> filteredConversations = conversations
                .stream()
                .filter(conversation -> Objects.equals(conversation.getMode().name(), requestContext.getExecutionMode().name()))
                .toList();
        if (filteredConversations.isEmpty()) {
            throw new IllegalArgumentException(String.format("Conversation \"%s\" not found. Please check the name and try again.", conversationName));
        }
        if (filteredConversations.size() > 1) {
            throw new IllegalArgumentException("Multiple conversations found with the same name. Please ensure the Welcome Conversation name is unique within this execution mode.");
        }
    }

}
