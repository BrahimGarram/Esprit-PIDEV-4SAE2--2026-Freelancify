<div align="center">

# 🚀 Freelance Management Platform

**Microservices Architecture — Connect clients & freelancers**

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Angular](https://img.shields.io/badge/Angular-17+-DD0031?style=flat-square&logo=angular)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring)](https://spring.io/)
[![Keycloak](https://img.shields.io/badge/Keycloak-Auth-FF6900?style=flat-square)](https://www.keycloak.org/)
[![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)

</div>

---

## 📌 Overview

A **Freelance Management Platform** built with a **microservices architecture**, enabling clients and freelancers to collaborate through dedicated services.

| Domain | Description |
|--------|--------------|
| 📝 **Posts** | Publish and manage posts (clients & freelancers) |
| 🧪 **Tests** | Technical tests and evaluations |
| 💳 **Payments** | Payments and transactions |
| 📋 **Complaints** | User complaints and support (reclamations) |
| 📁 **Projects** | Freelance projects and assignments |

**Stack at a glance:**

- **Backend:** Spring Boot microservices behind an **API Gateway**
- **Frontend:** Angular SPA
- **Auth:** Keycloak (login, roles, JWT)
- **Data:** Centralized database for freelance-related data

---

## 🏗️ Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐     ┌──────────────────┐     ┌──────────┐
│   Client    │────▶│   Angular   │────▶│   API Gateway   │────▶│  Microservices    │────▶│ Database │
│   (User)    │     │  Frontend   │     │  (single entry) │     │  (Spring Boot)    │     │  (MySQL) │
└─────────────┘     └─────────────┘     └─────────────────┘     └──────────────────┘     └──────────┘
```

### Microservices

| Service | Role |
|---------|------|
| **User Service** | User management, secured with Keycloak |
| **Post Service** | Posts published by clients/freelancers |
| **Test Service** | Technical tests and evaluations |
| **Payment Service** | Payments and transactions |
| **Reclamation Service** | Complaints and support requests |
| **Project Service** | Freelance projects and assignments |

---

## 🔐 Authentication — Keycloak

Authentication and authorization are handled by **Keycloak** and enforced at the API Gateway:

| Feature | Description |
|---------|-------------|
| 🔒 **Login & registration** | Secure sign-up and sign-in |
| 👥 **Roles** | Admin, Client, Freelancer |
| 🎫 **JWT** | Token-based authentication |
| 🏠 **Identity** | Centralized user management |

---

## ⚙️ Tech Stack

<table>
<tr>
<td width="33%">

**Backend**
- Java 17+
- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Security
- Keycloak

</td>
<td width="33%">

**Frontend**
- Angular
- TypeScript
- Bootstrap / Material UI

</td>
<td width="33%">

**Data & tools**
- MySQL
- REST APIs
- Maven

</td>
</tr>
</table>

---

<div align="center">

*Built with Spring Boot & Angular — Microservices Architecture*

</div>
