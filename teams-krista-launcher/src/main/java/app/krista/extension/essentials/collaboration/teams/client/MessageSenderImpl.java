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

import app.krista.omni.sdk.medium.interactive.InteractiveMessageOut;
import app.krista.omni.sdk.medium.interactive.message.InteractiveMediumMessageSender;

import java.util.List;

/**
 * Implementation of the {@link InteractiveMediumMessageSender} interface that facilitates sending
 * text-based and interactive medium messages using the Teams AppClient.
 */
public final class MessageSenderImpl implements InteractiveMediumMessageSender {

    private final TeamsAppClient teamsAppClient;

    /**
     * Constructs a new instance of TextMediumMessageSenderImpl.
     *
     * @param teamsAppClient The TeamsAppClient instance used to send messages.
     */
    public MessageSenderImpl(TeamsAppClient teamsAppClient) {
        this.teamsAppClient = teamsAppClient;
    }

    /**
     * Sends a list of text-based/interactive medium messages using the Teams AppClient.
     *
     * @param interactiveMessageOuts A list of interactiveMessageOut objects representing the text messages to be sent.
     */
    @Override
    public void sendMessages(List<InteractiveMessageOut> interactiveMessageOuts) {
        System.out.println("Available out message size: " + interactiveMessageOuts.size());
        for (InteractiveMessageOut interactiveMessage : interactiveMessageOuts) {
            System.out.println("Out message text: " + interactiveMessage.getText());
            teamsAppClient.postMessage(interactiveMessage);
        }
    }

}
