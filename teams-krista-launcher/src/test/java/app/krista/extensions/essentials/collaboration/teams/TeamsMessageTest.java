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

import app.krista.extension.essentials.collaboration.teams.teams.TeamsMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TeamsMessageTest {
    private final TeamsMessage teamsMessage = TeamsPojoGenerator.createTeamsMessage();

    @Test
    public void testGetMessage() {
        assertEquals("Hello world", teamsMessage.getMessage());
    }

    @Test
    public void getTextFormat() {
        assertEquals("plain", teamsMessage.getTextFormat());
    }

    @Test
    public void getType() {
        assertEquals("message", teamsMessage.getType());
    }

    @Test
    public void testGetId() {
        assertEquals("114149291599657", teamsMessage.getId());
    }

    @Test
    public void testGetChannelId() {
        assertEquals("msteams", teamsMessage.getChannelId());
    }

    @Test
    public void testUserName() {
        assertEquals("Jane Smith", teamsMessage.getTeamsUserProfile().getName());
    }

    @Test
    public void testUserEmail() {
        assertEquals("jane.smith@example.com", teamsMessage.getTeamsUserProfile().getEmailId());
    }

    @Test
    public void testUserTenantId() {
        assertEquals("3694f6b4-b5f1-47ef-852f-a0b4a459ab44", teamsMessage.getTeamsUserProfile().getTenantId());
    }
}
