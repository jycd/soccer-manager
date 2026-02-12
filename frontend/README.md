# Soccer Manager Frontend

A modern, optimized React frontend for the Soccer Manager fantasy football application with enhanced visual design and performance optimizations.

## Features

- **Modern UI Design**: Beautiful gradient-based interface with smooth animations
- **User Authentication**: Login and registration with JWT tokens
- **Enhanced Dashboard**: Optimized team statistics and player roster with responsive layout
- **Transfer Market**: Visually stunning player browsing and purchasing interface
- **Team Management**: Inline editing with modern form controls and validation
- **Responsive Design**: Mobile-first approach with no horizontal scrolling
- **Performance Optimized**: React best practices with memoization and component splitting

## Tech Stack

- **React 18** with TypeScript and modern hooks
- **React Router DOM** for navigation
- **Axios** for API communication
- **JWT** for authentication
- **Modern CSS** with gradients, animations, and design system
- **Component Architecture**: Reusable UI components with consistent theming
- **Performance Optimizations**: useCallback, useMemo, and code splitting

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
├── components/          # React components
│   ├── Dashboard.tsx       # Main dashboard with optimized layout
│   ├── TeamStats.tsx       # Team statistics cards component
│   ├── PlayerTable.tsx     # Optimized player table with no horizontal scroll
│   ├── TransferMarket.tsx  # Enhanced transfer market with modern design
│   ├── Button.tsx          # Reusable button component with variants
│   ├── Modal.tsx           # Reusable modal component
│   └── SortableHeader.tsx  # Reusable table header component
├── services/           # API services
│   └── api.ts         # API client with JWT handling
├── types/             # TypeScript type definitions
│   └── index.ts       # Application types
├── styles/            # Design system
│   ├── theme.ts       # Color palette, typography, spacing
│   └── common.ts      # Shared styles
├── App.tsx            # Main application component
├── index.tsx          # Application entry point
└── index.css          # Global styles with animations
```

## Usage

1. **Register/Login**: Create an account or sign in to access your team
2. **Dashboard**: View your team overview with beautiful statistics cards and player roster
3. **Transfer Market**: Browse available players with enhanced visual design and purchase new talent
4. **Team Management**: Edit team name/country with modern inline editing and update player information

## Design System

The frontend uses a comprehensive design system:

- **Color Palette**: Modern indigo/purple theme with semantic colors
- **Typography**: Consistent font scales and weights
- **Spacing**: Standardized spacing system
- **Components**: Reusable UI components with consistent styling
- **Animations**: Smooth transitions and micro-interactions
- **Responsive**: Mobile-first design approach

## Performance Optimizations

- **Component Splitting**: Large components broken into focused, reusable pieces
- **React Hooks**: useCallback and useMemo for optimal rendering
- **Memoization**: Player status calculations optimized
- **Bundle Optimization**: Unused imports and code removed
- **CSS Optimization**: Consolidated styles and animations

## API Integration

The frontend communicates with the backend REST API using JWT authentication:

- **Authentication**: `/auth/token` (login) and `/users` (registration)
- **Team Data**: `/teams/{id}` with optional `?with_players=true` parameter
- **Transfers**: `/transfers` (list) and `/transfers/{id}/buy` (purchase)
- **Player Updates**: `/teams/{teamId}/players/{playerId}` (edit)

## Development Notes

- **TypeScript**: Full type safety across all components
- **Modern React**: Hooks-based architecture with best practices
- **Design System**: Consistent theming and component library
- **Performance**: Optimized rendering and minimal re-renders
- **Accessibility**: Semantic HTML and keyboard navigation
- **Error Handling**: User-friendly error messages and loading states

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