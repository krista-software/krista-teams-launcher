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

/**
 * This utility class provides constant strings representing common exception messages that can be used throughout the application.
 * It is designed to centralize the management of exception messages for consistency and usability.
 */
public class ExceptionMessages {
    private ExceptionMessages() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ONE_OR_MORE_INVALID_INPUT = "Message delivery failed: The Teams Global Service could not process the request. Please verify the Global Service URL and user email address are correct.";

}
