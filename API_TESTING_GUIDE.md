# ReachGRC Trust Center - REST API Testing Guide

This guide describes how to test all backend REST API endpoints using Postman.

## Getting Started

1. **Base URL**: All endpoints are configured on the local backend server:
   ```
   http://localhost:8081
   ```
2. **Postman Environment Setup**:
   Create a new Postman Environment and add the following variables:
   - `baseUrl` = `http://localhost:8081`
   - `apiKey` = `rgc_a3f9c2e1b74d6085fa91dc23e0b58764` (default seeded API key)

---

## 1. Public & Unauthenticated Endpoints

### 1.1 Health Check (No Auth)
Verify that the server is operational.
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/trust/public/health`
- **Headers**: None
- **Expected Result** (`200 OK`):
  ```
  health
  ```

### 1.2 Fetch Company Profile by ID
Fetch public GRC compliance dashboard details for a specific tenant.
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/trust/1`
- **Headers**: None
- **Expected Result** (`200 OK`):
  ```json
  {
    "id": 1,
    "companyName": "Reach GRC",
    "statement": "Risk & Compliance Management Platform",
    "domains": [
      {
        "id": 1,
        "name": "Information Security",
        "controls": [
          {
            "id": 1,
            "name": "Access Control",
            "status": "PENDING",
            "remarks": "Ensure role-based access",
            "createdAt": "2026-06-22T08:10:54",
            "updatedAt": "2026-06-22T08:10:54"
          },
          {
            "id": 2,
            "name": "Encryption",
            "status": "OK",
            "remarks": "Data encryption at rest",
            "createdAt": "2026-06-22T08:10:54",
            "updatedAt": "2026-06-22T08:10:54"
          }
        ],
        "createdAt": "2026-06-22T08:10:54",
        "updatedAt": "2026-06-22T08:10:54"
      }
    ],
    "isActive": true,
    "apiKey": "rgc_a3f9c2e1b74d6085fa91dc23e0b58764",
    "apiKeyStatus": "ACTIVE",
    "subscriptionPlan": "FREE",
    "subscriptionStatus": "ACTIVE"
  }
  ```

---

## 2. Administrative Configuration & Management

### 2.1 Get Google Sheets Sync Configs
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/sheet-config`
- **Headers**: None
- **Expected Result** (`200 OK` or `404 Not Found` if empty):
  ```json
  {
    "id": 1,
    "sheetName": "ReachGRC Sync",
    "sheetUrl": "https://docs.google.com/spreadsheets/d/1...",
    "spreadsheetId": "1...",
    "sheetTabName": "Sheet1",
    "isActive": true,
    "syncEnabled": true,
    "lastSyncTime": "2026-06-22T08:12:00",
    "lastSyncStatus": "SUCCESS"
  }
  ```

### 2.2 Create or Update Sheet Configuration
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/sheet-config`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "sheetUrl": "https://docs.google.com/spreadsheets/d/17aBcDeFgHiJkLmNoPqRsTuVwXyZ/edit",
    "spreadsheetId": "17aBcDeFgHiJkLmNoPqRsTuVwXyZ",
    "sheetTabName": "Sheet1",
    "syncEnabled": true
  }
  ```
- **Expected Result** (`200 OK`): Matches updated config object with generated ID.

### 2.3 Trigger Sheets Synchronization
Sync company, domains, and controls directly from configured Google Sheet.
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/sheet-config/sync`
- **Headers**: None
- **Expected Result** (`200 OK`):
  ```
  Sync completed successfully in 1540ms. Synced 1 companies.
  ```

### 2.4 Regenerate/Rotate API Credentials
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/trust/1/api-key/generate`
- **Headers**: None
- **Expected Result** (`200 OK`): Returns company DTO showing the newly generated API Key (prefixed with `rgc_` and 32 characters long).

### 2.5 Toggle API Key status
Revoke or reactivate client API access credentials.
- **Method**: `Patch`
- **Path**: `{{baseUrl}}/api/trust/1/api-key/status`
- **Headers**: None
- **Expected Result** (`200 OK`): Toggles `apiKeyStatus` value (e.g. from `ACTIVE` to `INACTIVE`).

---

## 3. Client Developer APIs (Authenticated)

> [!IMPORTANT]
> The endpoints in this section **require** the API key to be passed in the headers. 
> In Postman, add the header: **`x-api-key: {{apiKey}}`** on every request.

### 3.1 Get Profile Context (`/me`)
Retrieves the company GRC profile associated with the token.
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/trust/public/me`
- **Headers**: `x-api-key: {{apiKey}}`
- **Expected Result** (`200 OK`): Returns the profile details of the company owning the API key.

