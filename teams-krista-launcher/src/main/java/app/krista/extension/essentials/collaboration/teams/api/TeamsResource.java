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

import app.krista.extension.essentials.collaboration.teams.KeyValueSessionStore;
import app.krista.extension.essentials.collaboration.teams.client.TeamsAppClient;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsCommand;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsMessage;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsUserProfile;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extension.util.ServiceRegistry;
import app.krista.ksdk.accounts.Account;
import app.krista.ksdk.accounts.AccountProvider;
import app.krista.ksdk.authorization.RoleManager;
import app.krista.ksdk.context.RequestContext;
import app.krista.ksdk.message.AskMessage;
import app.krista.ksdk.message.Message;
import app.krista.ksdk.message.MessageAnswer;
import app.krista.ksdk.workspace.Conversation;
import app.krista.ksdk.workspace.ConversationExecution;
import app.krista.model.field.DisplayNamedValuedField;
import app.krista.omni.sdk.medium.text.util.CommonInvokerParameters;
import app.krista.omni.sdk.spi.impl.services.AccountService;
import app.krista.omni.sdk.spi.impl.services.ConversationsSearch;
import app.krista.omni.sdk.spi.impl.store.AccountStore;
import com.google.gson.JsonObject;
import com.kristasoft.common.holders.ThreadLocalProxy;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.*;

/**
 * This resource class handles incoming Teams messages and processes them accordingly.
 * It listens for incoming messages, extracts the message content, and invokes the necessary actions.
 */

@Path("/")
public class TeamsResource {

    private final ExecutorService executorService;
    private final Set<String> processedMessages = new HashSet<>();
    private final AccountProvider accountProvider;
    private final RoleManager roleManager;
    private final AccountService accountService;
    private final Invoker invoker;
    private final RequestContext requestContext;
    private final ConversationsSearch conversationsSearch;
    private final TeamsAppClient teamsAppClient;
    private final KeyValueSessionStore keyValueSessionStore;
    private static final String KRISTA_FIELDS_TEXT = "com.krista.fields.Text";
    private static String KRISTA_CLIENT_DOMAIN;
    private static String KRISTA_CLIENT_BASE_URL;
    private static String EXTENSION_BASE_URL;

    /**
     * Constructs a TeamsResource instance.
     */
    @Inject
    public TeamsResource(RequestContext requestContext, KeyValueSessionStore keyValueSessionStore,
                         Invoker invoker, AccountProvider accountProvider, RoleManager roleManager) throws URISyntaxException {
        this.executorService = Executors.newCachedThreadPool();
        this.accountProvider = accountProvider;
        this.roleManager = roleManager;
        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();
        this.accountService = AccountService.create(serviceRegistry, AccountStore.create(serviceRegistry));
        this.invoker = invoker;
        this.requestContext = requestContext;
        this.conversationsSearch = ConversationsSearch.create(serviceRegistry);
        this.teamsAppClient = new TeamsAppClient(requestContext, invoker.getRoutingInfo());
        this.keyValueSessionStore = keyValueSessionStore;
        findUrls();
    }

