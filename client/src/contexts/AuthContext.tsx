import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService } from '../services/api';

interface User {
  id: string;
  email: string;
  name: string;
  role: 'archivist' | 'researcher' | 'public';
  accessLevel: string;
  status: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, name: string, role: string, organization?: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    console.log('🔑 AuthContext login called:', email);
    try {
      const response = await authService.login(email, password);
      console.log('📦 Backend response:', response);
      
      const userData = {
        id: response.userId,
        email: response.email,
        name: response.name,
        role: response.role,
        accessLevel: response.accessLevel,
        status: response.status
      };
      
      console.log('👤 User data created:', userData);
      
      setToken(response.token);
      setUser(userData);
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(userData));
      
      console.log('✅ Login successful, storage updated');
    } catch (error: any) {
      console.error('❌ AuthContext login error:', error);
      throw error;
    }
  };

  const register = async (email: string, password: string, name: string, role: string, organization?: string) => {
    try {
      const response = await authService.register(email, password, name, role, organization);
      const userData = {
        id: response.userId,
        email: response.email,
        name: response.name,
        role: response.role,
        accessLevel: response.accessLevel,
        status: response.status
      };
      setToken(response.token);
      setUser(userData);
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(userData));
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    isLoading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
