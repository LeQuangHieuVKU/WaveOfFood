# WaveOfFood

A two-app Android food ordering system built with Kotlin and Firebase:
- **WavesOfFood**: customer-facing app
- **AdminWave Of Food**: admin/restaurant operations app

This README is written from a production-style source review perspective: not only what files exist, but what the system is trying to achieve, how data flows, and where architecture risks exist.

## 1) Product & Business Logic

WaveOfFood implements a classic marketplace loop for a single restaurant context:
1. Customer authenticates (email/password or Google)
2. Customer browses menu items
3. Customer adds items to cart
4. Customer places order
5. Admin accepts and dispatches order
6. Customer sees history and marks payment received

### Core business entities
- **Menu item** (`menu/*`)
- **User profile** (`user/{uid}`)
- **Cart** (`user/{uid}/CartItems/*`)
- **Placed order queue** (`OrderDetails/{orderId}`)
- **Completed orders** (`CompletedOrder/{orderId}`)
- **User buy history** (`user/{uid}/BuyHistory/{orderId}`)

## 2) Repository Structure

```text
WaveOfFood/
├── WavesOfFood/                 # Customer app
│   └── app/src/main/java/com/example/wavesoffood
└── AdminWave Of Food/           # Admin app
    └── app/src/main/java/com/example/adminwaveoffood
```

Both apps are separate Android projects (each has its own `settings.gradle.kts`, wrapper, and dependencies).

## 3) System Design (Inferred)

### Architecture style
- **Mobile client-driven architecture** with Firebase as Backend-as-a-Service
- No custom backend service layer
- UI largely Activity/Fragment + Adapter pattern with direct Firebase reads/writes

### Backend components in use
- Firebase Authentication
- Firebase Realtime Database
- Google Sign-In
- External image hosting through ImgBB (admin app)

### Design intention
The code suggests a rapid MVP approach:
- Optimize for speed of feature delivery
- Keep all business state in Firebase Realtime Database
- Share data contracts between customer/admin by duplicated model classes

## 4) Data Flow

### Customer flow
- `LoginActivity` / `SignActivity` authenticate users and write profile to `user/{uid}`
- `HomeFragment` + `SearchFragment` read `menu`
- `DetailsActivity` writes selected item to `user/{uid}/CartItems`
- `CartFragment` loads cart and sends checkout payload to `PayOutActivity`
- `PayOutActivity` creates order in `OrderDetails/{orderId}`, copies to `user/{uid}/BuyHistory/{orderId}`, then clears cart
- `HistoryFragment` reads `BuyHistory`; updates `CompletedOrder/{orderId}/paymentReceived`

### Admin flow
- `LoginActivity` / `SignUpActivity` authenticate admin and write profile to `admin/{uid}`
- `AddItemActivity` uploads image to ImgBB, then writes menu item to `menu/{itemId}`
- `AllItemActivity` reads/deletes `menu`
- `PendingOrderActivity` reads `OrderDetails`, accepts order (`orderAccepted=true`), dispatches by moving to `CompletedOrder` and removing from `OrderDetails`
- `MainActivity` computes dashboard metrics from `OrderDetails` and `CompletedOrder`

## 5) Scalability Review

### What can scale reasonably
- Firebase-managed auth and realtime sync handle early-stage load
- Read/write patterns are simple and easy to replicate

### Current bottlenecks
- Many screens load full nodes (`menu`, `OrderDetails`, `CompletedOrder`) with single-value listeners
- No pagination, no query limits, limited indexing strategy
- Aggregate metrics (e.g., total earnings) computed client-side by scanning all completed orders
- Data duplication (`OrderDetails`, `CompletedOrder`, `BuyHistory`) increases write amplification and consistency burden

## 6) Maintainability Review

### Strengths
- Simple Kotlin + ViewBinding codebase
- Business logic is straightforward to trace
- Clear separation between customer and admin apps at repository level

### Risks
- Tight coupling: UI classes directly contain data access and business rules
- No repository/use-case/domain abstraction
- Repeated/duplicated model definitions across apps
- Inconsistent naming/spelling and typo-prone identifiers
- Limited automated tests (only default sample tests)

## 7) Security Review

### High-risk findings from source
1. **Sensitive data in database**: user/admin model stores `password` field in Realtime Database.
2. **Hardcoded third-party API key**: ImgBB API key is embedded in `AddItemActivity`.
3. **Client-trust-heavy logic**: critical order state transitions are executed fully from mobile clients.
4. **Potential overexposure risk**: security depends heavily on Firebase rules (rules are not versioned in this repo).

### Recommended direction
- Never store plaintext passwords in database nodes
- Move image upload key handling to a secure backend/proxy or secret-managed service
- Restrict write paths with strict Firebase Security Rules + custom claims for admin-only operations
- Prefer server-side order state transitions for stronger integrity

## 8) Performance Review

### Positives
- Uses Glide for image loading/caching
- Basic image compression before upload in admin app

### Issues
- Repeated full-node fetches can increase bandwidth/latency
- Frequent adapter recreation on filtering/search
- Main-thread/UI operations mixed with network callbacks in large activities/fragments

## 9) Build & Run

### Prerequisites
- Android Studio (recent stable)
- JDK 11
- Firebase projects configured for both apps
- `google-services.json` present per app module (already in repo)

### Open projects
- Customer app project root: `WavesOfFood/`
- Admin app project root: `AdminWave Of Food/`

Each project builds independently.

## 10) Verified Local Validation Status

Attempted commands:
- `./gradlew lint test assembleDebug` in both app roots

Current status in this environment:
- Build fails during plugin resolution for `com.android.application` `8.8.1` (dependency/plugin resolution issue in sandbox environment)

## 11) Suggested Production Roadmap

1. Security hardening first (password handling, API key removal, strict Firebase rules)
2. Introduce data layer abstraction (Repository + use-case boundaries)
3. Add backend-controlled workflows for order transitions
4. Add pagination/index-aware queries for menu and orders
5. Add meaningful unit/integration tests for checkout and admin dispatch logic

---

If you want, I can next produce:
- a Firebase Realtime Database schema document,
- a threat model checklist,
- and a refactor plan toward clean architecture with minimal disruption.
