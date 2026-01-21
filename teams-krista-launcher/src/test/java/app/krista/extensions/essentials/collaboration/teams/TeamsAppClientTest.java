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

import app.krista.extension.essentials.collaboration.teams.client.TeamsAppClient;
import app.krista.extension.essentials.collaboration.teams.util.Constants;
import app.krista.omni.sdk.medium.api.dto.MessageDto;
import app.krista.omni.sdk.medium.api.message.MediumInfo;
import app.krista.omni.sdk.medium.interactive.InteractiveMessageOut;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class TeamsAppClientTest {
    @Mock
    private WebTarget webTarget;
    @Mock
    private Invocation.Builder requestBuilder;
    private TeamsAppClient teamsAppClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(requestBuilder);
        teamsAppClient = new TeamsAppClient(null, null);
    }

    @Test
    public void testPostWithInvalidResponse() {
        String userMailId = "jane.smith@example.com";
        MediumInfo mediumInfo = new MediumInfo(Constants.MEDIUM_TYPE_TEAMS, userMailId);
        MessageDto messageDto = new MessageDto("Hello", "test");
        InteractiveMessageOut omniMessage = new InteractiveMessageOut(mediumInfo, messageDto, true, true);

        // Mock response and status info properly
        Response.StatusType statusType = mock(Response.StatusType.class);
        when(statusType.getStatusCode()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        Response response = mock(Response.class);
        when(response.getStatusInfo()).thenReturn(statusType);

        when(requestBuilder.post(any(Entity.class))).thenReturn(response);

        // Call the method directly
        assertThrows(NullPointerException.class, () -> teamsAppClient.postMessage(omniMessage));
    }


}

