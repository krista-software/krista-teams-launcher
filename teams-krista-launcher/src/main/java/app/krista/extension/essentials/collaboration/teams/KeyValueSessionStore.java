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

import app.krista.extensions.util.KeyValueStore;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * Service for managing session ID storage and retrieval using a key-value store.
 * <p>
 * This service provides bidirectional mapping between account IDs and session IDs,
 * allowing lookup in either direction.
 * </p>
 */
@Service
public class KeyValueSessionStore {
    private final KeyValueStore keyValueStore;

    /**
     * Constructs a KeyValueSessionStore with the specified key-value store.
     *
     * @param keyValueStore the underlying key-value store for session persistence
     */
    @SuppressWarnings("unused")
    @Inject
    public KeyValueSessionStore(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    /**
     * Saves a session ID associated with an account ID.
     * <p>
     * This method creates a bidirectional mapping, allowing lookup by either
     * account ID or session ID.
     * </p>
     *
     * @param accountId the account identifier
     * @param sessionId the session identifier to associate with the account
     */
    public void saveSessionId(String accountId, String sessionId) {
        keyValueStore.put(getSessionKey(accountId), sessionId);
        keyValueStore.put(getSessionKey(sessionId), accountId);
    }

    /**
     * Generates a storage key for the given identifier.
     *
     * @param accountId the identifier (account ID or session ID)
     * @return the prefixed storage key
     */
    private String getSessionKey(String accountId) {
        return "session_" + accountId;
    }

    /**
     * Retrieves the session ID associated with the specified account ID.
     *
     * @param accountId the account identifier
     * @return the session ID, or {@code null} if no session exists for the account
     */
    public String getSessionId(String accountId) {
        return (String) keyValueStore.get(getSessionKey(accountId));
    }
}