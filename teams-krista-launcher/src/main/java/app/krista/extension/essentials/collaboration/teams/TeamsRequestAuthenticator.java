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

package app.krista.extension.essentials.collaboration.teams;

import app.krista.extension.authorization.MustAuthenticateException;
import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsMessage;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpRequest;
import app.krista.extension.util.ServiceRegistry;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.RuntimeContext;
import app.krista.model.field.NamedField;
import app.krista.omni.sdk.spi.impl.services.AccountService;
import app.krista.omni.sdk.spi.impl.store.AccountStore;
import com.kristasoft.common.io.Unicodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.*;

/**
 * This class implements the RequestAuthenticator interface to authenticate incoming requests from Teams.
 * It extracts the authenticated account ID from the request payload and uses it for authentication.
 */
@SuppressWarnings("deprecation")
public class TeamsRequestAuthenticator implements RequestAuthenticator {

    private final RuntimeContext runtimeContext;
    private final SessionManager sessionManager;
    private final KeyValueSessionStore keyValueSessionStore;
    private final AccountService accountService;

    /**
     * Constructs a TeamsRequestAuthenticator instance.
     *
     * @param sessionManager  The service for handling krista client session
     * @param runtimeContext  To get Krista account for the workspace
     */
    public TeamsRequestAuthenticator(RuntimeContext runtimeContext,
                                     SessionManager sessionManager,
                                     KeyValueSessionStore keyValueSessionStore) {
        this.runtimeContext = runtimeContext;
        this.sessionManager = sessionManager;
        this.keyValueSessionStore = keyValueSessionStore;
        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();
        this.accountService = AccountService.create(serviceRegistry, AccountStore.create(serviceRegistry));
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return Collections.emptySet();
    }

    /**
     * Retrieves the authenticated account ID from the Teams request payload.
     *
     * @param protoRequest The incoming request.
     * @return The authenticated account ID extracted from the request payload.
     */
    @Override
    public String getAuthenticatedAccountId(ProtoRequest protoRequest) {
        try {
            HttpRequest httpRequest = (HttpRequest) protoRequest;
            httpRequest.bufferBody();
            String uriString = httpRequest.getUri().toString();

            // As checking for workspace admin, No need to execute it for omnichannel
            if (uriString.contains(CHECK_WS_ADMIN)) {
                return runtimeContext.getKristaAccount().getAccountId();
            }

            if (uriString.contains(LAUNCH_KRISTA.split("/")[1])) {
                String[] uriSplit = uriString.split("/");
                String accountIdFromPath = uriSplit[uriSplit.length - 1];
                System.out.println("Account ID from Path : " + accountIdFromPath);
                String clientSessionId = keyValueSessionStore.getSessionId(accountIdFromPath);
                System.out.println("ClientSession ID from Authenticated Account : " + clientSessionId);
                if (Objects.isNull(clientSessionId)) {
                    System.out.println("Found Client Session ID as Null");
                    createClientSessionId(accountIdFromPath);
                } else {
                    String lookupAccountId = sessionManager.lookupAccountId(clientSessionId);
                    System.out.println("Lookup Account ID : " + lookupAccountId);
                    if (Objects.isNull(lookupAccountId)) {
                        System.out.println("Found Lookup Account Id Null");
                        createClientSessionId(accountIdFromPath);
                    }
                }
                return accountIdFromPath;
            }

            InputStream body = httpRequest.getBody();
            String requestPayload = Unicodes.fromUTF8(body);
            TeamsMessage teamsMessage = GSON_JSON_MAPPER.fromString(requestPayload, TeamsMessage.class);
            String fromAddress = teamsMessage.getTeamsUserProfile().getEmailId();

            if (fromAddress == null) {
                return null;
            }
            return accountService.getAccount(fromAddress, teamsMessage.getTeamsUserProfile().getName(), MEDIUM_TYPE_TEAMS).getAccountId();
        } catch (IOException | RuntimeException cause) {
            System.out.println("Exception occurred while getting authenticated account id: " + cause.getMessage());
            return null;
        }
    }

    /**
     * Creates a new client session for the specified account and stores it in the key-value store.
     * <p>
     * This method is called when no existing session is found for the account or when
     * the existing session is invalid.
     * </p>
     *
     * @param accountIdFromPath the account ID for which to create a session
     */
    private void createClientSessionId(String accountIdFromPath) {
        System.out.println("session not find key value store, creating it");
        String session = sessionManager.createSession(accountIdFromPath);
        keyValueSessionStore.saveSessionId(accountIdFromPath, session);
        System.out.println(session + " session created and saved");
    }

    @Override
    public boolean setServiceAuthorization(String s) {
        return false;
    }

    @Override
    public Map<String, NamedField> getAttributeFields() {
        return Collections.emptyMap();
    }

    @Override
    public ProtoResponse getMustAuthenticateResponse(MustAuthenticateException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthenticateResponse(MustAuthenticateException e) {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthorizeResponse(MustAuthorizeException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthorizeResponse(MustAuthorizeException e) {
        return null;
    }
}
