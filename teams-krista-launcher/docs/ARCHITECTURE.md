# Teams Krista Launcher Extension - Architecture Document

**Version:** 1.0.2  
**Domain:** Collaboration  
**Ecosystem:** Essentials  
**Last Updated:** December 2024

---

## Table of Contents

1. [Overview](#1-overview)
2. [Architecture Layers](#2-architecture-layers)
3. [Component Diagrams](#3-component-diagrams)
4. [Data Flow](#4-data-flow)
5. [Performance Considerations](#5-performance-considerations)
6. [Error Scenarios](#6-error-scenarios)
7. [Limitations](#7-limitations)

---

## 1. Overview

The Teams Krista Launcher Extension provides seamless integration between Microsoft Teams and the Krista platform. It enables users to initiate Krista conversations directly from Microsoft Teams and continue them in the Krista Client.

### Key Capabilities

- Receive messages from Microsoft Teams via REST API
- Execute Krista conversations based on user messages
- Send adaptive cards with launch buttons back to Teams
- Manage session-based authentication for users
- Integrate with Teams Global Service for message routing

---

## 2. Architecture Layers

The extension follows a **5-layer architecture** pattern for clean separation of concerns:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  ╔═══════════════════════════════════════════════════════════════════════╗  │
│  ║                    LAYER 1: PRESENTATION LAYER                        ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       ║  │
│  ║  │ Microsoft Teams │  │ Adaptive Cards  │  │ Krista Client   │       ║  │
│  ║  │ (User sends     │  │ (Launch buttons │  │ (Conversation   │       ║  │
│  ║  │  messages)      │  │  displayed)     │  │  continues)     │       ║  │
│  ║  └────────┬────────┘  └────────▲────────┘  └────────▲────────┘       ║  │
│  ╚═══════════╪════════════════════╪════════════════════╪════════════════╝  │
│              │                    │                    │                    │
│              ▼                    │                    │                    │
│  ╔═══════════════════════════════════════════════════════════════════════╗  │
│  ║                    LAYER 2: API LAYER (REST)                          ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────────────────────────────────────────────────────┐ ║  │
│  ║  │                     TeamsResource.java                          │ ║  │
│  ║  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────────┐│ ║  │
│  ║  │  │POST /messages│ │POST /command │ │GET /launchKrista/{...}  ││ ║  │
│  ║  │  │              │ │              │ │                          ││ ║  │
│  ║  │  │Receive Teams │ │Bot register  │ │Redirect to Krista Client││ ║  │
│  ║  │  │messages      │ │(dummy)       │ │                          ││ ║  │
│  ║  │  └──────────────┘ └──────────────┘ └──────────────────────────┘│ ║  │
│  ║  │  ┌──────────────────────────────────────────────────────────┐  │ ║  │
│  ║  │  │POST /check-ws-admin - Check workspace admin status       │  │ ║  │
│  ║  │  └──────────────────────────────────────────────────────────┘  │ ║  │
│  ║  └─────────────────────────────────────────────────────────────────┘ ║  │
│  ╚═══════════════════════════════════════════════════════════════════════╝  │
│              │                                                              │
│              ▼                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════╗  │
│  ║                    LAYER 3: BUSINESS LOGIC LAYER                      ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       ║  │
│  ║  │ TeamsExtension  │  │ TeamsAppClient  │  │ MessageSenderImpl│      ║  │
│  ║  │                 │  │                 │  │                 │       ║  │
│  ║  │ • Lifecycle     │  │ • HTTP Client   │  │ • Implements    │       ║  │
│  ║  │   management    │  │ • Send messages │  │   message sender│       ║  │
│  ║  │ • Configuration │  │ • Adaptive cards│  │   interface     │       ║  │
│  ║  │   validation    │  │                 │  │                 │       ║  │
│  ║  └─────────────────┘  └─────────────────┘  └─────────────────┘       ║  │
│  ╚═══════════════════════════════════════════════════════════════════════╝  │
│              │                                                              │
│              ▼                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════╗  │
│  ║                    LAYER 4: SERVICE LAYER                             ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       ║  │
│  ║  │ TeamsAPS        │  │ TeamsRequest    │  │ KeyValueSession │       ║  │
│  ║  │ HelperService   │  │ Authenticator   │  │ Store           │       ║  │
│  ║  │                 │  │                 │  │                 │       ║  │
│  ║  │ • Publication   │  │ • Custom auth   │  │ • Session       │       ║  │
│  ║  │   management    │  │ • Account lookup│  │   persistence   │       ║  │
│  ║  │ • ALS server    │  │ • Session mgmt  │  │ • Bidirectional │       ║  │
│  ║  │   integration   │  │                 │  │   mapping       │       ║  │
│  ║  └─────────────────┘  └─────────────────┘  └─────────────────┘       ║  │
│  ╚═══════════════════════════════════════════════════════════════════════╝  │
│              │                                                              │
│              ▼                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════╗  │
│  ║                    LAYER 5: DATA & UTILITIES LAYER                    ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       ║  │
│  ║  │ Constants       │  │ ValidationHelper│  │ ExceptionMessages│      ║  │
│  ║  │                 │  │                 │  │                 │       ║  │
│  ║  │ • Endpoints     │  │ • URL validation│  │ • Error message │       ║  │
│  ║  │ • Config keys   │  │                 │  │   constants     │       ║  │
│  ║  │ • Message types │  │                 │  │                 │       ║  │
│  ║  └─────────────────┘  └─────────────────┘  └─────────────────┘       ║  │
│  ║                                                                       ║  │
│  ║  ┌─────────────────┐  ┌─────────────────────────────────────────────┐║  │
│  ║  │PerformanceLogging│ │ Model Classes: TeamsMessage, TeamsUserProfile│║  │
│  ║  │ (AspectJ AOP)   │  │ TeamsCommand, AdaptiveCard, AdaptiveCardWithLink│ ║
│  ║  └─────────────────┘  └─────────────────────────────────────────────┘║  │
│  ╚═══════════════════════════════════════════════════════════════════════╝  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Layer Descriptions

| Layer | Components | Responsibility |
|-------|------------|----------------|
| **Presentation** | Microsoft Teams, Adaptive Cards, Krista Client | User interaction and display |
| **API** | TeamsResource | REST endpoints, request/response handling |
| **Business Logic** | TeamsExtension, TeamsAppClient, MessageSenderImpl | Core processing, message handling |
| **Service** | TeamsAPSHelperService, TeamsRequestAuthenticator, KeyValueSessionStore | Authentication, session, APS integration |
| **Data/Utilities** | Constants, ValidationHelper, ExceptionMessages, Models | Configuration, validation, data models |

---

## 3. Component Diagrams

### 3.1 High-Level Component Interaction

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           EXTERNAL SYSTEMS                                   │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐          │
│  │ Microsoft Teams │    │ Teams Global    │    │ Krista Client   │          │
│  │                 │    │ Service         │    │                 │          │
│  └────────┬────────┘    └────────┬────────┘    └────────▲────────┘          │
└───────────┼──────────────────────┼──────────────────────┼───────────────────┘
            │                      │                      │
            │ User Message         │ Forward Message      │ Redirect
            ▼                      ▼                      │
┌──────────────────────────────────────────────────────────────────────────────┐
│                    TEAMS KRISTA LAUNCHER EXTENSION                           │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                         TeamsResource                                   │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │ │
│  │  │ /messages    │  │ /command     │  │ /launchKrista│  │/check-ws-  │ │ │
│  │  │              │  │              │  │              │  │admin       │ │ │
│  │  └──────┬───────┘  └──────────────┘  └──────┬───────┘  └────────────┘ │ │
│  └─────────┼────────────────────────────────────┼────────────────────────┘ │
│            │                                    │                          │
│            ▼                                    ▼                          │
│  ┌─────────────────────┐              ┌─────────────────────┐              │
│  │ TeamsRequestAuth    │◄────────────►│ KeyValueSessionStore│              │
│  │ (Authentication)    │              │ (Session Storage)   │              │
│  └─────────┬───────────┘              └─────────────────────┘              │
│            │                                                               │
│            ▼                                                               │
│  ┌─────────────────────┐              ┌─────────────────────┐              │
│  │ TeamsExtension      │◄────────────►│ TeamsAPSHelperService│             │
│  │ (Core Extension)    │              │ (Publication Mgmt)  │              │
│  └─────────┬───────────┘              └─────────────────────┘              │
│            │                                                               │
│            ▼                                                               │
│  ┌─────────────────────┐              ┌─────────────────────┐              │
│  │ TeamsAppClient      │◄────────────►│ MessageSenderImpl   │              │
│  │ (HTTP Client)       │              │ (Message Sender)    │              │
│  └─────────────────────┘              └─────────────────────┘              │
│                                                                            │
└──────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           KRISTA PLATFORM                                    │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐          │
│  │ ConversationExec│    │ AccountService  │    │ SessionManager  │          │
│  │                 │    │                 │    │                 │          │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘          │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Dependency Injection Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                    HK2 DEPENDENCY INJECTION                     │
│                                                                 │
│  @Service Classes (Auto-registered):                            │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ TeamsAPSHelperService                                       ││
│  │   └── @Inject: Invoker, PublicationProvider, RequestContext ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │ KeyValueSessionStore                                        ││
│  │   └── @Inject: KeyValueStore                                ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                 │
│  @Inject Constructor Injection:                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ TeamsExtension                                              ││
│  │   └── TeamsAPSHelperService, RuntimeContext, RequestContext,││
│  │       ConversationsSearch, SessionManager, KeyValueSession  ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │ TeamsResource                                               ││
│  │   └── RequestContext, KeyValueSessionStore, Invoker,        ││
│  │       AccountProvider, RoleManager                          ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Data Flow

### 4.1 Message Processing Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        MESSAGE PROCESSING FLOW                              │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌─────────────┐
     │ User sends  │
     │ message in  │
     │ Teams       │
     └──────┬──────┘
            │
            ▼
     ┌─────────────┐
     │ Teams Global│
     │ Service     │
     │ forwards    │
     └──────┬──────┘
            │
            ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │                    POST /messages                               │
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ 1. TeamsRequestAuthenticator.getAuthenticatedAccountId()    ││
     │  │    • Extract email from TeamsUserProfile                    ││
     │  │    • Lookup/create account via AccountService               ││
     │  └─────────────────────────────────────────────────────────────┘│
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ 2. Check message deduplication (processedMessages Set)      ││
     │  │    • Skip if message ID already processed                   ││
     │  └─────────────────────────────────────────────────────────────┘│
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ 3. Execute asynchronously (ExecutorService)                 ││
     │  │    • Preserve ThreadLocal context                           ││
     │  │    • Execute welcome conversation                           ││
     │  └─────────────────────────────────────────────────────────────┘│
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ 4. Send launcher message via TeamsAppClient                 ││
     │  │    • Create AdaptiveCardWithLink                            ││
     │  │    • POST to Global Service /api/send                       ││
     │  └─────────────────────────────────────────────────────────────┘│
     └─────────────────────────────────────────────────────────────────┘
            │
            ▼
     ┌─────────────┐
     │ User sees   │
     │ launch      │
     │ button      │
     └──────┬──────┘
            │
            ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │                GET /launchKrista/{...}                          │
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ 1. Extract accountId from path                              ││
     │  │ 2. Retrieve/create session via KeyValueSessionStore         ││
     │  │ 3. Encode session as X-Krista-Context                       ││
     │  │ 4. Redirect (303) to Krista Client URL                      ││
     │  └─────────────────────────────────────────────────────────────┘│
     └─────────────────────────────────────────────────────────────────┘
            │
            ▼
     ┌─────────────┐
     │ User        │
     │ continues   │
     │ in Krista   │
     │ Client      │
     └─────────────┘
```

### 4.2 Session Management Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SESSION MANAGEMENT FLOW                              │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌─────────────────────────────────────────────────────────────────┐
     │                    User clicks launch button                    │
     └──────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │              Extract accountId from URL path                    │
     └──────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │         KeyValueSessionStore.getSessionId(accountId)            │
     └──────────────────────────────┬──────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
     ┌──────────────────────────┐    ┌──────────────────────────┐
     │   Session EXISTS         │    │   Session NOT FOUND      │
     └────────────┬─────────────┘    └────────────┬─────────────┘
                  │                               │
                  ▼                               ▼
     ┌──────────────────────────┐    ┌──────────────────────────┐
     │ SessionManager.validate()│    │ SessionManager.create()  │
     └────────────┬─────────────┘    └────────────┬─────────────┘
                  │                               │
          ┌───────┴───────┐                       │
          │               │                       │
          ▼               ▼                       ▼
     ┌─────────┐    ┌─────────┐    ┌──────────────────────────┐
     │ VALID   │    │ INVALID │    │ Store in KeyValueStore   │
     └────┬────┘    └────┬────┘    │ (bidirectional mapping)  │
          │              │         └────────────┬─────────────┘
          │              │                      │
          │              ▼                      │
          │    ┌──────────────────────────┐     │
          │    │ Create new session       │     │
          │    │ Store in KeyValueStore   │     │
          │    └────────────┬─────────────┘     │
          │                 │                   │
          └────────┬────────┴───────────────────┘
                   │
                   ▼
     ┌──────────────────────────────────────────────────────────────────┐
     │              Encode session as JSON (X-Krista-Context)           │
     │              Redirect to Krista Client with session              │
     └──────────────────────────────────────────────────────────────────┘
```

---

## 5. Performance Considerations

### 5.1 Async Processing

| Aspect | Implementation | Benefit |
|--------|----------------|---------|
| **Thread Pool** | `Executors.newCachedThreadPool()` | Dynamic thread allocation based on load |
| **Non-blocking** | Async conversation execution | Immediate response to Teams |
| **Context Preservation** | `ThreadLocalProxy.setAll()` | Maintains request context in async threads |

### 5.2 Performance Monitoring

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ASPECTJ PERFORMANCE LOGGING                              │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ @Around("call(public * ...MediumMessageReceiver.receiveMessage(..))")   ││
│  │   → Logs: "Conversation execution took time in milliseconds: X"         ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ @Around("execution(public * ...MessageSenderImpl.sendMessages(..))")    ││
│  │   → Logs: "Sending message took time in milliseconds: X"                ││
│  └─────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.3 Caching Strategy

| Cache | Storage | Purpose |
|-------|---------|---------|
| **Session IDs** | KeyValueStore | Avoid repeated session creation |
| **Processed Messages** | In-memory Set | Prevent duplicate message processing |

### 5.4 Performance Recommendations

1. **Monitor conversation execution time** - Long-running conversations impact user experience
2. **Ensure low-latency Global Service** - Network latency affects message delivery
3. **Scale thread pool** - Adjust for expected concurrent users
4. **Monitor memory** - Processed messages set grows with traffic

---

## 6. Error Scenarios

### 6.1 Error Classification

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ERROR SCENARIOS                                   │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ CONFIGURATION ERRORS (Validation Phase)                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: Invalid Global Service URL                                       ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: Malformed URL in configuration                                 ││
│  │ Message: "Invalid Global Service URL: The URL format is incorrect.      ││
│  │          Please enter a valid URL (e.g., https://teams.krista.app)."    ││
│  │ Resolution: Enter valid URL with protocol (https://)                    ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: Conversation Not Found                                           ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: Welcome Conversation name doesn't exist                        ││
│  │ Message: "Conversation \"{name}\" not found. Please check the name      ││
│  │          and try again."                                                ││
│  │ Resolution: Create conversation or correct the name                     ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: Duplicate Conversation                                           ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: Multiple conversations with same name in LIVE mode             ││
│  │ Message: "Multiple conversations found with the same name. Please       ││
│  │          ensure the Welcome Conversation name is unique within this     ││
│  │          execution mode."                                               ││
│  │ Resolution: Rename conversations to be unique                           ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ RUNTIME ERRORS (Message Processing Phase)                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: Message Delivery Failed                                          ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: Teams Global Service returns non-2xx response                  ││
│  │ Message: "Message delivery failed: The Teams Global Service could not   ││
│  │          process the request. Please verify the Global Service URL      ││
│  │          and user email address are correct."                           ││
│  │ Resolution: Verify Global Service URL and user email configuration      ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: Authentication Failure                                           ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: User email not found in Krista accounts                        ││
│  │ Message: MustAuthenticateException thrown                               ││
│  │ Resolution: Ensure user has Krista account with matching email          ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │ ERROR: APS Publication Error                                            ││
│  │ ─────────────────────────────────────────────────────────────────────── ││
│  │ Trigger: IOException or IllegalAccessException during APS operations    ││
│  │ Message: RuntimeException wrapping original cause                       ││
│  │ Resolution: Check ALS server connectivity and permissions               ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 Error Handling Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ERROR HANDLING FLOW                                  │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌─────────────────────────────────────────────────────────────────┐
     │                    Request Received                             │
     └──────────────────────────────┬──────────────────────────────────┘
                                    │
                                    ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │                    Validation Layer                             │
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ • URL format validation (ValidationHelper)                  ││
     │  │ • Conversation existence check                              ││
     │  │ • Configuration completeness                                ││
     │  └─────────────────────────────────────────────────────────────┘│
     └──────────────────────────────┬──────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
     ┌──────────────────────────┐    ┌──────────────────────────┐
     │   Validation PASSED      │    │   Validation FAILED      │
     └────────────┬─────────────┘    └────────────┬─────────────┘
                  │                               │
                  ▼                               ▼
     ┌──────────────────────────┐    ┌──────────────────────────┐
     │ Continue Processing      │    │ Return Error Response    │
     └────────────┬─────────────┘    │ with user-friendly       │
                  │                  │ message                  │
                  │                  └──────────────────────────┘
                  ▼
     ┌─────────────────────────────────────────────────────────────────┐
     │                    Business Logic Layer                         │
     │  ┌─────────────────────────────────────────────────────────────┐│
     │  │ try {                                                       ││
     │  │   // Execute conversation                                   ││
     │  │   // Send message                                           ││
     │  │ } catch (Exception e) {                                     ││
     │  │   // Log error                                              ││
     │  │   // Return appropriate HTTP status                         ││
     │  │ }                                                           ││
     │  └─────────────────────────────────────────────────────────────┘│
     └─────────────────────────────────────────────────────────────────┘
```

---

## 7. Limitations

### 7.1 Execution Mode Limitations

| Limitation | Description | Impact |
|------------|-------------|--------|
| **LIVE Mode Only** | Extension only supports LIVE execution mode | Cannot test in Draft/Test modes |
| **No Offline Support** | Requires active connection to Teams Global Service | Messages fail if service unavailable |

### 7.2 Scalability Limitations

| Limitation | Description | Mitigation |
|------------|-------------|------------|
| **In-memory Deduplication** | Processed messages stored in memory | May grow unbounded with high traffic |
| **Single Thread Pool** | Shared ExecutorService for all requests | May need tuning for high concurrency |
| **Session Storage** | KeyValueStore dependency | Performance depends on store implementation |

### 7.3 Functional Limitations

| Limitation | Description |
|------------|-------------|
| **Single Welcome Conversation** | Only one conversation can be triggered per invoker |
| **No Rich Media Support** | Limited to text and adaptive cards |
| **No Conversation History** | Each message starts fresh conversation |
| **Workspace Admin Required** | Installation requires admin rights |

### 7.4 Integration Limitations

| Limitation | Description |
|------------|-------------|
| **Teams Global Service Dependency** | All messages routed through external service |
| **No Direct Teams API** | Cannot interact with Teams API directly |
| **Single Tenant** | One invoker per Teams tenant |

---

## Appendix: Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Language** | Java | 21 |
| **Build** | Gradle | 8.14 |
| **DI Framework** | HK2 | - |
| **REST** | JAX-RS | - |
| **JSON** | Gson | - |
| **AOP** | AspectJ | 1.9.21 |
| **Krista APIs** | krista-apis | 1.0.112-sp1-rc1 |

---

*Document generated for Teams Krista Launcher Extension v1.0.2*

