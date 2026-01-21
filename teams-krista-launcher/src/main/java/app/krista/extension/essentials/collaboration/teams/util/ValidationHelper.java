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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class providing validation methods for the Teams Krista Launcher extension.
 * <p>
 * This class contains static helper methods for validating configuration parameters
 * such as URLs. It cannot be instantiated.
 * </p>
 */
public final class ValidationHelper {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws IllegalStateException always, as this class should not be instantiated
     */
    private ValidationHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates that the given string is a properly formatted URL.
     * <p>
     * This method attempts to parse the string as a URI and convert it to a URL.
     * If either operation fails, a {@link MalformedURLException} is thrown with
     * a user-friendly error message.
     * </p>
     *
     * @param urlString the URL string to validate
     * @throws MalformedURLException if the URL format is invalid or cannot be parsed
     */
    public static void validateURL(String urlString) throws MalformedURLException {
        try {
            URI uri = new URI(urlString);
            @SuppressWarnings("unused")
            URL ignored = uri.toURL();  // Ensures the URL is valid
        } catch (MalformedURLException | URISyntaxException malformedURLException) {
            throw new MalformedURLException(
                    "Invalid Global Service URL (" + urlString + "): Please provide a valid URL (e.g., https://teams.krista.app)."
            );
        }
    }
}
