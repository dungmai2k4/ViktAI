import { useEffect, useMemo, useState } from 'react';
import { createConversation, deleteConversation, getConversation, getConversations, sendMessage } from '../api/conversationApi.js';
import ChatWindow from '../components/ChatWindow.jsx';
import NewChatForm from '../components/NewChatForm.jsx';
import AppLayout from '../layouts/AppLayout.jsx';

export default function ChatPage() {
  const [conversations, setConversations] = useState([]);
  const [activeConversation, setActiveConversation] = useState(null);
  const [isNewChat, setIsNewChat] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    refreshConversations();
  }, []);

  const sidebarProps = useMemo(() => ({
    conversations,
    activeId: activeConversation?.id,
    onNewChat: () => {
      setIsNewChat(true);
      setActiveConversation(null);
      setError('');
    },
    onSelect: handleSelectConversation,
    onDelete: handleDeleteConversation,
  }), [conversations, activeConversation]);

  async function refreshConversations() {
    try {
      const data = await getConversations();
      setConversations(data);
    } catch (err) {
      setError(readError(err));
    }
  }

  async function handleSelectConversation(id) {
    setLoading(true);
    setError('');
    try {
      const data = await getConversation(id);
      setActiveConversation(data);
      setIsNewChat(false);
    } catch (err) {
      setError(readError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateConversation(payload) {
    setLoading(true);
    setError('');
    try {
      const data = await createConversation(payload);
      setActiveConversation(data);
      setIsNewChat(false);
      await refreshConversations();
    } catch (err) {
      setError(readError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleSendMessage(message) {
    if (!activeConversation) return;
    setLoading(true);
    setError('');
    try {
      const data = await sendMessage(activeConversation.id, message);
      setActiveConversation(data);
      await refreshConversations();
    } catch (err) {
      setError(readError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleDeleteConversation(id) {
    setLoading(true);
    setError('');
    try {
      await deleteConversation(id);
      if (activeConversation?.id === id) {
        setActiveConversation(null);
        setIsNewChat(true);
      }
      await refreshConversations();
    } catch (err) {
      setError(readError(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <AppLayout sidebarProps={sidebarProps}>
      <div className="h-full">
        {error && (
          <div className="absolute left-1/2 top-4 z-10 -translate-x-1/2 rounded-xl bg-red-50 px-4 py-3 text-sm font-medium text-red-700 shadow-lg ring-1 ring-red-200">
            {error}
          </div>
        )}
        {isNewChat ? (
          <NewChatForm onSubmit={handleCreateConversation} loading={loading} />
        ) : (
          <ChatWindow conversation={activeConversation} onSendMessage={handleSendMessage} loading={loading} />
        )}
      </div>
    </AppLayout>
  );
}

function readError(error) {
  return error?.response?.data?.message || error?.message || 'Có lỗi xảy ra';
}
