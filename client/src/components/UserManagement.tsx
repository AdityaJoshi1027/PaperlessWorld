import React, { useState, useEffect } from 'react';
import { userService } from '../services/api';

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const data = await userService.getAll();
      setUsers(data);
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateUserAccess = async (userId: string, status: string) => {
    try {
      await userService.updateAccess(userId, { status });
      loadUsers();
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div className="card">
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ borderBottom: '2px solid #E5E7EB', textAlign: 'left' }}>
            <th style={{ padding: '12px' }}>Name</th>
            <th style={{ padding: '12px' }}>Email</th>
            <th style={{ padding: '12px' }}>Role</th>
            <th style={{ padding: '12px' }}>Status</th>
            <th style={{ padding: '12px' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user._id} style={{ borderBottom: '1px solid #E5E7EB' }}>
              <td style={{ padding: '12px' }}>{user.name}</td>
              <td style={{ padding: '12px' }}>{user.email}</td>
              <td style={{ padding: '12px' }}><span className={`badge badge-${user.role}`}>{user.role}</span></td>
              <td style={{ padding: '12px' }}><span className={`badge badge-${user.status}`}>{user.status}</span></td>
              <td style={{ padding: '12px' }}>
                {user.status === 'pending' && (
                  <button className="btn btn-success" style={{ padding: '4px 12px', fontSize: '12px' }} onClick={() => updateUserAccess(user._id, 'active')}>
                    Approve
                  </button>
                )}
                {user.status === 'active' && (
                  <button className="btn btn-danger" style={{ padding: '4px 12px', fontSize: '12px' }} onClick={() => updateUserAccess(user._id, 'suspended')}>
                    Suspend
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserManagement;
