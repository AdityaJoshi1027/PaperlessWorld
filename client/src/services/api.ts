import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Auth services
export const authService = {
  login: async (email: string, password: string) => {
    console.log('🔐 Login attempt:', { email, apiUrl: API_URL });
    try {
      const response = await api.post('/auth/login', { email, password });
      console.log('✅ Login response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('❌ Login error:', {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message,
        url: error.config?.url,
        baseURL: error.config?.baseURL
      });
      throw error;
    }
  },
  
  register: async (email: string, password: string, name: string, role: string, organization?: string) => {
    console.log('📝 Register attempt:', { email, apiUrl: API_URL });
    try {
      const response = await api.post('/auth/register', { email, password, name, role, organization });
      console.log('✅ Register response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('❌ Register error:', {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message
      });
      throw error;
    }
  }
};

// Document services
export const documentService = {
  getAll: async (params?: any) => {
    const response = await api.get('/documents', { params });
    return response.data;
  },

  getById: async (id: string) => {
    const response = await api.get(`/documents/${id}`);
    return response.data;
  },

  create: async (formData: FormData) => {
    const response = await api.post('/documents', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  },

  update: async (id: string, data: any) => {
    const response = await api.put(`/documents/${id}`, data);
    return response.data;
  },

  delete: async (id: string) => {
    const response = await api.delete(`/documents/${id}`);
    return response.data;
  },

  getStats: async () => {
    const response = await api.get('/documents/stats/overview');
    return response.data;
  }
};

// User services
export const userService = {
  getAll: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  getById: async (id: string) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  updateAccess: async (id: string, data: any) => {
    const response = await api.put(`/users/${id}/access`, data);
    return response.data;
  },

  update: async (id: string, data: any) => {
    const response = await api.put(`/users/${id}`, data);
    return response.data;
  },

  getStats: async () => {
    const response = await api.get('/users/stats/overview');
    return response.data;
  }
};

// Annotation services
export const annotationService = {
  getByDocument: async (documentId: string) => {
    const response = await api.get(`/annotations/${documentId}`);
    return response.data;
  },

  create: async (data: any) => {
    const response = await api.post('/annotations', data);
    return response.data;
  },

  update: async (id: string, data: any) => {
    const response = await api.put(`/annotations/${id}`, data);
    return response.data;
  },

  delete: async (id: string) => {
    const response = await api.delete(`/annotations/${id}`);
    return response.data;
  }
};

// Feedback services
export const feedbackService = {
  submit: async (data: any) => {
    const response = await api.post('/feedback', data);
    return response.data;
  },

  getAll: async (params?: any) => {
    const response = await api.get('/feedback', { params });
    return response.data;
  },

  update: async (id: string, data: any) => {
    const response = await api.put(`/feedback/${id}`, data);
    return response.data;
  },

  delete: async (id: string) => {
    const response = await api.delete(`/feedback/${id}`);
    return response.data;
  }
};

export default api;
