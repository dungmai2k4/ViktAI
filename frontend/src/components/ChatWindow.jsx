import ChatInput from './ChatInput.jsx';
import ImageViewer from './ImageViewer.jsx';
import MessageList from './MessageList.jsx';

export default function ChatWindow({ conversation, onSendMessage, loading }) {
  if (!conversation) {
    return null;
  }

  return (
    <div className="flex h-full flex-col bg-slate-100">
      <header className="border-b border-slate-200 bg-white px-6 py-4">
        <p className="text-sm font-semibold uppercase tracking-wide text-emerald-600">{readableDesignType(conversation.designType)}</p>
        <h1 className="text-2xl font-bold text-slate-950">Thiết kế phong cách {conversation.style}</h1>
        <p className="text-sm text-slate-500">{conversation.description || 'Không có mô tả ban đầu'}</p>
      </header>

      <main className="chat-scrollbar flex-1 overflow-y-auto p-4 md:p-8">
        <div className="mx-auto max-w-4xl space-y-8">
          <ImageViewer imageUrl={conversation.currentImageUrl} description={conversation.currentDescription} />
          <MessageList messages={conversation.messages || []} />
          {loading && <p className="text-center text-sm text-slate-500">ViktAI đang cập nhật thiết kế...</p>}
        </div>
      </main>

      <ChatInput onSend={onSendMessage} disabled={loading} />
    </div>
  );
}

function readableDesignType(designType) {
  if (designType === 'FOUR_WALLS') return '4 mặt căn phòng';
  if (designType === 'FLOOR_PLAN') return '1 sơ đồ mặt bằng';
  return designType;
}
