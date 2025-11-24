import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';

// Pages
import Login from './pages/Login';
import Register from './pages/Register';
import ArchivistDashboard from './pages/ArchivistDashboard';
import ResearcherDashboard from './pages/ResearcherDashboard';
import PublicDashboard from './pages/PublicDashboard';

const PrivateRoute: React.FC<{ children: React.ReactElement, allowedRoles?: string[] }> = ({ 
  children, 
  allowedRoles 
}) => {
  const { isAuthenticated, user, isLoading } = useAuth();

  if (isLoading) {
    return <div className="loading"><div className="spinner"></div></div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" />;
  }

  return children;
};

const DashboardRouter: React.FC = () => {
  const { user } = useAuth();

  if (!user) return <Navigate to="/login" />;

  switch (user.role) {
    case 'archivist':
      return <ArchivistDashboard />;
    case 'researcher':
      return <ResearcherDashboard />;
    case 'public':
      return <PublicDashboard />;
    default:
      return <Navigate to="/login" />;
  }
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route 
            path="/dashboard" 
            element={
              <PrivateRoute>
                <DashboardRouter />
              </PrivateRoute>
            } 
          />
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route 
            path="/unauthorized" 
            element={
              <div style={{ padding: '40px', textAlign: 'center' }}>
                <h1>Unauthorized Access</h1>
                <p>You don't have permission to access this page.</p>
              </div>
            } 
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
