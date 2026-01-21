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

package app.krista.extension.essentials.collaboration.teams.client;

import app.krista.extension.essentials.collaboration.teams.adaptive.card.postback.AdaptiveCard;
import app.krista.extension.essentials.collaboration.teams.adaptive.card.open.link.AdaptiveCardWithLink;
import app.krista.extension.essentials.collaboration.teams.util.ExceptionMessages;
import app.krista.extension.essentials.collaboration.teams.util.ValidationHelper;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.util.InvokerAttributeProvider;
import app.krista.extension.util.ServiceRegistry;
import app.krista.ksdk.context.RequestContext;
import app.krista.model.field.util.JavaTypes;
import app.krista.omni.sdk.complex_fields.impl.util.Constants;
import app.krista.omni.sdk.medium.api.dto.Form;
import app.krista.omni.sdk.medium.api.dto.MessageDto;
import app.krista.omni.sdk.medium.interactive.InteractiveMessageOut;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.MalformedURLException;
import java.util.Objects;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.*;

/**
 * This class represents a client for interacting with a Teams application.
 * It provides methods for sending messages to external endpoints and handling responses.
 */
public class TeamsAppClient {

    private final Client client;
    private final RequestContext requestContext;
    private final RoutingInfo routingInfo;

    /**
     * Constructs a TeamsAppClient instance.
     *
     * @param requestContext Contains request context
     * @param routingInfo Contains routing info
     */
    public TeamsAppClient(RequestContext requestContext, RoutingInfo routingInfo) {
        this.client = ClientBuilder.newClient();
        this.requestContext = requestContext;
        this.routingInfo = routingInfo;
    }

    /**
     * Sends a InteractiveMessageOut to an external endpoint using a POST request.
     *
     * @param omniMessage The message to be sent.
     */
    public void postMessage(InteractiveMessageOut omniMessage) {
        InvokerAttributeProvider<String> globalServiceURL = getStringInvokerAttributeProvider();
        Invocation.Builder request = client.target(globalServiceURL.get()).path(GLOBAL_SERVICE_API_SEND).request(MediaType.APPLICATION_JSON);
        String outMessageBody = prepareMessageBody(omniMessage);
        System.out.println("Out message body : " + outMessageBody);
        Response response = request.post(Entity.entity(outMessageBody, MediaType.APPLICATION_JSON));
        handleResponse(response);
    }

    /**
     * Sends a launcher message with an adaptive card containing a launch button to the user.
     * <p>
     * This method constructs an adaptive card with a button that redirects the user
     * to the Krista Client when clicked.
     * </p>
     *
     * @param email      the recipient's email address
     * @param url        the URL to redirect to when the button is clicked
     * @param buttonText the text displayed on the launch button
     * @param bodyText   the description text displayed in the adaptive card
     * @throws IllegalArgumentException if the Teams Global Service returns an error response
     */
    public void postLauncherMessage(String email, String url, String buttonText, String bodyText) {
        InvokerAttributeProvider<String> globalServiceURL = getStringInvokerAttributeProvider();
        Invocation.Builder request = client.target(globalServiceURL.get()).path(GLOBAL_SERVICE_API_SEND).request(MediaType.APPLICATION_JSON);
        String outMessageBody = prepareLauncherMessageBody(email, url, buttonText, bodyText);
        System.out.println("Out Launcher message body : " + outMessageBody);
        Response response = request.post(Entity.entity(outMessageBody, MediaType.APPLICATION_JSON));
        handleResponse(response);
    }

    /**
     * Posts a message to notify the removal of an invoker to an external endpoint using a POST request.
     *
     * @param message The message to be posted.
     */
    public void postInvokerRemovedMessage(String message) {
        InvokerAttributeProvider<String> globalServiceURL = getStringInvokerAttributeProvider();
        Invocation.Builder request = client.target(globalServiceURL.get()).path(GLOBAL_SERVICE_INVOKER_REMOVED).request(MediaType.APPLICATION_JSON);
        Response response = request.post(Entity.entity(message, MediaType.APPLICATION_JSON));
        handleResponse(response);
    }

