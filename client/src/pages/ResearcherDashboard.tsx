import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import DocumentList from '../components/DocumentList';
import DocumentViewer from '../components/DocumentViewer';
import './Dashboard.css';

const ResearcherDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('search');
  const [selectedDocument, setSelectedDocument] = useState<any>(null);

  const renderContent = () => {
    switch (activeTab) {
      case 'search':
        return (
          <div className="search-section">
            <h2>Document Search</h2>
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
              onClick={() => setActiveTab('search')}
              style={{ marginBottom: '20px' }}
            >
              ← Back to Search
            </button>
            {selectedDocument && (
              <DocumentViewer document={selectedDocument} />
            )}
          </div>
        );
      case 'annotations':
        return (
          <div className="annotations-section">
            <h2>My Annotations</h2>
            <p>View and manage your document annotations and notes.</p>
            {/* Add annotations list here */}
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
          <h1>Researcher Dashboard</h1>
          <p>Welcome back, {user?.name}!</p>
        </div>

        <div className="dashboard-tabs">
          <button
            className={`tab ${activeTab === 'search' ? 'active' : ''}`}
            onClick={() => setActiveTab('search')}
          >
            🔍 Search Documents
          </button>
          <button
            className={`tab ${activeTab === 'view' ? 'active' : ''}`}
            onClick={() => setActiveTab('view')}
            disabled={!selectedDocument}
          >
            📄 Document View
          </button>
          <button
            className={`tab ${activeTab === 'annotations' ? 'active' : ''}`}
            onClick={() => setActiveTab('annotations')}
          >
            📝 My Annotations
          </button>
        </div>

        <div className="dashboard-content">
          {renderContent()}
        </div>
      </div>
    </Layout>
  );
};

export default ResearcherDashboard;