    /**
     * Initializes the extension and Krista client URLs from the invoker's routing information.
     * <p>
     * This method extracts the appliance URL for the extension and the client URL
     * for redirecting users to the Krista Client.
     * </p>
     *
     * @throws URISyntaxException if the routing URL cannot be parsed as a valid URI
     */
    private void findUrls() throws URISyntaxException {
        EXTENSION_BASE_URL = invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE);
        System.out.println("EXTENSION_BASE_URL : " + EXTENSION_BASE_URL);
        URI httpURI = new URI(invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.CLIENT));
        KRISTA_CLIENT_DOMAIN = httpURI.getHost();
        System.out.println("KRISTA_CLIENT_DOMAIN : " + KRISTA_CLIENT_DOMAIN);
        KRISTA_CLIENT_BASE_URL = httpURI.getScheme() + "://" + KRISTA_CLIENT_DOMAIN;
        System.out.println("KRISTA_CLIENT_BASE_URL : " + KRISTA_CLIENT_BASE_URL);
    }

    /**
     * Handles the Teams bot registration command.
     * <p>
     * This endpoint receives registration commands from the Teams bot.
     * Currently returns a dummy response as the actual registration
     * is handled by the Teams Global Service.
     * </p>
     *
     * @param command the Teams command containing registration information
     * @return a successful HTTP 200 response
     */
    @POST
    @Path(ENDPOINT_COMMAND)
    public Response registerTeamsBot(TeamsCommand command) {
        // As of now this will have dummy response
        return Response.ok().build();
    }

    /**
     * Checks if the given Teams user profile corresponds to a workspace admin.
     *
     * @param teamsUserProfile The user profile of a Teams user.
     * @return A JSON response indicating whether the user is a workspace admin.
     */
    @POST
    @Path(CHECK_WS_ADMIN)
    public Response isWorkspaceAdmin(TeamsUserProfile teamsUserProfile) {
        Account account = accountProvider.lookupAccount(teamsUserProfile.getEmailId());
        boolean isWorkspaceAdmin = false;

        if (account != null) {
            isWorkspaceAdmin = roleManager.isWorkspaceAdmin(account);
        }

        String jsonResponse = "{\"isWorkspaceAdmin\": " + isWorkspaceAdmin + "}";
        return Response.status(Response.Status.OK.getStatusCode())
                .entity(jsonResponse)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .build();
    }

    /**
     * Handles incoming Teams messages by processing the message content.
     *
     * @param teamsUserMessage The incoming Teams message.
     * @return A response indicating the success of message processing.
     */
    @POST
    @Path(ENDPOINT_MESSAGE)
    public Response teamsMessage(Object teamsUserMessage) {

        long timestamp = System.currentTimeMillis();
        System.out.println("Preparing for adding Preferences");
        accountService.upsertPreference("onboard_completed", true);
        System.out.println("Added Preferences");
        String userMessage = GSON_JSON_MAPPER.toString(teamsUserMessage);
        System.out.println("User message : " + userMessage);
        TeamsMessage teamsMessage = GSON_JSON_MAPPER.fromString(userMessage, TeamsMessage.class);
        TeamsUserProfile teamsUserProfile = teamsMessage.getTeamsUserProfile();
        String messageId = teamsMessage.getId();

        try {
            if (!processedMessages.contains(messageId)) {
                processedMessages.add(messageId);
                Map<Class<?>, Object> allContexts = ThreadLocalProxy.getAll();

                this.executorService.submit(() -> {
                    ThreadLocalProxy.setAll(allContexts);
                    ConversationExecution conversationExecution = executeConversation(teamsMessage.getMessage(), timestamp);
                    sendLauncherMessage(teamsUserProfile.getEmailId(), conversationExecution);
                });
            }
        } catch (RuntimeException cause) {
            System.err.println(cause.getMessage() + cause);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok().build();
    }

    /**
     * Executes the welcome conversation with the user's message as input.
     * <p>
     * This method finds the configured welcome conversation, initializes it,
     * extracts the text input field, and submits the user's message as the first input.
     * </p>
     *
     * @param messageText the user's message text from Teams
     * @param timestamp   the timestamp when the message was received, used for performance logging
     * @return the conversation execution instance containing the execution context
     * @throws java.util.NoSuchElementException if the conversation or text field is not found
     */
    private ConversationExecution executeConversation(String messageText, long timestamp) {
        String conversationName = invoker.getAttributes().getOrDefault(CommonInvokerParameters.WELCOME_CONVERSATION, "").toString().trim();

        List<Conversation> conversations = conversationsSearch.getConversations(conversationName);
        Conversation foundConversation = conversations
                .stream()
                .filter(conversation -> Objects.equals(conversation.getMode().name(), requestContext.getExecutionMode().name()))
                .findFirst()
                .orElseThrow();

        Message message = foundConversation.init();
        Optional<DisplayNamedValuedField> textField = ((AskMessage) message).getFields().values().stream()
                .filter(entry -> KRISTA_FIELDS_TEXT.equals(entry.getFieldType()))
                .findFirst();
        String fieldName = textField.orElseThrow().getName();

        ConversationExecution conversationExecution = foundConversation.submit(
                MessageAnswer.create(UUID.randomUUID().toString(), Map.of(fieldName, messageText), MessageAnswer.SubmitType.SUBMIT, null));

        System.out.println("Conversation execution took time in milliseconds: " + (System.currentTimeMillis() - timestamp));

        return conversationExecution;
    }

    /**
     * Sends a launcher message to the user in Teams with a button to continue the conversation.
     * <p>
     * This method constructs the launch URL and sends an adaptive card to the user's
     * Teams chat with a button that redirects them to the Krista Client.
     * </p>
     *
     * @param email                 the user's email address for message delivery
     * @param conversationExecution the conversation execution containing context for the launch URL
     */
    private void sendLauncherMessage(String email, ConversationExecution conversationExecution) {
        String buttonText = invoker.getAttributes().getOrDefault(LAUNCHER_BUTTON_TEXT, "").toString().trim();
        String buttonDescription = invoker.getAttributes().getOrDefault(LAUNCHER_BUTTON_DESCRIPTION, "").toString().trim();
        long currentTimeInMilliseconds = System.currentTimeMillis();

        String accountId = accountService.lookupAccountId(email);
        String url = getExtensionUrl(EXTENSION_BASE_URL, requestContext.getWorkspaceId(),
                conversationExecution.getConversation().getTopic().getTopicId(),
                conversationExecution.getExecutionId(), accountId);

        teamsAppClient.postLauncherMessage(email, url, buttonText, buttonDescription);

        System.out.println("Sending message took time in milliseconds: " + (System.currentTimeMillis() - currentTimeInMilliseconds));
    }

    /**
     * Constructs the extension URL for launching the Krista Client.
     *
     * @param baseUrl                 the base URL of the extension
     * @param workspaceId             the workspace identifier
     * @param topicId                 the topic identifier
     * @param conversationExecutionId the conversation execution identifier
     * @param accountId               the user's account identifier
     * @return the fully constructed launch URL
     */
    private String getExtensionUrl(String baseUrl, String workspaceId, String topicId, String conversationExecutionId, String accountId) {
        return String.join("/", baseUrl, JAX_RS_ID, LAUNCH_KRISTA.split("/")[1], workspaceId, topicId, conversationExecutionId, accountId);
    }

    /**
     * Handles the launch request to redirect users to the Krista Client.
     * <p>
     * This endpoint is called when a user clicks the launch button in Teams.
     * It retrieves the user's session and redirects them to the Krista Client
     * with the appropriate context to continue their conversation.
     * </p>
     *
     * @param workspaceId             the workspace identifier
     * @param topicId                 the topic identifier
     * @param conversationExecutionId the conversation execution identifier
     * @param accountId               the user's account identifier
     * @return a redirect response to the Krista Client URL
     */
    @GET
    @Path(LAUNCH_KRISTA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response launchKrista(
            @PathParam("workspaceId") String workspaceId,
            @PathParam("topicId") String topicId,
            @PathParam("conversationExecutionId") String conversationExecutionId,
            @PathParam("accountId") String accountId) {
        String sessionId = keyValueSessionStore.getSessionId(accountId);

        System.out.println("Session Id : " + sessionId);
        String domain = KRISTA_CLIENT_DOMAIN;
        System.out.println("Domain Name : " + domain);

        String redirectURL = getRedirectUrl(workspaceId, topicId, conversationExecutionId, sessionId);
        System.out.println("redirectURL: " + redirectURL);

        return Response.seeOther(URI.create(redirectURL.replace("https://localhost/", "http://localhost:8100/")))
                .build();
    }

    /**
     * Constructs the redirect URL for the Krista Client.
     * <p>
     * The URL includes the workspace, topic, and conversation execution context,
     * along with the encoded session ID for authentication.
     * </p>
     *
     * @param workspaceId             the workspace identifier
     * @param topicId                 the topic identifier
     * @param conversationExecutionId the conversation execution identifier
     * @param sessionId               the user's session identifier
     * @return the fully constructed redirect URL
     */
    private String getRedirectUrl(String workspaceId, String topicId, String conversationExecutionId, String sessionId) {
        return UriBuilder.fromUri(KRISTA_CLIENT_BASE_URL)
                .path("workspace")
                .path(workspaceId)
                .path(topicId)
                .path(conversationExecutionId)
                .queryParam("source", "teams")
                .queryParam("X-Krista-Context", getEncodedClientSessionId(sessionId))
                .build()
                .toString();
    }

    /**
     * Encodes the session ID as a JSON object for inclusion in the redirect URL.
     * <p>
     * The session ID is wrapped in a JSON object with the key "clientSessionId"
     * and URL-encoded for safe transmission.
     * </p>
     *
     * @param sessionId the session identifier to encode
     * @return the URL-encoded JSON string containing the session ID
     */
    private String getEncodedClientSessionId(String sessionId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clientSessionId", sessionId);
        return URLEncoder.encode(jsonObject.toString(), StandardCharsets.UTF_8);
    }
}