import React, { useState, useEffect } from 'react';
import { annotationService } from '../services/api';

interface DocumentViewerProps {
  document: any;
  readOnly?: boolean;
}

const DocumentViewer: React.FC<DocumentViewerProps> = ({ document, readOnly = false }) => {
  const [annotations, setAnnotations] = useState<any[]>([]);
  const [newNote, setNewNote] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (document && !readOnly) {
      loadAnnotations();
    } else {
      setLoading(false);
    }
  }, [document]);

  const loadAnnotations = async () => {
    try {
      const data = await annotationService.getByDocument(document._id);
      setAnnotations(data);
    } catch (error) {
      console.error('Error loading annotations:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddNote = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newNote.trim()) return;

    try {
      await annotationService.create({
        documentId: document._id,
        content: newNote,
        type: 'note'
      });
      setNewNote('');
      loadAnnotations();
    } catch (error) {
      console.error('Error adding note:', error);
    }
  };

  return (
    <div>
      <div className="card">
        <h2>{document.title}</h2>
        <p style={{ color: '#6B7280', marginBottom: '20px' }}>{document.description}</p>
        <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <span className={`badge badge-${document.accessLevel}`}>{document.accessLevel}</span>
          <span className="badge" style={{ background: '#F3F4F6', color: '#374151' }}>{document.category}</span>
        </div>
        <div style={{ padding: '20px', background: '#F9FAFB', borderRadius: '8px', marginBottom: '20px' }}>
          <p><strong>File:</strong> {document.fileName}</p>
          <p><strong>Size:</strong> {(document.fileSize / 1024).toFixed(2)} KB</p>
          <p><strong>Views:</strong> {document.viewCount}</p>
          <p><strong>Uploaded:</strong> {new Date(document.createdAt).toLocaleString()}</p>
        </div>
        <a href={`http://localhost:5000${document.fileUrl}`} target="_blank" rel="noopener noreferrer" className="btn btn-primary">
          View Document
        </a>
      </div>

      {!readOnly && (
        <div className="card">
          <h3>Annotations & Notes</h3>
          <form onSubmit={handleAddNote} style={{ marginBottom: '20px' }}>
            <div className="form-group">
              <textarea className="input" rows={3} value={newNote} onChange={(e) => setNewNote(e.target.value)} placeholder="Add your notes here..." />
            </div>
            <button type="submit" className="btn btn-primary">Add Note</button>
          </form>
          {loading ? (
            <div className="loading"><div className="spinner"></div></div>
          ) : annotations.length > 0 ? (
            annotations.map((ann) => (
              <div key={ann._id} className="card" style={{ marginBottom: '10px', background: '#FFFBEB', border: '1px solid #FCD34D' }}>
                <p>{ann.content}</p>
                <small style={{ color: '#92400E' }}>
                  {ann.userId?.name} - {new Date(ann.createdAt).toLocaleDateString()}
                </small>
              </div>
            ))
          ) : (
            <p style={{ color: '#6B7280' }}>No annotations yet.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default DocumentViewer;
