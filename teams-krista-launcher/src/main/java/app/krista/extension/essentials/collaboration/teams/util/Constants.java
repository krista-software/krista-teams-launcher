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

package app.krista.extension.essentials.collaboration.teams.util;

import app.krista.model.field.util.GsonJsonMapper;

/**
 * This utility class provides constant values that are commonly used throughout the application.
 * It is designed to centralize the management of such constants for consistency and usability.
 */
public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final GsonJsonMapper GSON_JSON_MAPPER = GsonJsonMapper.create();
    public static final String JAX_RS_ID = "teams";
    public static final String DOCUMENTATION = "Documentation";
    public static final String DOCS_PATH = "static/docs";
    public static final String ENDPOINT_MESSAGE = "/messages";
    public static final String LAUNCH_KRISTA = "/launchKrista/{workspaceId}/{topicId}/{conversationExecutionId}/{accountId}";
    public static final String ENDPOINT_COMMAND = "/command";
    public static final String GLOBAL_SERVICE_API_SEND = "/api/send";
    public static final String GLOBAL_SERVICE_INVOKER_REMOVED = "/api/invoker-removed";
    public static final String CHECK_WS_ADMIN = "/check-ws-admin";

    public static final String PUBLICATION_ID = "omni:teams";

    public static final String GLOBAL_SERVICE_URL = "Global Service URL";
    public static final String LAUNCHER_BUTTON_TEXT = "Launcher button text";
    public static final String LAUNCHER_BUTTON_DESCRIPTION = "Launcher button description";
    public static final String TEXT = "text";
    public static final String EMAIL = "email";
    public static final String MEDIUM_TYPE_TEAMS = "TEAMS";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String MESSAGE_TYPE = "message";
    public static final String IS_DEFAULT_MESSAGE = "isDefaultMessage";
    public static final String ATTACHMENT_LAYOUT = "list";
    public static final String HERO_CARD_CONTENT_TYPE = "application/vnd.microsoft.card.hero";
    public static final String INPUT_HINT = "acceptingInput";
    public static final String BUTTON_TYPE = "postBack";
    public static final String BUTTON_OPEN_URL_TYPE = "openUrl";
    public static final String IS_CONVERSATION_ENDED = "isConversationEnded";
}
