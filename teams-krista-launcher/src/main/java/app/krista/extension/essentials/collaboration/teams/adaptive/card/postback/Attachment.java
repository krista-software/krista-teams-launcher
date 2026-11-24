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

@SuppressWarnings("unused")
public class Attachment {

    private final String contentType;

    private final String inputHint;

    private final Content content;

    private Attachment(Content content) {
        this.contentType = Constants.HERO_CARD_CONTENT_TYPE;
        this.inputHint = Constants.INPUT_HINT;
        this.content = content;
    }

    public static Attachment create(MessageDto messageDto) {
        return new Attachment(Content.create(messageDto));
    }

    public String getContentType() {
        return contentType;
    }

    public String getInputHint() {
        return inputHint;
    }

    public Content getContent() {
        return content;
    }
}
