# Teams Krista Launcher Extension

## Overview

The **Teams Krista Launcher Extension** enables seamless integration between Microsoft Teams and the Krista platform. Users can initiate conversations directly from Microsoft Teams by sending a message, which triggers a Krista conversation and provides a launch button to continue the interaction in the Krista Client.

## Key Features

- ✅ **Teams Integration**: Send messages from Microsoft Teams to trigger Krista conversations
- ✅ **Adaptive Cards**: Display interactive launch buttons in Teams
- ✅ **Seamless Handoff**: Continue conversations in the Krista Client with a single click
- ✅ **Workspace Admin Controls**: Register and unregister invokers via Teams commands
- ✅ **Session Management**: Automatic session handling for authenticated users

## Quick Start Guide

1. **Install the Extension**: Navigate to Catalog > Essentials > Collaboration > Teams Krista Launcher
2. **Configure Settings**: Enter the Global Service URL and configure the welcome conversation
3. **Set Up Teams App**: Add the Krista app to your Microsoft Teams organization
4. **Register Invoker**: Use the `register <Invoker ID>` command in Teams
5. **Start Using**: Send messages in Teams to launch Krista conversations

## Documentation Structure

### Getting Started
- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration guide
- [Authentication](pages/Authentication.md) - Authentication and security information
- [Teams App Setup](pages/TeamsAppSetup.md) - Microsoft Teams app installation

### Reference
- [Prerequisites](pages/Prerequisites.md) - Requirements before installation
- [Release Notes](pages/ReleaseNotes.md) - Version history and updates

## Support & Resources

| Resource | Details       |
|----------|---------------|
| **Extension Version** | 1.0.2         |
| **Krista Service APIs** | 1.0.112-sp1   |
| **Global Catalog Version** | GC.2025.12.2  |
| **Ecosystem** | Essentials    |
| **Domain** | Collaboration |
| **Execution Mode** | Live only     |

## How It Works

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Microsoft      │     │  Teams Krista   │     │  Krista         │
│  Teams          │────▶│  Launcher       │────▶│  Platform       │
│                 │     │  Extension      │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                       │                       │
        │  1. User sends        │  2. Extension         │
        │     message           │     triggers          │
        │                       │     conversation      │
        │                       │                       │
        │  4. User clicks       │  3. Returns           │
        │     launch button ◀───│     adaptive card     │
        │                       │                       │
        ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Krista Client                                │
│              (Conversation continues here)                      │
└─────────────────────────────────────────────────────────────────┘
```

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md)
- [Teams App Setup](pages/TeamsAppSetup.md)
