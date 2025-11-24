import React, { useState, useEffect } from 'react';
import { documentService } from '../services/api';
import './DocumentList.css';

interface DocumentListProps {
  limit?: number;
  onDocumentSelect?: (document: any) => void;
}

const DocumentList: React.FC<DocumentListProps> = ({ limit, onDocumentSelect }) => {
  const [documents, setDocuments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [category, setCategory] = useState('all');

  useEffect(() => {
    loadDocuments();
  }, [searchTerm, category]);

  const loadDocuments = async () => {
    try {
      setLoading(true);
      const params: any = {};
      if (searchTerm) params.search = searchTerm;
      if (category !== 'all') params.category = category;
      
      const data = await documentService.getAll(params);
      setDocuments(limit ? data.slice(0, limit) : data);
    } catch (error) {
      console.error('Error loading documents:', error);
    } finally {
      setLoading(false);
    }
  };

  const getAccessBadgeClass = (accessLevel: string) => {
    return `badge badge-${accessLevel}`;
  };

  if (loading) {
    return <div className="loading"><div className="spinner"></div></div>;
  }

  return (
    <div className="document-list">
      {!limit && (
        <div className="document-filters">
          <input
            type="text"
            placeholder="Search documents..."
            className="input search-input"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <select
            className="input"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="all">All Categories</option>
            <option value="historical">Historical</option>
            <option value="legal">Legal</option>
            <option value="research">Research</option>
            <option value="administrative">Administrative</option>
            <option value="cultural">Cultural</option>
            <option value="other">Other</option>
          </select>
        </div>
      )}

      {documents.length === 0 ? (
        <p className="no-documents">No documents found.</p>
      ) : (
        <div className="documents-grid">
          {documents.map((doc) => (
            <div 
              key={doc._id} 
              className="document-card"
              onClick={() => onDocumentSelect && onDocumentSelect(doc)}
              style={{ cursor: onDocumentSelect ? 'pointer' : 'default' }}
            >
              <div className="document-header">
                <h3>{doc.title}</h3>
                <span className={getAccessBadgeClass(doc.accessLevel)}>
                  {doc.accessLevel}
                </span>
              </div>
              <p className="document-description">{doc.description}</p>
              <div className="document-meta">
                <span className="document-category">📁 {doc.category}</span>
                <span className="document-views">👁️ {doc.viewCount} views</span>
              </div>
              <div className="document-footer">
                <span className="document-date">
                  {new Date(doc.createdAt).toLocaleDateString()}
                </span>
                <span className="document-size">
                  {(doc.fileSize / 1024).toFixed(2)} KB
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DocumentList;
