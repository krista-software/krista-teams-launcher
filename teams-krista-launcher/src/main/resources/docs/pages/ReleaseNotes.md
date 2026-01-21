# Release Notes

## Teams Krista Launcher Extension

### Version 1.0.2

**Release Date**: December 2024

#### Version Information

| Component | Version     |
|-----------|-------------|
| **Extension Version** | 1.0.2       |
| **Krista Service APIs Java** | 1.0.112-sp1 |
| **Global Catalog Version** | 1.0.55      |
| **Appliance Release Version** | 3.4.4-sp1   |

#### Location

| Property | Value |
|----------|-------|
| **Ecosystem** | Essentials |
| **Domain** | Collaboration |

#### Features

- ✅ Microsoft Teams integration for Krista conversations
- ✅ Adaptive card display with launch buttons
- ✅ Seamless handoff to Krista Client
- ✅ Workspace admin controls for invoker registration
- ✅ Session-based authentication

#### Known Limitations

1. **Execution Mode**: This extension is exclusively supported in Live mode. Test and Draft modes are not supported.

2. **First-Time Launch**: If a user was already logged into the Krista Client before using this feature, clicking the launch button for the first time may log them out to clear any conflicting cookies. Subsequent clicks will work as expected.

3. **Account Deletion**: When a user's account is deleted:
   - Sending a message afterward is treated like a first-time case
   - The previous cookie is no longer valid
   - All previously generated buttons become invalid

4. **Multiple Workspaces**: If a user is part of multiple workspaces, this feature may not function as expected.

---

## Upgrade Notes

### Upgrading to 1.0.0

This is the initial release. No upgrade steps are required.

### Compatibility

| Component | Minimum Version |
|-----------|-----------------|
| Krista Platform | 3.4.4-sp1 |
| Krista Service APIs | 1.0.112-sp1 |

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration guide
- [Prerequisites](pages/Prerequisites.md) - Requirements before installation
- [Error Reference](pages/ErrorReference.md) - Common errors and solutions

