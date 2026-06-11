import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
});

export async function getConversations() {
  const { data } = await api.get('/conversations');
  return data;
}

export async function getConversation(id) {
  const { data } = await api.get(`/conversations/${id}`);
  return data;
}

export async function createConversation({ style, designType, description, files }) {
  const formData = new FormData();
  formData.append('style', style);
  formData.append('designType', designType);
  formData.append('description', description || '');
  files.forEach((file) => formData.append('files', file));

  const { data } = await api.post('/conversations', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return data;
}

export async function sendMessage(conversationId, message) {
  const { data } = await api.post(`/conversations/${conversationId}/messages`, { message });
  return data;
}

export async function deleteConversation(id) {
  await api.delete(`/conversations/${id}`);
}
