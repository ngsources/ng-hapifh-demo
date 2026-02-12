# FHIR Organization & Patient Management

This API provides FHIR-compliant endpoints for managing **Organization** and **Patient** resources.

All endpoints:

- Follow FHIR R4 specification
- Require JWT authentication (unless otherwise configured)
- Return `application/fhir+json`
- Use `OperationOutcome` for error responses

---

# Base URL

/fhir


---

# Authentication

All endpoints (except `/fhir/metadata`) require:

Authorization: Bearer <JWT_TOKEN>


---

# Organization Management

## 1. Get Organization by ID

**Endpoint**

GET /fhir/Organization/{id}


**Required Role**

- READ or WRITE

**Description**

Returns a single Organization resource by its logical ID.

**Success Response**

- `200 OK`
- FHIR `Organization` resource

**Error Responses**

- `401 Unauthorized` – Missing or invalid token
- `403 Forbidden` – Insufficient role
- `404 Not Found` – Organization does not exist
- `500 Internal Server Error` – Server failure

---

## 2. Search Organizations

**Endpoint**

GET /fhir/Organization?name={name}


**Required Role**

- READ or WRITE

**Description**

Searches Organizations using standard FHIR search parameters.

**Example**

GET /fhir/Organization?name=Test


**Success Response**

- `200 OK`
- FHIR `Bundle` (type: searchset)

**Error Responses**

- `401 Unauthorized`
- `403 Forbidden`
- `500 Internal Server Error`

---

## 3. Create Organization

**Endpoint**

POST /fhir/Organization


**Required Role**

- WRITE

**Request Body**

- FHIR `Organization` resource
- Content-Type: `application/fhir+json`

**Success Response**

- `201 Created`
- Created `Organization` resource
- `Location` header with new resource ID

**Error Responses**

- `400 Bad Request` – Invalid resource
- `401 Unauthorized`
- `403 Forbidden`
- `500 Internal Server Error`

---

# Patient Management

## 1. Get Patient by ID

**Endpoint**

GET /fhir/Patient/{id}


**Required Role**

- READ or WRITE

**Description**

Returns a single Patient resource by its logical ID.

**Success Response**

- `200 OK`
- FHIR `Patient` resource

**Error Responses**

- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `500 Internal Server Error`

---

## 2. Search Patients

**Endpoint**

GET /fhir/Patient?family={familyName}


**Required Role**

- READ or WRITE

**Description**

Searches Patients using standard FHIR search parameters.

**Example**

GET /fhir/Patient?family=Doe


**Success Response**

- `200 OK`
- FHIR `Bundle` (type: searchset)

**Error Responses**

- `401 Unauthorized`
- `403 Forbidden`
- `500 Internal Server Error`

---

## 3. Create Patient

**Endpoint**

POST /fhir/Patient


**Required Role**

- WRITE

**Request Body**

- FHIR `Patient` resource
- Content-Type: `application/fhir+json`

**Success Response**

- `201 Created`
- Created `Patient` resource
- `Location` header with new resource ID

**Error Responses**

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `500 Internal Server Error`

---
# Request Processing Flow

The following sequence diagram illustrates how requests are processed
through the Security layer, HAPI FHIR server, service layer, and database.

It also shows how different error scenarios are handled.

---



---
# Error Handling

All error responses return a FHIR-compliant `OperationOutcome` resource.

Example:

```json
{
  "resourceType": "OperationOutcome",
  "issue": [
    {
      "severity": "error",
      "code": "exception",
      "diagnostics": "Service temporarily unavailable"
    }
  ]
}
```


## Error Handling Strategy

The API follows FHIR-compliant error handling using `OperationOutcome` for all error responses.

### Response Matrix

| Scenario                     | HTTP Status | Response Body         | Description |
|------------------------------|------------|-----------------------|------------|
| Missing token                | 401        | OperationOutcome      | Authentication is required but no JWT token was provided. |
| Invalid / expired token      | 401        | OperationOutcome      | The provided JWT token is invalid or expired. |
| Insufficient role            | 403        | OperationOutcome      | The authenticated user does not have the required role. |
| Database unavailable         | 500        | OperationOutcome      | Infrastructure-level failure (e.g., database down). |
| Unhandled server exception   | 500        | OperationOutcome      | Unexpected internal server error. |

---


# LocaL usage
- checkout the project
- execute the "docker compose build"
- execute the "docker compose up"
- import the json in postman directory to a postman collection
- you can try the endpoints (first must be the login)
