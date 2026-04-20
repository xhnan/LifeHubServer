---
name: lifehub-bookkeeping
description: Resolve bookkeeping requests for the LifeHub finance system. Use when OpenClaw or Codex needs to record an expense, income, or transfer into LifeHub, choose the user's default book, resolve valid subject accounts from that book, build balanced double-entry payloads, and call the transaction creation API.
---

# LifeHub Bookkeeping

Convert natural-language bookkeeping requests into valid LifeHub finance API calls.

Use this base URL for all LifeHub API calls in this skill:

```text
https://api.xhnya.top
```

## Workflow

Always follow this order:

0. Send the user's token in the `X-API-Key` request header for every LifeHub API call.
1. Call `GET /fin/skills/bookkeeping-context`.
2. Read `defaultBook.id` as the `bookId` unless the user explicitly requests another accessible book.
3. Read `subjectCategories` and choose valid subject accounts only from the returned lists.
4. Infer the bookkeeping intent: `expense`, `income`, or `transfer`.
5. Build a balanced `entries` array.
6. Call `POST /fin/transactions/with-entries`.

Do not invent `bookId`.
Do not invent `accountId`.
Do not call the transaction API before context resolution finishes.
Do not assume account codes are stable across users; always trust the returned `id` values.

## Authentication

All LifeHub API calls in this skill must include the user's token in this header:

```http
X-API-Key: <user-token>
```

Rules:

- Always pass the token with the exact header name `X-API-Key`
- Do not put the token in the query string
- Do not rename the header to `Authorization`
- Use the same header for both the context API and the transaction API
- If a request fails because the token is missing, retry only after restoring the `X-API-Key` header

Example context request:

```http
GET /fin/skills/bookkeeping-context HTTP/1.1
Host: api.xhnya.top
X-API-Key: <user-token>
```

Example transaction request:

```http
POST /fin/transactions/with-entries HTTP/1.1
Host: api.xhnya.top
Content-Type: application/json
X-API-Key: <user-token>
```

## Context API

Use `GET /fin/skills/bookkeeping-context` as the first step for bookkeeping requests.

It returns:

- `defaultBook`
- `subjectCategories`

`defaultBook` includes:

- `id`
- `name`
- `description`
- `defaultCurrency`
- `coverUrl`
- `fromUserConfig`

`subjectCategories` includes:

- `expense.occurrenceSubjects`
- `expense.paymentSubjects`
- `income.occurrenceSubjects`
- `income.receiptSubjects`
- `allSubjects`

Interpret them as:

- `expense.occurrenceSubjects`: valid expense category subjects such as dining, transport, shopping
- `expense.paymentSubjects`: valid payment accounts, usually `ASSET` or `LIABILITY`
- `income.occurrenceSubjects`: valid income category subjects such as salary or bonus
- `income.receiptSubjects`: valid receipt accounts, usually `ASSET` or `LIABILITY`

Prefer the sorted order returned by the API when multiple subjects are reasonable matches.

## Transaction Model

This project uses double-entry bookkeeping.

Create transactions with `POST /fin/transactions/with-entries`.

Build this payload shape:

```json
{
  "transDate": "2026-04-20 12:30:00",
  "description": "lunch",
  "bookId": 123,
  "entries": [
    {
      "accountId": 50101,
      "direction": "DEBIT",
      "amount": "25.00",
      "memo": "meal expense"
    },
    {
      "accountId": 10103,
      "direction": "CREDIT",
      "amount": "25.00",
      "memo": "wechat payment"
    }
  ]
}
```

Use string values for `amount`.

The backend requires:

- `transDate` must exist
- `transDate` must use `yyyy-MM-dd HH:mm:ss`
- `bookId` must exist
- `entries` must not be empty
- each `amount` must be a positive number
- each `direction` must be `DEBIT` or `CREDIT`
- total debit must equal total credit within `0.01`

Do not send ISO datetime with `T` such as `2026-04-20T12:30:00`; that format was rejected in real testing.

## Entry Rules

Use these account-direction rules:

- `ASSET` and `EXPENSE` usually increase with `DEBIT`
- `LIABILITY`, `EQUITY`, and `INCOME` usually increase with `CREDIT`

