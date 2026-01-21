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
 * Represents a message sent or received in a Teams collaboration environment.
 * This class holds information about the message's ID, content, text format, type, channel ID, and user profile.
 */
@SuppressWarnings("unused")
public class TeamsMessage {
    private String id;
    private String message;
    private String textFormat;
    private String type;
    private String channelId;
    private TeamsUserProfile teamsUserProfile;

    /**
     * Retrieves the unique identifier of the message.
     *
     * @return The message's identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the content of the message.
     *
     * @return The message's content.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the text format of the message.
     *
     * @return The text format of the message.
     */
    public String getTextFormat() {
        return textFormat;
    }

    /**
     * Retrieves the type of the message.
     *
     * @return The message's type.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the channel ID associated with the message.
     *
     * @return The channel ID.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Retrieves the user profile associated with the message.
     *
     * @return The user's Teams user profile.
     */
    public TeamsUserProfile getTeamsUserProfile() {
        return teamsUserProfile;
    }

    /**
     * Generates a string representation of the TeamsMessage object.
     *
     * @return A formatted string containing the message's attributes, including ID, content, text format, type, channel ID, and user profile.
     */
    @Override
    public String toString() {
        return "TeamsMessage{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", textFormat='" + textFormat + '\'' +
                ", type='" + type + '\'' +
                ", channelId='" + channelId + '\'' +
                ", teamsUserProfile=" + teamsUserProfile +
                '}';
    }
}
