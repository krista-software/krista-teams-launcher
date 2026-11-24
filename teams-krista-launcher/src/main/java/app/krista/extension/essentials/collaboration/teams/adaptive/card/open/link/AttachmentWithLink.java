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

package app.krista.extension.essentials.collaboration.teams.adaptive.card.open.link;

import app.krista.extension.essentials.collaboration.teams.util.Constants;

@SuppressWarnings("unused")
public class AttachmentWithLink {

    private final String contentType;

    private final String inputHint;

    private final ContentWithLink content;

    private AttachmentWithLink(ContentWithLink content) {
        this.contentType = Constants.HERO_CARD_CONTENT_TYPE;
        this.inputHint = Constants.INPUT_HINT;
        this.content = content;
    }

    public static AttachmentWithLink create(String url, String buttonText, String title) {
        return new AttachmentWithLink(ContentWithLink.create(url, buttonText, title));
    }

    public String getContentType() {
        return contentType;
    }

    public String getInputHint() {
        return inputHint;
    }

    public ContentWithLink getContent() {
        return content;
    }
}