### Expense

For a normal expense:

- expense category subject: `DEBIT`
- payment account subject: `CREDIT`

Example:

- user says `lunch 25, paid by wechat`
- choose one account from `expense.occurrenceSubjects` for dining
- choose one account from `expense.paymentSubjects` for WeChat
- build:
  - dining `DEBIT 25.00`
  - wechat `CREDIT 25.00`

### Income

For a normal income:

- receipt account subject: `DEBIT`
- income category subject: `CREDIT`

Example:

- user says `salary 12000, received in bank card`
- choose one account from `income.receiptSubjects` for the bank card
- choose one account from `income.occurrenceSubjects` for salary
- build:
  - bank account `DEBIT 12000.00`
  - salary income `CREDIT 12000.00`

### Transfer

For a transfer between the user's own accounts:

- destination account: `DEBIT`
- source account: `CREDIT`

Do not use expense or income subjects for a pure transfer.

## Mapping Rules

Resolve subjects from the context response instead of hard-coding mappings.

Use this matching priority:

1. Exact semantic match to the user's wording
2. Common near-synonym match within the correct returned category
3. First high-confidence candidate near the top of the API-sorted list

If confidence is low, ask a clarification question instead of guessing.

When the user is recording an expense:

- infer `amount`
- infer `description`
- resolve one expense category account
- resolve one payment account

When the user is recording income:

- infer `amount`
- infer `description`
- resolve one income category account
- resolve one receipt account

When the user is recording a transfer:

- resolve source account
- resolve destination account
- avoid expense and income subjects

Preserve the user's original wording in `description` unless a clearer short description is obvious.

If the user gives no timestamp, use the current local time when creating `transDate`.
Format the generated timestamp exactly as `yyyy-MM-dd HH:mm:ss`.

If the user gives no currency, rely on the selected book's `defaultCurrency`. Do not add a currency field unless the API supports it.

### Practical Matching Hints

Use the returned `subjectCategories` as the only candidate pool.

For common expense wording, prefer these interpretations when they exist in the returned list:

- `wufan`, `zaocan`, `wanfan`, `chifan`, `canyin`, `waimai`, or English dining wording -> dining-related expense subject such as a meals category
- `wechat pay`, `wechat payment`, `weixin`, or `wx` -> payment subject such as WeChat
- `alipay pay`, `alipay`, or `zhifubao` -> payment subject such as Alipay
- `cash pay` or `cash` -> payment subject such as Cash

These are semantic hints, not fixed ids. The final selected subjects must still come from the current context response.

### Worked Example

User request:

```text
lunch 25 wechat pay
```

Interpretation:

- type: `expense`
- amount: `25.00`
- description: `lunch`
- expense subject: choose a dining subject from `expense.occurrenceSubjects`
- payment subject: choose a WeChat-like payment subject from `expense.paymentSubjects`

If the context response contains:

- a meals expense subject with id `2020050575622000657`
- a WeChat payment subject with id `2020050575622000643`
- `defaultBook.id = 2`

Build:

```json
{
  "bookId": 2,
  "transDate": "2026-04-20 14:32:09",
  "description": "lunch",
  "entries": [
    {
      "accountId": 2020050575622000657,
      "direction": "DEBIT",
      "amount": "25.00",
      "memo": "lunch"
    },
    {
      "accountId": 2020050575622000643,
      "direction": "CREDIT",
      "amount": "25.00",
      "memo": "wechat pay"
    }
  ]
}
```

This exact flow was verified successfully against the backend.

## Clarification Policy

Ask only when a required field cannot be resolved safely.

Ask for clarification if:

- the amount is missing
- no suitable subject account can be matched
- a transfer is mentioned but source or destination account is missing
- the user explicitly refers to another book and that book is not available in the returned context

Do not ask for optional fields unless the user specifically wants a more detailed record.

## Success Response

After a successful create call, respond concisely with:

- the transaction type
- the amount
- the selected book name when helpful
- the short description

## Forbidden Behavior

Never:

- invent book ids
- invent account ids
- send unbalanced entries
- treat this API as single-entry bookkeeping
- skip the context endpoint
- use expense subjects as payment accounts
- use income subjects as receipt accounts