---

### 3.2 Logo/Image Management

#### A. List Logos
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/trust/public/image`
- **Headers**: `x-api-key: {{apiKey}}`
- **Expected Result** (`200 OK`):
  ```json
  [
    {
      "id": 1,
      "fileName": "logo.png",
      "fileType": "image/png",
      "fileSize": 20480,
      "uploadedAt": "2026-06-22T08:12:00"
    }
  ]
  ```

#### B. Upload a Logo
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/trust/public/image`
- **Headers**: `x-api-key: {{apiKey}}`
- **Body** (form-data):
  - Choose key `file` of type **File**, and select an image file (`.png`, `.jpg`).
- **Expected Result** (`200 OK`): Returns uploaded file details metadata.

#### C. Delete Logo by ID
- **Method**: `DELETE`
- **Path**: `{{baseUrl}}/api/trust/public/image/1`
- **Headers**: `x-api-key: {{apiKey}}`
- **Expected Result** (`200 OK`):
  ```
  "Deleted logo ID: 1 from company ID: 1"
  ```

---

### 3.3 Compliance Document (PDF) Management

#### A. List PDF Documents
- **Method**: `GET`
- **Path**: `{{baseUrl}}/api/trust/public/pdf/all`
- **Headers**: `x-api-key: {{apiKey}}`
- **Expected Result** (`200 OK`): Lists metadata properties of all uploaded PDF sheets.

#### B. Upload a PDF
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/trust/public/pdf/new`
- **Headers**: `x-api-key: {{apiKey}}`
- **Body** (form-data):
  - Choose key `file` of type **File**, and select a PDF file (`.pdf`).
- **Expected Result** (`200 OK`): Returns JSON metadata of the uploaded document.

---

## 4. Razorpay Subscription Billing

### 4.1 Create Billing Order
Initialize an order sequence.
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/subscription/create-order`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "companyId": 1,
    "plan": "GROWTH"
  }
  ```
- **Expected Result** (`200 OK`):
  ```json
  {
    "orderId": "order_mock_1719000000000_123",
    "amount": 499900,
    "currency": "INR",
    "keyId": "rzp_test_mockKeyId123",
    "companyId": 1,
    "plan": "GROWTH",
    "isSimulated": true
  }
  ```

### 4.2 Verify Transaction
Submit payment credentials returned by Razorpay callback to finalize database state upgrade.
- **Method**: `POST`
- **Path**: `{{baseUrl}}/api/subscription/verify`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "companyId": 1,
    "plan": "GROWTH",
    "razorpayOrderId": "order_mock_1719000000000_123",
    "razorpayPaymentId": "pay_mock_123456",
    "razorpaySignature": "sig_mock_123456"
  }
  ```
- **Expected Result** (`200 OK`):
  ```json
  {
    "success": true,
    "message": "Subscription activated successfully",
    "plan": "GROWTH",
    "status": "ACTIVE"
  }
  ```

---

## 5. Security & Error Reference Testing

Verify that API key authentication is correctly enforced on `/api/trust/public/**`.

### 5.1 API Key Missing
Send a request without adding `x-api-key` header.
- **Request**: `GET {{baseUrl}}/api/trust/public/me`
- **Headers**: None
- **Expected Result** (`401 Unauthorized`):
  ```json
  {
    "error": "API key missing"
  }
  ```

### 5.2 Api Key Not Found
Send a request with an invalid/typo key value.
- **Request**: `GET {{baseUrl}}/api/trust/public/me`
- **Headers**: `x-api-key: rgc_wrong_key_123`
- **Expected Result** (`401 Unauthorized`):
  ```json
  {
    "error": "Api key not found"
  }
  ```

### 5.3 Api Key Inactive
In Postman, toggle the key status to inactive (using Endpoint 2.5), and then attempt to access a client route.
- **Request**: `GET {{baseUrl}}/api/trust/public/me`
- **Headers**: `x-api-key: {{apiKey}}`
- **Expected Result** (`403 Forbidden`):
  ```json
  {
    "error": "Api Key is InActive"
  }
  ```
