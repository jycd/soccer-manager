# Soccer Manager Frontend

A clean, maintainable React frontend for the Soccer Manager fantasy football application with balanced optimization and user account management.

## Features

- **User Authentication**: Login and registration with JWT tokens
- **Account Management**: Complete user profile editing and account deletion
- **Team Dashboard**: Clean interface for team statistics and player roster
- **Transfer Market**: Player browsing and purchasing functionality
- **Team Management**: Edit team information and player details
- **Responsive Design**: Mobile-friendly interface
- **Balanced Optimization**: Essential optimizations without over-engineering

## Tech Stack

- **React 18** with TypeScript and modern hooks
- **Axios** for API communication
- **JWT** for authentication
- **Component Architecture**: Essential reusable components
- **Custom Hooks**: Authentication state management
- **Clean Code**: Maintainable structure with sensible abstractions

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm start
   ```

3. Open http://localhost:3000 in your browser

## Configuration

The frontend is configured to connect to the backend API running on http://localhost:8081. Make sure the backend server is running before starting the frontend.

## Project Structure

```
src/
├── components/          # Essential React components
│   ├── Dashboard.tsx       # Main dashboard with team overview
│   ├── TeamStats.tsx       # Team statistics cards
│   ├── PlayerTable.tsx     # Player roster table
│   ├── TransferMarket.tsx  # Transfer market interface
│   ├── UserProfileEdit.tsx # User profile and account management
│   ├── Button.tsx          # Reusable button component
│   ├── Modal.tsx           # Reusable modal component
│   ├── CurrencyDisplay.tsx # Currency formatting component
│   ├── SortableHeader.tsx  # Table header component
│   └── index.ts           # Component exports
├── hooks/              # Custom React hooks
│   ├── useAuth.ts         # Authentication state management
│   └── index.ts          # Hook exports
├── services/           # API services
│   └── api.ts         # API client with JWT handling
├── types/             # TypeScript type definitions
│   └── index.ts       # Application types
├── utils/             # Utility functions
│   ├── auth.ts         # Authentication utilities
│   ├── helpers.ts      # Essential helper functions
│   └── index.ts       # Utility exports
├── constants/         # Application constants
│   └── index.ts       # API and configuration constants
├── styles/            # Design system
│   ├── theme.ts       # Color palette, typography, spacing
│   └── common.ts      # Shared styles
├── App.tsx            # Main application component
└── index.tsx          # Application entry point
```

## Usage

1. **Register/Login**: Create an account or sign in to access your team
2. **Dashboard**: View your team overview with statistics and player roster
3. **Transfer Market**: Browse available players and purchase new talent
4. **Team Management**: Edit team name/country and update player information
5. **Profile Management**: Edit user profile and delete account if needed

## Frontend Architecture

The frontend follows a balanced optimization approach:

### Essential Components Only
- **Button**: Reusable button with multiple variants
- **Modal**: Complex modal for confirmations and forms
- **CurrencyDisplay**: Specialized currency formatting
- **Core Features**: Dashboard, TransferMarket, UserProfileEdit

### Custom Hooks
- **useAuth**: Centralized authentication state and token management

### Minimal Utilities
- **formatCurrency**: Currency formatting
- **validateEmail/Password**: Basic form validation
- **authStorage**: Token management utilities

### Essential Constants
- **API_BASE_URL**: Backend API endpoint
- **POSITION_ORDER**: Player position sorting logic

## API Integration

The frontend communicates with the backend REST API using JWT authentication:

- **Authentication**: `/auth/token` (login) and `/users` (registration)
- **User Management**: `/users/{id}` (delete account)
- **Team Data**: `/teams/{id}` with optional `?with_players=true` parameter
- **Transfers**: `/transfers` (list) and `/teams/{teamId}/transfers/{transferId}` (buy)
- **Player Updates**: `/teams/{teamId}/players/{playerId}` (edit)

## Development Philosophy

This frontend follows a **balanced optimization** approach:

- **No Over-Engineering**: Simple forms use inline styles, not over-abstracted components
- **Essential Abstractions**: Only complex UI elements are extracted to components
- **Maintainability**: Clean structure that's easy to understand and modify
- **Performance**: Optimized where it matters without unnecessary complexity

## Development Notes

- **TypeScript**: Full type safety across all components
- **Modern React**: Hooks-based architecture with best practices
- **Authentication**: JWT tokens stored in localStorage with automatic cleanup
- **Error Handling**: User-friendly error messages and loading states
- **Account Deletion**: Secure user self-deletion with proper confirmation

## Troubleshooting

- **401 Unauthorized**: Check that the backend is running and CORS is configured
- **403 Forbidden**: Verify JWT token is valid and not expired
- **No players displayed**: Ensure `?with_players=true` is included in team API calls
- **Transfer errors**: Check that player status is 'ACTIVE' before transfers
- **Build errors**: Ensure all dependencies are installed and TypeScript is configured

## Build

To create a production build:
```bash
npm run build
```

The build artifacts will be stored in the `build/` directory.