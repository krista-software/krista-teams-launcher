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

package app.krista.extension.essentials.collaboration.teams.adaptive.card.postback;

import app.krista.extension.essentials.collaboration.teams.util.Constants;
import app.krista.omni.sdk.medium.api.dto.MessageDto;

import java.util.List;

/**
 * This class is a message template class for the hero card to send messages with buttons wherever required.
 */
@SuppressWarnings("unused")
public class AdaptiveCard {

    private final String type;

    private final String attachmentLayout;

    private final List<Attachment> attachments;

    private AdaptiveCard(List<Attachment> attachments) {
        this.type = Constants.MESSAGE_TYPE;
        this.attachmentLayout = Constants.ATTACHMENT_LAYOUT;
        this.attachments = attachments;
    }

    /**
     * This method is used to publish all message template content to send interactive message to the client.
     *
     * @param messageDto object is used to populate interactive message template data.
     *
     * @return {@link AdaptiveCard} message template with buttons.
     */
    public static AdaptiveCard create(MessageDto messageDto) {
        return new AdaptiveCard(List.of(Attachment.create(messageDto)));
    }

    public String getType() {
        return type;
    }

    public String getAttachmentLayout() {
        return attachmentLayout;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
