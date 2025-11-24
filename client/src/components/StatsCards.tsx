import React from 'react';

interface StatsCardsProps {
  stats: any;
}

const StatsCards: React.FC<StatsCardsProps> = ({ stats }) => {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '30px' }}>
      <div className="card" style={{ textAlign: 'center' }}>
        <h3>📄 Total Documents</h3>
        <p style={{ fontSize: '32px', fontWeight: 'bold', color: '#4F46E5' }}>{stats.documents?.totalDocuments || 0}</p>
      </div>
      <div className="card" style={{ textAlign: 'center' }}>
        <h3>🌐 Public Documents</h3>
        <p style={{ fontSize: '32px', fontWeight: 'bold', color: '#10B981' }}>{stats.documents?.publicDocuments || 0}</p>
      </div>
      <div className="card" style={{ textAlign: 'center' }}>
        <h3>👥 Total Users</h3>
        <p style={{ fontSize: '32px', fontWeight: 'bold', color: '#F59E0B' }}>{stats.users?.totalUsers || 0}</p>
      </div>
      <div className="card" style={{ textAlign: 'center' }}>
        <h3>👁️ Total Views</h3>
        <p style={{ fontSize: '32px', fontWeight: 'bold', color: '#8B5CF6' }}>{stats.documents?.totalViews || 0}</p>
      </div>
    </div>
  );
};

export default StatsCards;
