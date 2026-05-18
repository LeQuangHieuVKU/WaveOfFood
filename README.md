# 🌊 WaveOfFood

> A dual-application Android food ordering system built with Kotlin and Firebase.

WaveOfFood là hệ thống đặt món ăn trên Android gồm hai ứng dụng:
- **WavesOfFood** — ứng dụng dành cho khách hàng
- **AdminWave Of Food** — ứng dụng quản trị nhà hàng

README này được viết theo hướng **production-style source review**, không chỉ mô tả source code mà còn phân tích:
- business logic
- data flow
- architecture
- scalability
- security
- maintainability
- production risks

---

# 📚 Table of Contents

- [1. Product Overview](#1-product-overview)
- [2. Core Features](#2-core-features)
- [3. Repository Structure](#3-repository-structure)
- [4. Technology Stack](#4-technology-stack)
- [5. System Architecture](#5-system-architecture)
- [6. Business Flow](#6-business-flow)
- [7. Firebase Database Design](#7-firebase-database-design)
- [8. Customer App Flow](#8-customer-app-flow)
- [9. Admin App Flow](#9-admin-app-flow)
- [10. Scalability Review](#10-scalability-review)
- [11. Maintainability Review](#11-maintainability-review)
- [12. Security Review](#12-security-review)
- [13. Performance Review](#13-performance-review)
- [14. Build & Run](#14-build--run)
- [15. Production Roadmap](#15-production-roadmap)
- [16. Overall Evaluation](#16-overall-evaluation)
- [17. Future Improvements](#17-future-improvements)

---

# 1. Product Overview

WaveOfFood triển khai mô hình đặt món ăn trực tuyến cho một nhà hàng theo quy trình:

```text
Customer Login
      ↓
Browse Menu
      ↓
Add To Cart
      ↓
Place Order
      ↓
Admin Accepts Order
      ↓
Order Completed
```

## 🎯 Business Goals

- Cho phép khách hàng đặt món nhanh chóng
- Quản lý menu và đơn hàng theo thời gian thực
- Đồng bộ dữ liệu realtime bằng Firebase
- Tối ưu tốc độ phát triển MVP

---

# 2. Core Features

## 👤 Customer Features

- Email/Password Authentication
- Google Sign-In
- Browse Food Menu
- Search Food
- Add To Cart
- Place Orders
- View Order History
- Update Payment Status

---

## 🛠️ Admin Features

- Admin Authentication
- Add/Edit/Delete Menu Items
- Upload Food Images
- Manage Pending Orders
- Accept Orders
- Dispatch Orders
- Revenue Dashboard

---

# 3. Repository Structure

```text
WaveOfFood/
├── WavesOfFood/                 # Customer Android App
│   └── app/src/main/java/com/example/wavesoffood
│
└── AdminWave Of Food/           # Admin Android App
    └── app/src/main/java/com/example/adminwaveoffood
```

## Repository Characteristics

| Component | Description |
|---|---|
| WavesOfFood | Customer-facing mobile application |
| AdminWave Of Food | Restaurant/admin management application |
| Firebase | Backend-as-a-Service |
| ImgBB | External image hosting service |

---

# 4. Technology Stack

## 📱 Mobile Development

| Technology | Purpose |
|---|---|
| Kotlin | Android development |
| XML | UI Layouts |
| ViewBinding | Safer UI binding |
| RecyclerView | Dynamic lists |
| Glide | Image loading & caching |

---

## ☁️ Backend Services

| Service | Usage |
|---|---|
| Firebase Authentication | User authentication |
| Firebase Realtime Database | Realtime data storage |
| Google Sign-In | OAuth login |
| ImgBB API | Image upload hosting |

---

# 5. System Architecture

## 🏗️ Architecture Style

The project follows:

```text
Mobile Client Architecture + Firebase BaaS
```

There is:
- ❌ No dedicated backend server
- ❌ No REST API layer
- ❌ No microservice architecture

Instead:
- Android clients directly communicate with Firebase.

---

## High-Level Architecture

```text
+-------------------+
| Customer App      |
+-------------------+
          |
          |
          v
+-------------------+
| Firebase Backend  |
| Authentication    |
| Realtime Database |
+-------------------+
          ^
          |
          |
+-------------------+
| Admin App         |
+-------------------+
```

---

# 6. Business Flow

## Customer Workflow

```text
Login/Register
      ↓
Browse Menu
      ↓
View Food Details
      ↓
Add To Cart
      ↓
Checkout
      ↓
Create Order
      ↓
Track History
```

---

## Admin Workflow

```text
Admin Login
      ↓
Manage Menu
      ↓
View Pending Orders
      ↓
Accept Orders
      ↓
Move To Completed Orders
```

---

# 7. Firebase Database Design

## Realtime Database Structure

```text
menu/
user/
admin/
OrderDetails/
CompletedOrder/
```

---

## Core Entities

### 🍔 Menu Item

```json
{
  "foodName": "Burger",
  "foodPrice": "5$",
  "foodDescription": "Cheese Burger",
  "foodImage": "image_url"
}
```

---

### 👤 User Profile

```json
{
  "name": "John",
  "email": "john@gmail.com",
  "password": "plaintext-risk"
}
```

---

### 🛒 Cart

```text
user/{uid}/CartItems/*
```

---

### 📦 Orders

```text
OrderDetails/{orderId}
CompletedOrder/{orderId}
```

---

# 8. Customer App Flow

## 🔐 Authentication

### Activities
- `LoginActivity`
- `SignActivity`

### Database Write
```text
user/{uid}
```

---

## 🍽️ Menu Browsing

### Components
- `HomeFragment`
- `SearchFragment`

### Database Read
```text
menu/*
```

---

## 🛒 Cart Management

### Activity
- `DetailsActivity`

### Database Write
```text
user/{uid}/CartItems
```

---

## 💳 Checkout Flow

### Components
- `CartFragment`
- `PayOutActivity`

### Operations
- Create order
- Copy order to history
- Clear cart

### Database Writes

```text
OrderDetails/{orderId}
user/{uid}/BuyHistory/{orderId}
```

---

## 📜 Order History

### Fragment
- `HistoryFragment`

### Status Update
```text
CompletedOrder/{orderId}/paymentReceived
```

---

# 9. Admin App Flow

## 🔐 Admin Authentication

### Activities
- `LoginActivity`
- `SignUpActivity`

### Database Write
```text
admin/{uid}
```

---

## 🍕 Menu Management

### AddItemActivity
- Upload image to ImgBB
- Save menu item to Firebase

### AllItemActivity
- Read menu items
- Delete menu items

---

## 📦 Order Processing

### PendingOrderActivity

### Features
- Read pending orders
- Accept order
- Move order to completed queue

### State Transition

```text
OrderDetails
      ↓
CompletedOrder
```

---

## 📊 Dashboard Analytics

### MainActivity
Calculates:
- total orders
- pending orders
- total revenue

⚠️ Metrics are computed client-side.

---

# 10. Scalability Review

## ✅ Current Strengths

- Firebase handles realtime synchronization
- Authentication scales reasonably for MVP usage
- Easy deployment without server maintenance

---

## ⚠️ Scalability Risks

### Full-node fetching
Many screens read:
- full menu
- full order list
- full completed orders

This can increase:
- bandwidth
- memory usage
- latency

---

### No Pagination
Missing:
- query limits
- lazy loading
- cursor pagination

---

### Client-side aggregation
Revenue and metrics are computed by scanning all orders.

This becomes inefficient at scale.

---

### Data Duplication

Order exists in:
- `OrderDetails`
- `CompletedOrder`
- `BuyHistory`

Result:
- increased write amplification
- consistency risks

---

# 11. Maintainability Review

## ✅ Strengths

- Easy-to-follow business flow
- Kotlin + ViewBinding improve readability
- Clear separation between admin/customer apps

---

## ⚠️ Risks

### Tight Coupling
Activities/Fragments directly:
- access Firebase
- handle business logic
- update UI

---

### Missing Layers

Project lacks:
- Repository layer
- Domain layer
- UseCase layer
- ViewModel abstraction

---

### Duplicate Models
Customer/admin apps duplicate many data classes.

---

### Naming Issues
Several:
- typos
- inconsistent naming
- unclear identifiers

---

### Minimal Testing
Only default Android sample tests exist.

---

# 12. Security Review

# 🚨 High-Risk Findings

## 1. Plaintext Password Storage

Database stores:
```json
{
  "password": "123456"
}
```

This is a major security vulnerability.

---

## 2. Hardcoded API Key

ImgBB API key is embedded directly in source code.

Risk:
- API abuse
- credential leakage

---

## 3. Client-Trusted Workflow

Critical order transitions occur entirely on mobile clients.

Risk:
- request forgery
- unauthorized order manipulation

---

## 4. Firebase Rules Missing

Repository does not version:
```text
Firebase Security Rules
```

Security depends heavily on external Firebase configuration.

---

# ✅ Recommended Security Improvements

- Remove password field from database
- Use FirebaseAuth only
- Move image upload behind backend service
- Add strict Firebase Security Rules
- Use admin custom claims
- Validate order transitions server-side

---

# 13. Performance Review

## ✅ Positive Points

- Glide caching
- Basic image compression before upload

---

## ⚠️ Performance Issues

### Repeated Full Reads
Large Firebase nodes loaded repeatedly.

---

### Adapter Recreation
Search/filter recreates adapters frequently.

---

### Heavy UI Classes
Activities contain:
- UI logic
- Firebase callbacks
- business logic

inside single classes.

---

# 14. Build & Run

## 📋 Prerequisites

- Android Studio
- JDK 11
- Firebase projects configured
- `google-services.json`

---

## Open Customer App

```text
WavesOfFood/
```

---

## Open Admin App

```text
AdminWave Of Food/
```

---

## Build Command

```bash
./gradlew lint test assembleDebug
```

---

## Validation Status

Current sandbox environment failed resolving:

```text
com.android.application 8.8.1
```

Likely due to:
- Gradle plugin resolution issue
- sandbox limitations

---

# 15. Production Roadmap

## 🔐 Phase 1 — Security Hardening

- Remove plaintext passwords
- Remove hardcoded API keys
- Add Firebase Security Rules
- Use admin roles/custom claims

---

## 🧱 Phase 2 — Clean Architecture

Introduce:
- Repository layer
- UseCases
- ViewModels
- Dependency Injection

---

## ⚙️ Phase 3 — Backend Services

Move:
- order validation
- state transitions
- payment verification

to backend/cloud functions.

---

## 📈 Phase 4 — Scalability

Add:
- pagination
- indexed queries
- lazy loading
- caching strategy

---

## 🧪 Phase 5 — Testing

Add:
- Unit Tests
- Integration Tests
- UI Tests

for:
- checkout
- authentication
- order workflow

---

# 16. Overall Evaluation

## ✅ Technical Strengths

- Complete food ordering workflow
- Good Firebase integration
- Clear admin/customer separation
- Suitable MVP architecture
- Easy to demo and extend

---

## ⚠️ Technical Weaknesses

- Security risks
- Heavy client-side logic
- No scalable backend architecture
- Limited testing
- Tight coupling
- Data duplication

---

# 17. Future Improvements

## Suggested Upgrades

- MVVM + Clean Architecture
- Firestore migration
- Spring Boot / Node.js backend
- JWT authorization
- Cloud Functions
- Push Notifications
- Payment Gateway Integration
- Dockerized backend
- CI/CD pipeline
- Analytics & monitoring

---

# 📌 Final Assessment

WaveOfFood demonstrates a solid MVP-level Android food ordering system with practical Firebase integration and complete end-to-end ordering flow.

The project is suitable for:
- academic/mobile app portfolios
- Firebase learning
- Android architecture practice
- MVP prototyping

However, significant improvements are required before production deployment, especially in:
- security
- backend integrity
- scalability
- maintainability
- testing strategy

---
