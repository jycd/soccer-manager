# Soccer Manager

A fantasy soccer management application where users can create teams, buy/sell players, and manage their squad.

## Features

- User authentication (registration/login)
- Team management with 20 players per team
- Player transfer market with buying/selling functionality
- Team budget management
- Player value appreciation after transfers

## Task Detail

You need to write a RESTful or GraphQL API for a simple application where football/soccer fans will create fantasy teams and will be able to sell or buy players.

* Users must be able to create an account and log in using the API.
* Each user can have only one team (user is identified by an email)
* When the user is signed up, they should get a team of 20 players (the system should generate players):
  * 3 goalkeepers
  * 6 defenders
  * 6 midfielders
  * 5 attackers
* Each player has an initial value of $1,000,000.
* Each team has an additional $5,000,000 to buy other players.
* When logged in, a user can see their team and player information
* The team has the following information:
  * Team name and team country (can be edited).
  * Team value (sum of player values).
* The Player has the following information
  * First name, last name, country (can be edited by a team owner).
  * Age (random number from 18 to 40) and market value.
* A team owner can set the player on a transfer list
* When a user places a player on a transfer list, they must set the asking price/value for this player. This value should be listed on a market list. When another user/team buys this player, they must be bought for this price.
* Each user should be able to see all players on a transfer list.
* With each transfer, team budgets are updated.
* When a player is transferred to another team, their value should be increased between 10 and 100 percent. Implement a random factor for this purpose.
* Make it possible to perform all user actions via RESTful or GraphQL API, including authentication.

## Prerequisites

Please make sure the following tools installed before setting up the system:

### Backend
* Java 11
* PostgreSQL 14
* Maven 3.6+

### Frontend
* Node.js 16+
* npm or yarn

## Installation

### Backend Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd soccer-manager
   ```

2. Configure database:
   - Update PostgreSQL credentials in `src/main/resources/application.properties`
   - Replace `${YOUR_JWT_SECRET}` with your JWT secret

3. Run the backend:
   ```bash
   mvn clean install spring-boot:run
   ```

The backend service will run on port 8081.

### Frontend Setup
1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend will run on port 3000 and proxy API requests to the backend.

## API Endpoints

### Authentication
- `POST /auth/token` - Login
- `POST /users` - Register new user

### Teams
- `GET /teams/{id}` - Get team details
- `PATCH /teams/{id}` - Update team information

### Players
- `PATCH /teams/{teamId}/players/{playerId}` - Update player information

### Transfers
- `GET /transfers` - Get all transfer listings
- `POST /teams/{teamId}/transfers` - Put player on transfer list
- `DELETE /teams/{teamId}/transfers/{transferId}` - Buy player from transfer list
- `PATCH /teams/{teamId}/transfers/{transferId}` - Update transfer price
- `DELETE /teams/{teamId}/transfers/{playerId}` - Remove player from transfer list

## Technology Stack

### Backend
- Spring Boot 2.7.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Lombok

### Frontend
- React 18
- TypeScript
- Axios
- CSS-in-JS styling

## Development Notes

- The backend uses JWT tokens for authentication
- The frontend stores tokens in localStorage
- All API requests are proxied through the React development server
- Player values increase by 10-100% after transfers (random factor)
