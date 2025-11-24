import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import DocumentList from '../components/DocumentList';
import DocumentViewer from '../components/DocumentViewer';
import FeedbackForm from '../components/FeedbackForm';
import './Dashboard.css';

const PublicDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('browse');
  const [selectedDocument, setSelectedDocument] = useState<any>(null);

  const renderContent = () => {
    switch (activeTab) {
      case 'browse':
        return (
          <div className="browse-section">
            <h2>Public Documents</h2>
            <p>Browse and search public documents in our archive.</p>
            <DocumentList 
              onDocumentSelect={(doc) => {
                setSelectedDocument(doc);
                setActiveTab('view');
              }} 
            />
          </div>
        );
      case 'view':
        return (
          <div className="viewer-section">
            <button 
              className="btn btn-secondary"
              onClick={() => setActiveTab('browse')}
              style={{ marginBottom: '20px' }}
            >
              ← Back to Browse
            </button>
            {selectedDocument && (
              <DocumentViewer document={selectedDocument} readOnly />
            )}
          </div>
        );
      case 'feedback':
        return (
          <div className="feedback-section">
            <h2>Submit Feedback</h2>
            <p>Help us improve the digital archive by sharing your feedback.</p>
            <FeedbackForm />
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
          <h1>Public Archive</h1>
          <p>Welcome, {user?.name}!</p>
        </div>

        <div className="dashboard-tabs">
          <button
            className={`tab ${activeTab === 'browse' ? 'active' : ''}`}
            onClick={() => setActiveTab('browse')}
          >
            📚 Browse Documents
          </button>
          <button
            className={`tab ${activeTab === 'view' ? 'active' : ''}`}
            onClick={() => setActiveTab('view')}
            disabled={!selectedDocument}
          >
            📄 Document View
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

export default PublicDashboard;
