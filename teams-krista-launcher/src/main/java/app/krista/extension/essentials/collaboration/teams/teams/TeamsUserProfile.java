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
 * Represents a user profile associated with a Teams collaboration environment.
 * This class holds information about the user's ID, name, email, and tenant ID.
 */
@SuppressWarnings("unused")
public class TeamsUserProfile {

    private String id;
    private String name;
    private String emailId;
    private String tenantId;

    /**
     * Retrieves the unique identifier of the user.
     *
     * @return The user's identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the email ID of the user.
     *
     * @return The user's email ID.
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Retrieves the tenant ID associated with the user's Teams environment.
     *
     * @return The user's tenant ID.
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Generates a string representation of the TeamsUserProfile object.
     *
     * @return A formatted string containing the user's ID, name, email ID, and tenant ID.
     */
    @Override
    public String toString() {
        return "TeamsUserProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", emailId='" + emailId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
