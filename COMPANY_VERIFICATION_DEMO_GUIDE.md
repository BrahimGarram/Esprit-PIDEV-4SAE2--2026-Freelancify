# Company Verification - Demo Guide

## Quick Start

The company verification feature is currently running in **DEMO MODE** for testing purposes.

### How to Test

1. **Start the application:**
   ```bash
   # Terminal 1 - Backend
   cd backend
   mvn spring-boot:run
   
   # Terminal 2 - Frontend
   cd frontend
   ng serve
   ```

2. **Access the application:**
   - Open browser: `http://localhost:4200`
   - Log in as an Enterprise user

3. **Test company verification:**
   - Click "New Company" button
   - Type one of these company names:
     - Google
     - Microsoft
     - Apple
     - Amazon
     - Meta
     - Tesla
     - Netflix
     - Oracle
     - IBM
     - Intel
   - Watch real-time verification
   - See company details appear

4. **Test unrecognized company:**
   - Type any other company name (e.g., "My Startup Inc")
   - See "not found" message
   - You can still create the company

---

## What You'll See

### ✅ Verified Company (e.g., "Google")
```
✓ Company Verified!
Google
🌍 United States
🆔 REG-123456789
📍 123 Business St, City, State 12345
🔗 https://www.google.com
(Demo Mode)
```

### ⚠️ Unrecognized Company
```
⚠ Not Verified
Company not found in registry. This is a demo - only recognizes: 
Google, Microsoft, Apple, Amazon, Meta, Tesla, Netflix, Oracle, IBM, Intel

You can still create the company, but it won't be marked as verified.
```

---

## Demo Limitations

This is a **mock implementation** that:
- ❌ Does NOT call external APIs
- ❌ Does NOT verify real companies (except hardcoded ones)
- ❌ Does NOT check business registries
- ✅ Demonstrates the UI/UX flow
- ✅ Shows how verification would work
- ✅ Allows testing without API costs

---

## Upgrading to Production

To use real company verification:

1. **Choose a provider:**
   - [OpenCorporates](https://opencorporates.com/api) - Global, free tier available
   - [Middesk](https://www.middesk.com/) - US businesses, paid
   - [Companies House](https://developer-specs.company-information.service.gov.uk/) - UK, free
   - [VerifyVAT](https://verifyvat.com/) - EU companies

2. **Update the service:**
   - Replace `CompanyVerificationService.verifyCompany()` method
   - Add HTTP client calls to chosen API
   - Handle API responses and errors
   - Add caching to reduce API calls

3. **Configure API key:**
   ```yaml
   # application.yml
   company:
     verification:
       api:
         key: ${COMPANY_API_KEY}
         url: https://api.provider.com
   ```

4. **Test thoroughly:**
   - Test with real company names
   - Handle rate limits
   - Implement fallbacks
   - Monitor costs

---

## Code Location

**Backend:**
- Service: `backend/src/main/java/com/freelance/userservice/service/CompanyVerificationService.java`
- Controller: `backend/src/main/java/com/freelance/userservice/controller/CompanyVerificationController.java`

**Frontend:**
- Service: `frontend/src/app/services/company-verification.service.ts`
- Component: `frontend/src/app/components/collaborations/collaborations.component.ts`
- Template: `frontend/src/app/components/collaborations/collaborations.component.html`

---

## Support

For questions or issues:
1. Check the integration documentation: `COMPANY_VERIFICATION_INTEGRATION.md`
2. Review the usage examples: `COMPANY_VERIFICATION_USAGE_EXAMPLE.md`
3. Check backend logs for errors
4. Verify frontend console for API call issues
