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

package app.krista.extension.essentials.collaboration.teams.teams;

/**
 * Represents a command received from Teams, including information about the command, tenant, and user profile.
 */
@SuppressWarnings("unused")
public class TeamsCommand {
    String command;
    TeamsUserProfile teamsUserProfile;

    /**
     * Retrieves the command string associated with this Teams command.
     *
     * @return The command string.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Retrieves the user profile associated with this Teams command.
     *
     * @return The user's Teams user profile.
     */
    public TeamsUserProfile getTeamsUserProfile() {
        return teamsUserProfile;
    }

    /**
     * Generates a string representation of the TeamsCommand object.
     *
     * @return A formatted string containing the command, tenant ID, and user profile information.
     */
    @Override
    public String toString() {
        return "TeamsCommand{" +
                "command='" + command + '\'' +
                ", teamsUserProfile=" + teamsUserProfile +
                '}';
    }
}
