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

import app.krista.extension.essentials.collaboration.teams.KeyValueSessionStore;
import app.krista.extension.essentials.collaboration.teams.api.TeamsResource;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsCommand;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsUserProfile;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.util.ServiceRegistry;
import app.krista.ksdk.accounts.Account;
import app.krista.ksdk.accounts.AccountProvider;
import app.krista.ksdk.authorization.RoleManager;
import app.krista.ksdk.context.RequestContext;
import app.krista.omni.sdk.spi.impl.services.AccountService;
import app.krista.omni.sdk.spi.impl.services.ConversationsSearch;
import app.krista.omni.sdk.spi.impl.store.AccountStore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("SameParameterValue")
public class TeamsResourceTest {

    @Mock
    private static KeyValueSessionStore keyValueSessionStore;
    @Mock
    private static AccountProvider accountProvider;
    @Mock
    private static RoleManager roleManager;
    @Mock
    private static Invoker invoker;
    @Mock
    private static RequestContext requestContext;
    @Mock
    private static ServiceRegistry serviceRegistry;
    @Mock
    private static AccountStore accountStore;
    @Mock
    private static AccountService accountService;
    @Mock
    private static ConversationsSearch conversationsSearch;

    private static TeamsResource teamsResource;

    @SuppressWarnings("resource")
    @BeforeClass
    public static void setup() throws URISyntaxException {
        MockedStatic<ServiceRegistry> mockedServiceRegistry = Mockito.mockStatic(ServiceRegistry.class);
        mockedServiceRegistry.when(ServiceRegistry::getInstance).thenReturn(serviceRegistry);

        MockedStatic<AccountStore> mockedAccountStore = Mockito.mockStatic(AccountStore.class);
        mockedAccountStore.when(() -> AccountStore.create(Mockito.any())).thenReturn(accountStore);

        MockedStatic<AccountService> mockedAccountService = Mockito.mockStatic(AccountService.class);
        mockedAccountService.when(() -> AccountService.create(Mockito.any(), Mockito.any())).thenReturn(accountService);

        MockedStatic<ConversationsSearch> mockedConversationsSearch= Mockito.mockStatic(ConversationsSearch.class);
        mockedConversationsSearch.when(() -> ConversationsSearch.create(Mockito.any())).thenReturn(conversationsSearch);

        // Mock invoker behavior
        invoker = mock(Invoker.class);
        RoutingInfo mockRoutingInfo = mock(RoutingInfo.class);
        when(mockRoutingInfo.getRoutingId()).thenReturn("xyz");
        when(mockRoutingInfo.getRoutingURL(anyString(), any())).thenReturn("https://xyz.com");
        when(invoker.getRoutingInfo()).thenReturn(mockRoutingInfo);

        accountProvider = mock(AccountProvider.class);
        Account mockAccount = mock(Account.class);
        when(accountProvider.lookupAccount("abc@xyz.com")).thenReturn(mockAccount);

        roleManager = mock(RoleManager.class);
        when(roleManager.isWorkspaceAdmin(mockAccount)).thenReturn(true);

        teamsResource = new TeamsResource(requestContext, keyValueSessionStore, invoker, accountProvider, roleManager);
    }

    @Test
    public void testRegisterTeamsBot() {
        Response response = teamsResource.registerTeamsBot(new TeamsCommand());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testIsWorkspaceAdmin() throws NoSuchFieldException, IllegalAccessException {
        TeamsUserProfile profile = new TeamsUserProfile();
        setPrivateField(profile, "emailId", "abc@xyz.com");

        Response response = teamsResource.isWorkspaceAdmin(profile);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("\"isWorkspaceAdmin\": true"));
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }
}
