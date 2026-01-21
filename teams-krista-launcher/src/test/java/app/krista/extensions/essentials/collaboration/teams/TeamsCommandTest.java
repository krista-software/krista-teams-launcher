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

package app.krista.extensions.essentials.collaboration.teams;

import app.krista.extension.essentials.collaboration.teams.teams.TeamsCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TeamsCommandTest {

    private final TeamsCommand teamsCommand = TeamsPojoGenerator.createTeamsCommand();


    @Test
    public void testGetCommand() {
        assertEquals("sampleCommand", teamsCommand.getCommand());
    }

    @Test
    public void testGetTenantId() {
        assertEquals("3694f6b4-b5f1-47ef-852f-a0b4a459ab44", teamsCommand.getTeamsUserProfile().getTenantId());
    }

    @Test
    public void testGetTeamsUserProfile() {
        assertNotNull(teamsCommand.getTeamsUserProfile());
        assertEquals("user456", teamsCommand.getTeamsUserProfile().getId());
        assertEquals("Jane Smith", teamsCommand.getTeamsUserProfile().getName());
        assertEquals("jane.smith@example.com", teamsCommand.getTeamsUserProfile().getEmailId());
        assertEquals("3694f6b4-b5f1-47ef-852f-a0b4a459ab44", teamsCommand.getTeamsUserProfile().getTenantId());
    }

    @Test
    public void testToString() {
        String expectedToString = "TeamsCommand{" +
                "command='sampleCommand', " +
                "teamsUserProfile=TeamsUserProfile{" +
                "id='user456', " +
                "name='Jane Smith', " +
                "emailId='jane.smith@example.com', " +
                "tenantId='3694f6b4-b5f1-47ef-852f-a0b4a459ab44'" +
                "}" +
                "}";
        assertEquals(expectedToString, teamsCommand.toString());
    }

}
