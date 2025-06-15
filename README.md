# Split(NotSo)Wise 💸

A client-server console application inspired by [Splitwise](https://www.splitwise.com), designed to simplify splitting expenses among friends, housemates, and groups — and to end the eternal dormitory beer payment disputes.

## 🧠 Project Overview

Split(NotSo)Wise allows users to register, log in, manage friendships and groups, track shared expenses, and settle debts. The application uses a server-side file-based database to persist user and transaction data.

## 🧰 Features

### ✅ User Management
- **Register** new users with `username` and `password`
- **Login** securely
- Server stores users in a persistent file-based system

### 👥 Friend & Group Management
- `add-friend <username>` – Add a registered user to your friend list
- `create-group <group_name> <username>...` – Create a group with 3+ users

### 💸 Expense Splitting
- `split <amount> <username> <reason>` – Split an expense 50/50 with a friend
- `split-group <amount> <group_name> <reason>` – Split an expense equally within a group
- Automatic debt tracking and recalculations

### 📊 Status & Payments
- `get-status` – View how much you owe and are owed by friends and in groups
- `payed <amount> <username>` – Accept payment and update debts

### 🔔 Notifications
- On login, users see recent updates and transactions involving them

### 🧾 Payment History
- Users can view their transaction history (saved server-side)

### 🌍 Currency Conversion (Bonus)
- `switch-currency <CURRENCY_CODE>` – Convert all balances into another currency (e.g., EUR, USD)
- Fetches real-time exchange rates via HTTP API

## 🖥️ Usage

Commands are entered via a console-based client interface.

Example interaction:
```bash
$ login alex alexslongpassword
Successful login!
*** Notifications ***
Friends:
Misho approved your payment 10 LV [Mixtape beers].

Groups:
* Roommates:
You owe Gery 20 LV [Tanya Bday Present]
