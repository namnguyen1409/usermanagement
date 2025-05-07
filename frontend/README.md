# User Management System

This project is a User Management System built with React, TypeScript, and Vite. It provides a frontend interface for managing users, including features like user registration, login, profile editing, and more.

## Features

- **User Authentication**: Login and registration functionality.
- **Role-Based Access Control**: Manage user permissions and roles.
- **User Management**: Create, edit, and delete users.
- **Profile Management**: Edit user profiles.
- **Login History**: View user login history.
- **Responsive Design**: Optimized for various screen sizes.

## Project Structure

```
frontend/
├── public/                # Static assets
├── src/                   # Source code
│   ├── assets/            # Images and other assets
│   ├── components/        # Reusable components
│   ├── hooks/             # Custom hooks
│   ├── pages/             # Page components
│   ├── router/            # Application routing
│   └── utils/             # Utility functions
├── eslint.config.js       # ESLint configuration
├── index.html             # HTML template
├── package.json           # Project dependencies
├── tsconfig.json          # TypeScript configuration
├── vite.config.ts         # Vite configuration
└── README.md              # Project documentation
```

## Getting Started

### Prerequisites

- Node.js (>= 16.x)
- npm or yarn

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/namnguyen1409/usermanagement.git
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   ```

3. Start the development server:
   ```bash
   npm run dev
   # or
   yarn dev
   ```

4. Open your browser and navigate to `http://localhost:3001`.

## Scripts

- `npm run dev`: Start the development server.
- `npm run build`: Build the project for production.
- `npm run preview`: Preview the production build.
- `npm run lint`: Run ESLint to check for code issues.

## ESLint Configuration

This project uses ESLint for code linting. The configuration is located in `eslint.config.js`. To expand the configuration for production, refer to the [official documentation](https://eslint.org/).

## License

This project is licensed under the MIT License. See the LICENSE file for details.
