# Spring Security — Database Authentication Flow

---

## The Full Flow (left to right)

### 1. Authentication Filter
The entry point of every request. Two types exist in this system:

- **JwtAuthFilter** — intercepts requests that carry a JWT token, validates the token, and if valid sets the authentication directly in the `SecurityContext` without going through the Manager/Provider flow. This is the shortcut for already-authenticated requests so the DB is not hit on every call.
- **Default Auth Filter** — intercepts login requests, extracts username + password from the request body, wraps them in an `Authentication Object (Credentials)`, and passes it to the Authentication Manager.

---

### 2. Authentication Manager
Receives the credentials object and calls `authenticate()`. Does not know how to authenticate itself — delegates to one or more Authentication Providers.

---

### 3. Authentication Provider
Each provider has two methods:
- `supports()` — "can I handle this type of authentication?"
- `authenticate()` — actually performs the authentication

The manager tries each registered provider until one returns `supports() = true` and handles the request. Three common provider strategies:

| Provider | Used for |
|----------|---------|
| **DAO** | Database users (this project) |
| **LDAP** | Directory server authentication |
| **OAuth2** | Third-party login (Google, GitHub, etc.) |

This project uses the **DAO Authentication Provider**.

---

### 4. UserDetailsService → `loadUserByUsername()`
The DAO provider calls `loadUserByUsername()` on `CustomUserDetailsService`, which fetches the user from the database via `UserRepository`. Returns a `UserDetails` object to the provider.

---

### 5. UserDetails — `User` implements it
Spring Security compares the password in the returned `UserDetails` against what the user submitted. 

- **Match** → returns an `Authentication Object (Principal)` back to the filter, authentication is successful.
- **No match** → throws `BadCredentialsException`, authentication is denied.

---

## Full Request Lifecycle

```
Incoming Request
      │
      ▼
JwtAuthFilter
      │
      ├── Token present and valid?
      │         │
      │         YES → set auth in SecurityContext → skip to Controller
      │         │
      │         NO ↓
      │
Authentication Filter (login request)
      │
      ▼
Authentication Manager → authenticate()
      │
      ▼
DAO Authentication Provider
  supports() = true
  authenticate()
      │
      ▼
CustomUserDetailsService → loadUserByUsername()
      │
      ▼
UserRepository → DB lookup
      │
      ▼
UserDetails returned
      │
      ▼
Password comparison
      │
      ├── Match → Authentication Object (Principal) → JWT generated → returned to client
      │
      └── No match → BadCredentialsException → 401
```

---

## Why `CustomUserDetailsService`?

Spring Security has no idea where your users live. `UserDetailsService` is the interface it uses to ask "find me this user". By implementing it with `CustomUserDetailsService` you tell Spring to look in your database via `UserRepository`.

`loadUserByUsername` is the single method of that interface — Spring Security calls it internally during the DAO provider's `authenticate()`. You never call it yourself.

---

## JWT Role in the Flow

The JWT filter is a shortcut that sits before the full Manager/Provider chain. For requests after login:

- Client sends JWT token in the `Authorization` header
- `JwtAuthFilter` validates the token signature and expiry
- If valid, authentication is set directly in `SecurityContext` — no DB call, no password check
- The Manager/Provider/UserDetailsService chain is bypassed entirely

The full Manager/Provider/UserDetailsService chain only runs on the **login request** where credentials are submitted and the JWT is generated for the first time.