    /**
     * Prepares the JSON body for the outgoing message.
     *
     * @param message The message for which the JSON body is prepared.
     * @return The JSON body as a string.
     */
    private String prepareMessageBody(InteractiveMessageOut message) {
        JsonObject outMessage = new JsonObject();
        String targetAddress = message.getMediumInfo().getTargetAddress();
        outMessage.addProperty(EMAIL, targetAddress);
        outMessage.addProperty(IS_CONVERSATION_ENDED, message.isConversationEnded());
        outMessage.addProperty(MESSAGE_TYPE, message.getMessage().getType());
        outMessage.addProperty(IS_DEFAULT_MESSAGE, message.isDefaultMessage());

        String text = message.getText();
        boolean sendAdaptiveCard = checkIfInteractiveMessage(message.getMessage());
        if (text.contains(routingInfo.getRoutingId()) || !sendAdaptiveCard) {
            text = text.replace("\n", "<br> ");
            outMessage.addProperty(TEXT, text);
        } else {
            text = message.getMessage().getMessage();
            if (!Objects.isNull(text)) {
                text = text.replace("\n", "<br> ");
                message.getMessage().setMessage(text);
            }
            outMessage.add(TEXT, new Gson().toJsonTree(AdaptiveCard.create(message.getMessage())));
        }
        return outMessage.toString();
    }

    /**
     * Prepares the JSON body for a launcher message with an adaptive card.
     *
     * @param email      the recipient's email address
     * @param url        the URL for the launch button
     * @param buttonText the text displayed on the button
     * @param bodyText   the description text in the adaptive card
     * @return the JSON body as a string
     */
    private String prepareLauncherMessageBody(String email, String url, String buttonText, String bodyText) {
        JsonObject outMessage = new JsonObject();
        outMessage.addProperty(EMAIL, email);
        outMessage.addProperty(IS_CONVERSATION_ENDED, true);
        outMessage.addProperty(MESSAGE_TYPE, MEDIUM_TYPE_TEAMS);
        outMessage.addProperty(IS_DEFAULT_MESSAGE, true);
        outMessage.add(TEXT, new Gson().toJsonTree(AdaptiveCardWithLink.create(url, buttonText, bodyText)));

        return outMessage.toString();
    }

    /**
     * Handles the response from an external endpoint after a request.
     *
     * @param response The response from the external endpoint.
     * @throws IllegalArgumentException If the response indicates invalid input.
     */
    private void handleResponse(Response response) {
        String responseString = response.readEntity(String.class);
        System.out.println("response string from teams:" + responseString);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new IllegalArgumentException(ExceptionMessages.ONE_OR_MORE_INVALID_INPUT);
        }
    }

    /**
     * Retrieves and validates the Global Service URL from the invoker attributes.
     *
     * @return the invoker attribute provider containing the validated Global Service URL
     * @throws RuntimeException if the URL is malformed or invalid
     */
    private InvokerAttributeProvider<String> getStringInvokerAttributeProvider() {
        InvokerAttributeProvider<String> globalServiceURL = ServiceRegistry.getInstance().lookupService
                (JavaTypes.getTypedClass(InvokerAttributeProvider.class, String.class),
                        GLOBAL_SERVICE_URL, requestContext.getExecutionMode());
        try {
            ValidationHelper.validateURL(globalServiceURL.get());
        } catch (MalformedURLException cause) {
            throw new RuntimeException(cause.getMessage());
        }
        return globalServiceURL;
    }

    /**
     * Determines if a message should be rendered as an interactive adaptive card.
     * <p>
     * Interactive messages include NLP options, confirmation options, and conversation path options.
     * </p>
     *
     * @param messageDto the message to check
     * @return {@code true} if the message should be rendered as an interactive card, {@code false} otherwise
     */
    private boolean checkIfInteractiveMessage(MessageDto messageDto) {
        Form form = messageDto.getForm();
        if (!Objects.isNull(form) && !form.getPages().isEmpty() && !form.getPages().getFirst().getFields().isEmpty()) {
            String fieldName = form.getPages().getFirst().getFields().getFirst().getName();
            return fieldName.equals(Constants.NLP_OPTIONS) || fieldName.equals(Constants.CONFIRMATION_OPTIONS)
                    || fieldName.equals(Constants.CONVERSATION_PATH_OPTIONS);
        }
        return false;
    }

}
