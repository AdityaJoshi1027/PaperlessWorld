import React, { useState } from 'react';
import { feedbackService } from '../services/api';

const FeedbackForm: React.FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    message: '',
    category: 'suggestion'
  });
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await feedbackService.submit(formData);
      setMessage('Feedback submitted successfully! Thank you.');
      setFormData({ name: '', email: '', subject: '', message: '', category: 'suggestion' });
    } catch (error) {
      setMessage('Failed to submit feedback. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="card">
      {message && <div className={`alert ${message.includes('success') ? 'alert-success' : 'alert-error'}`}>{message}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="label">Name</label>
          <input className="input" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} required />
        </div>
        <div className="form-group">
          <label className="label">Email</label>
          <input type="email" className="input" value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} required />
        </div>
        <div className="form-group">
          <label className="label">Category</label>
          <select className="input" value={formData.category} onChange={(e) => setFormData({...formData, category: e.target.value})}>
            <option value="suggestion">Suggestion</option>
            <option value="issue">Issue</option>
            <option value="compliment">Compliment</option>
            <option value="question">Question</option>
            <option value="other">Other</option>
          </select>
        </div>
        <div className="form-group">
          <label className="label">Subject</label>
          <input className="input" value={formData.subject} onChange={(e) => setFormData({...formData, subject: e.target.value})} required />
        </div>
        <div className="form-group">
          <label className="label">Message</label>
          <textarea className="input" rows={5} value={formData.message} onChange={(e) => setFormData({...formData, message: e.target.value})} required />
        </div>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Submitting...' : 'Submit Feedback'}
        </button>
      </form>
    </div>
  );
};

export default FeedbackForm;
