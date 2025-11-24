import React, { ReactNode } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import Logo from './Logo';
import './Layout.css';

interface LayoutProps {
  children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header className="layout-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="logo">
              <Logo size={40} /> Paperless World
            </h1>
          </div>
          <div className="header-right">
            <div className="user-info">
              <span className="user-name">{user?.name}</span>
              <span className={`user-role badge badge-${user?.role}`}>{user?.role}</span>
            </div>
            <button onClick={handleLogout} className="btn btn-secondary btn-logout">
              Logout
            </button>
          </div>
        </div>
      </header>
      <main className="layout-main">
        <div className="container">
          {children}
        </div>
      </main>
      <footer className="layout-footer">
        <p>© 2025 Paperless World - Digital Archive System | Team Double A</p>
      </footer>
    </div>
  );
};

export default Layout;
