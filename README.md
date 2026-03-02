# Subscription Service

A comprehensive microservice for managing subscription lifecycle and feature access control in a freelancing platform.

## Features

### Core Functionality
- **Subscription Plans Management**: Create and manage multiple subscription tiers
- **Subscription Lifecycle**: Handle creation, activation, renewal, cancellation, and expiration
- **Payment Integration**: Webhook support for payment gateway integration
- **Upgrade/Downgrade**: Seamless plan transitions with proration logic
- **Feature Access Control**: Real-time validation of user permissions
- **Auto-Renewal**: Scheduled jobs for automatic subscription renewal
- **Usage Tracking**: Monitor bid usage and enforce limits

### Subscription Workflow
1. User selects a subscription plan
2. System creates subscription with PENDING_PAYMENT status
3. Payment gateway processes payment
4. Webhook confirms payment success
5. Subscription status changes to ACTIVE
6. System monitors expiration and handles renewal
7. Feature access validated in real-time

## Architecture

Following the same pattern as complaints-service:
- **Entity Layer**: Domain models (Subscription, SubscriptionPlan, SubscriptionHistory)
- **Repository Layer**: JPA repositories for data access
- **Service Layer**: Business logic and orchestration
- **Controller Layer**: REST API endpoints
- **Scheduled Tasks**: Background jobs for renewal and expiration checks

## Database Schema

### Tables
- `subscription_plan`: Available subscription tiers
- `subscription`: User subscriptions
- `subscription_history`: Audit trail of all subscription actions

## API Endpoints

### Subscription Plans
- `GET /api/subscriptions/plans` - Get all active plans
- `GET /api/subscriptions/plans/{planId}` - Get plan details
- `POST /api/subscriptions/plans` - Create new plan (Admin)

### Subscriptions
- `POST /api/subscriptions/create` - Create new subscription
- `GET /api/subscriptions/user/{userId}/active` - Get active subscription
- `GET /api/subscriptions/user/{userId}/history` - Get subscription history
- `POST /api/subscriptions/cancel/{userId}` - Cancel subscription
- `POST /api/subscriptions/change-plan` - Upgrade/downgrade plan

### Feature Access (For Microservices)
- `POST /api/subscriptions/validate-access` - Validate feature access
- `GET /api/subscriptions/user/{userId}/can-bid` - Check bid permission
- `POST /api/subscriptions/user/{userId}/increment-bid` - Track bid usage

### Webhooks
- `POST /api/subscriptions/webhook/payment` - Payment gateway webhook

## Configuration

### application.yml
```yaml
server:
  port: 8091
  servlet:
    context-path: /api/subscriptions

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freelancify-subscriptions
```

## Scheduled Jobs

- **Expiration Check**: Runs hourly to mark expired subscriptions
- **Auto-Renewal**: Runs daily at 8 AM to renew subscriptions 3 days before expiration
- **Renewal Reminders**: Runs daily at 9 AM to send reminders 7 days before expiration

## Feature Access Control

### Supported Features
- **BID**: Check if user can place bids (with monthly limits)
- **ANALYTICS**: Access to analytics dashboard
- **PROFILE_BOOST**: Profile visibility boost
- **COMMISSION**: Dynamic commission rate based on plan

### Integration Example
```java
// From another microservice
FeatureAccessRequest request = new FeatureAccessRequest();
request.setUserId(userId);
request.setFeatureName("BID");

FeatureAccessResponse response = restTemplate.postForObject(
    "http://localhost:8091/api/subscriptions/validate-access",
    request,
    FeatureAccessResponse.class
);

if (response.getHasAccess()) {
    // Allow action
}
```

## Running the Service

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven

### Start Service
```bash
# Windows
run.bat

# Linux/Mac
./mvnw spring-boot:run
```

### Database Setup
The service automatically creates the database `freelancify-subscriptions` on startup.

## Sample Subscription Plans

### Free Plan
- 5 bids per month
- 10% commission
- No analytics
- No profile boost

### Basic Plan ($9.99/month)
- 20 bids per month
- 7% commission
- Basic analytics
- No profile boost

### Pro Plan ($29.99/month)
- 100 bids per month
- 5% commission
- Full analytics
- Profile boost
- Priority support

### Enterprise Plan ($99.99/month)
- Unlimited bids
- 3% commission
- Full analytics
- Profile boost
- Priority support
- Featured listing

## Payment Integration

The service includes webhook endpoints for payment gateway integration. Currently supports:
- Payment confirmation
- Payment failure handling
- Automatic renewal payments

To integrate with a payment gateway (Stripe, PayPal, etc.):
1. Update `PaymentService.initiatePayment()` with gateway API
2. Configure webhook URL in payment gateway dashboard
3. Update webhook secret in application.yml

## Monitoring & Logging

All subscription actions are logged to `subscription_history` table for audit purposes:
- Subscription creation
- Payment received/failed
- Activation
- Renewal
- Upgrade/downgrade
- Cancellation
- Expiration

## Error Handling

Global exception handler provides consistent error responses:
- `SubscriptionException`: Business logic errors
- `RuntimeException`: Unexpected errors
- All errors logged with timestamps

## Security Considerations

- Webhook signature verification (TODO)
- User authentication integration (TODO)
- Admin role validation for plan management (TODO)
- Rate limiting for API endpoints (TODO)

## Future Enhancements

- [ ] Stripe/PayPal integration
- [ ] Email notifications for renewals
- [ ] Proration refunds
- [ ] Trial periods
- [ ] Discount codes/coupons
- [ ] Usage analytics dashboard
- [ ] Multi-currency support
- [ ] Invoice generation
