# Prerequisites

## Overview

Before installing and configuring the Teams Krista Launcher extension, ensure you meet all the requirements listed below.

## Account Requirements

| Requirement | Description |
|-------------|-------------|
| **Microsoft Teams Account** | Valid account with access to the Microsoft Teams application |
| **Krista Workspace Access** | Access to a Krista workspace where the extension will be installed |
| **Workspace Admin Role** | Required for register/unregister operations in Teams |

## Environment Requirements

| Requirement | Description |
|-------------|-------------|
| **APS Environment Variables** | Application Publication Service (APS) environment variables must be configured in your workspace environment |
| **Teams Global Service** | Access to the Teams Global Service for your environment |
| **Network Connectivity** | Network access between Teams, the extension, and the Krista platform |

## Conversation Requirements

### Welcome Conversation Setup

You must have a conversation configured to serve as the welcome conversation:

1. **First Step**: Must be an "Ask a Person" step
2. **Input Field**: Must have exactly one text input field
3. **Execution Mode**: Must be available in LIVE mode
4. **Unique Name**: The conversation name must be unique within the execution mode

### Role Configuration

The role assigned to users must have:

- ✅ Access to the welcome conversation
- ✅ Permission to execute the conversation
- ✅ Appropriate permissions for any subsequent conversation steps

## Teams App Requirements

| Requirement | Description |
|-------------|-------------|
| **Krista App Access** | The Krista app must be available in your organization's Teams app catalog |
| **App Installation Rights** | Users must have permission to install apps in Teams |
| **Admin Center Access** | For organization-wide deployment, access to Teams Admin Center is required |

## Technical Requirements

### Supported Execution Mode

| Mode | Supported |
|------|-----------|
| **Live** | ✅ Yes |
| **Test** | ❌ No |
| **Draft** | ❌ No |

> **⚠️ Important**: The Teams Krista Launcher extension is exclusively supported in Live mode.

### Browser Requirements

For the Krista Client launch functionality:

- Modern web browser (Chrome, Edge, Firefox, Safari)
- JavaScript enabled
- Cookies enabled

## Checklist

Before proceeding with installation, verify:

- [ ] Microsoft Teams account is active and accessible
- [ ] Krista workspace is accessible
- [ ] APS environment variables are configured
- [ ] Welcome conversation is created with proper configuration
- [ ] Role with conversation access is configured
- [ ] Krista app is available in Teams app catalog
- [ ] Network connectivity is verified

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration guide
- [Teams App Setup](pages/TeamsAppSetup.md) - Microsoft Teams app installation
- [Authentication](pages/Authentication.md) - Authentication and security information

