import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { documentService, userService, feedbackService } from '../services/api';
import Layout from '../components/Layout';
import DocumentUpload from '../components/DocumentUpload';
import DocumentList from '../components/DocumentList';
import UserManagement from '../components/UserManagement';
import FeedbackManagement from '../components/FeedbackManagement';
import StatsCards from '../components/StatsCards';
import './Dashboard.css';

const ArchivistDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [stats, setStats] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const [docStats, userStats] = await Promise.all([
        documentService.getStats(),
        userService.getStats()
      ]);
      setStats({ documents: docStats, users: userStats });
    } catch (error) {
      console.error('Error loading stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'overview':
        return (
          <div className="overview-section">
            <h2>Dashboard Overview</h2>
            {loading ? (
              <div className="loading"><div className="spinner"></div></div>
            ) : stats ? (
              <StatsCards stats={stats} />
            ) : null}
            <div className="recent-activity">
              <h3>Recent Documents</h3>
              <DocumentList limit={5} />
            </div>
          </div>
        );
      case 'documents':
        return (
          <div className="documents-section">
            <h2>Document Management</h2>
            <DocumentUpload onUploadSuccess={() => {}} />
            <DocumentList />
          </div>
        );
      case 'users':
        return (
          <div className="users-section">
            <h2>User Access Management</h2>
            <UserManagement />
          </div>
        );
      case 'feedback':
        return (
          <div className="feedback-section">
            <h2>Feedback Management</h2>
            <FeedbackManagement />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <Layout>
      <div className="dashboard">
        <div className="dashboard-header">
          <h1>Archivist Dashboard</h1>
          <p>Welcome back, {user?.name}!</p>
        </div>

        <div className="dashboard-tabs">
          <button
            className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            📊 Overview
          </button>
          <button
            className={`tab ${activeTab === 'documents' ? 'active' : ''}`}
            onClick={() => setActiveTab('documents')}
          >
            📁 Documents
          </button>
          <button
            className={`tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            👥 Users
          </button>
          <button
            className={`tab ${activeTab === 'feedback' ? 'active' : ''}`}
            onClick={() => setActiveTab('feedback')}
          >
            💬 Feedback
          </button>
        </div>

        <div className="dashboard-content">
          {renderContent()}
        </div>
      </div>
    </Layout>
  );
};

export default ArchivistDashboard;
