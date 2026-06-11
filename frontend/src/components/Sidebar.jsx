import { MessageSquare, Plus, Trash2 } from 'lucide-react';

export default function Sidebar({ conversations, activeId, onNewChat, onSelect, onDelete }) {
  return (
    <aside className="flex h-full w-full flex-col border-r border-slate-200 bg-slate-950 text-white md:w-80">
      <div className="border-b border-white/10 p-4">
        <button
          onClick={onNewChat}
          className="flex w-full items-center justify-center gap-2 rounded-xl bg-emerald-500 px-4 py-3 font-semibold text-white transition hover:bg-emerald-400"
        >
          <Plus size={18} /> New Chat
        </button>
      </div>

      <div className="chat-scrollbar flex-1 space-y-2 overflow-y-auto p-3">
        {conversations.length === 0 && (
          <p className="rounded-xl border border-dashed border-white/20 p-4 text-sm text-slate-300">
            Chưa có cuộc hội thoại. Hãy tạo thiết kế đầu tiên cho không gian Việt Nam của bạn.
          </p>
        )}

        {conversations.map((conversation) => (
          <div
            key={conversation.id}
            className={`group flex items-center gap-2 rounded-xl p-3 transition ${
              activeId === conversation.id ? 'bg-white text-slate-950' : 'hover:bg-white/10'
            }`}
          >
            <button className="flex min-w-0 flex-1 items-center gap-3 text-left" onClick={() => onSelect(conversation.id)}>
              <MessageSquare size={18} className="shrink-0" />
              <span className="truncate text-sm font-medium">{conversation.style} · {readableDesignType(conversation.designType)}</span>
            </button>
            <button
              aria-label="Xóa cuộc hội thoại"
              className="rounded-lg p-1 opacity-70 hover:bg-red-500 hover:text-white"
              onClick={() => onDelete(conversation.id)}
            >
              <Trash2 size={16} />
            </button>
          </div>
        ))}
      </div>
    </aside>
  );
}

function readableDesignType(designType) {
  if (designType === 'FOUR_WALLS') return '4 mặt căn phòng';
  if (designType === 'FLOOR_PLAN') return 'Sơ đồ';
  return designType;
}
