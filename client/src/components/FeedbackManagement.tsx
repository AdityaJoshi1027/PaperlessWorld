import React, { useState, useEffect } from 'react';
import { feedbackService } from '../services/api';

const FeedbackManagement: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadFeedbacks();
  }, []);

  const loadFeedbacks = async () => {
    try {
      const data = await feedbackService.getAll();
      setFeedbacks(data);
    } catch (error) {
      console.error('Error loading feedbacks:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id: string, status: string) => {
    try {
      await feedbackService.update(id, { status });
      loadFeedbacks();
    } catch (error) {
      console.error('Error updating feedback:', error);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="card">
      {feedbacks.map((feedback) => (
        <div key={feedback._id} className="card" style={{ marginBottom: '15px', background: '#F9FAFB' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div style={{ flex: 1 }}>
              <h4>{feedback.subject}</h4>
              <p style={{ color: '#6B7280', fontSize: '14px' }}>{feedback.message}</p>
              <div style={{ marginTop: '8px' }}>
                <span style={{ fontSize: '12px', color: '#9CA3AF' }}>From: {feedback.name} ({feedback.email})</span>
                <span style={{ marginLeft: '15px', fontSize: '12px', color: '#9CA3AF' }}>
                  {new Date(feedback.createdAt).toLocaleDateString()}
                </span>
              </div>
            </div>
            <div>
              <span className={`badge badge-${feedback.status}`}>{feedback.status}</span>
              {feedback.status === 'new' && (
                <button className="btn btn-primary" style={{ marginLeft: '10px', padding: '4px 12px', fontSize: '12px' }} onClick={() => updateStatus(feedback._id, 'reviewed')}>
                  Mark Reviewed
                </button>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default FeedbackManagement;
