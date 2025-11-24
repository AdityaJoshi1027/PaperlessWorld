import React, { useState } from 'react';
import { documentService } from '../services/api';

interface DocumentUploadProps {
  onUploadSuccess: () => void;
}

const DocumentUpload: React.FC<DocumentUploadProps> = ({ onUploadSuccess }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'historical',
    accessLevel: 'public',
    isPublic: true
  });
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      setMessage('Please select a file');
      return;
    }

    const data = new FormData();
    data.append('file', file);
    data.append('title', formData.title);
    data.append('description', formData.description);
    data.append('category', formData.category);
    data.append('accessLevel', formData.accessLevel);
    data.append('isPublic', String(formData.isPublic));

    setUploading(true);
    try {
      await documentService.create(data);
      setMessage('Document uploaded successfully!');
      setFormData({ title: '', description: '', category: 'historical', accessLevel: 'public', isPublic: true });
      setFile(null);
      onUploadSuccess();
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Upload failed');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="card">
      <h3>Upload New Document</h3>
      {message && <div className={`alert ${message.includes('success') ? 'alert-success' : 'alert-error'}`}>{message}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="label">Title</label>
          <input className="input" value={formData.title} onChange={(e) => setFormData({...formData, title: e.target.value})} required />
        </div>
        <div className="form-group">
          <label className="label">Description</label>
          <textarea className="input" value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} required />
        </div>
        <div className="form-group">
          <label className="label">Category</label>
          <select className="input" value={formData.category} onChange={(e) => setFormData({...formData, category: e.target.value})}>
            <option value="historical">Historical</option>
            <option value="legal">Legal</option>
            <option value="research">Research</option>
            <option value="administrative">Administrative</option>
            <option value="cultural">Cultural</option>
            <option value="other">Other</option>
          </select>
        </div>
        <div className="form-group">
          <label className="label">File</label>
          <input type="file" className="input" onChange={(e) => setFile(e.target.files?.[0] || null)} required />
        </div>
        <button type="submit" className="btn btn-primary" disabled={uploading}>
          {uploading ? 'Uploading...' : 'Upload Document'}
        </button>
      </form>
    </div>
  );
};

export default DocumentUpload;
