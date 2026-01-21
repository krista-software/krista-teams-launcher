# Authentication

## Overview

The Teams Krista Launcher extension uses a session-based authentication mechanism to securely identify users and manage their interactions between Microsoft Teams and the Krista platform.

## Authentication Modes

### Teams User Authentication

The extension authenticates users based on their Microsoft Teams email address. When a user sends a message from Teams:

1. The extension extracts the user's email from the Teams message payload
2. The email is used to look up or create a Krista account
3. A session is created and associated with the user's account

### Workspace Admin Authentication

For administrative operations (register/unregister commands), the extension verifies that the user is a Workspace Admin:

1. The user's email is extracted from the Teams message
2. The account is looked up in the Krista platform
3. The `RoleManager` verifies workspace admin privileges

## Authentication Flow

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Microsoft      │     │  Teams Krista   │     │  Krista         │
│  Teams          │     │  Launcher       │     │  Platform       │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │  1. User sends        │                       │
         │     message           │                       │
         │──────────────────────▶│                       │
         │                       │                       │
         │                       │  2. Extract email     │
         │                       │     from payload      │
         │                       │                       │
         │                       │  3. Lookup/create     │
         │                       │     account           │
         │                       │──────────────────────▶│
         │                       │                       │
         │                       │  4. Account ID        │
         │                       │◀──────────────────────│
         │                       │                       │
         │                       │  5. Create session    │
         │                       │                       │
         │  6. Authenticated     │                       │
         │     response          │                       │
         │◀──────────────────────│                       │
         │                       │                       │
```

## Session Management

### Session Creation

When a user interacts with the extension for the first time:

1. A new session is created using the `SessionManager`
2. The session ID is stored in the `KeyValueSessionStore`
3. The session is associated with the user's account ID

### Session Lookup

For subsequent interactions:

1. The extension looks up the existing session ID
2. If the session is valid, it is reused
3. If the session is invalid or expired, a new session is created

### Session Storage

Sessions are stored using the `KeyValueSessionStore` service, which provides:

- Persistent session storage
- Account-to-session mapping
- Session retrieval by account ID

## Krista Client Launch Authentication

When a user clicks the launch button in Teams:

1. The extension retrieves the session ID for the user's account
2. The session ID is encoded and included in the redirect URL
3. The Krista Client uses this session to authenticate the user

### Redirect URL Structure

```
https://<krista-client-domain>/workspace/<workspaceId>/<topicId>/<executionId>
  ?source=teams
  &X-Krista-Context=<encoded-session>
```

## Security Considerations

### 1. Email-Based Identity

The extension trusts the email address provided by Microsoft Teams. Ensure your Teams environment is properly secured.

### 2. Session Security

- Sessions are created per-user and stored securely
- Session IDs are URL-encoded when passed to the Krista Client
- Invalid sessions trigger re-authentication

### 3. Workspace Admin Verification

Administrative operations require workspace admin privileges, verified through the Krista platform's role management system.

## Troubleshooting Authentication

### User Not Authenticated

**Symptom**: User receives an error when sending messages

**Possible Causes**:
- Email address not found in Teams payload
- Account creation failed
- Session creation failed

**Solution**: Verify the user has a valid Microsoft Teams account with an email address.

### Session Expired

**Symptom**: Launch button redirects to login page

**Possible Causes**:
- Session has expired
- Session was invalidated
- Account was deleted

**Solution**: Send a new message from Teams to create a fresh session.

### Workspace Admin Check Failed

**Symptom**: Register/unregister commands fail

**Possible Causes**:
- User is not a workspace admin
- Account not found in Krista

**Solution**: Verify the user has workspace admin privileges in the Krista platform.

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration guide
- [Teams App Setup](pages/TeamsAppSetup.md) - Microsoft Teams app installation

