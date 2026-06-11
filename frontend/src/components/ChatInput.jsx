import { SendHorizontal } from 'lucide-react';
import { useState } from 'react';

export default function ChatInput({ onSend, disabled }) {
  const [message, setMessage] = useState('');

  const submit = (event) => {
    event.preventDefault();
    if (!message.trim()) return;
    onSend(message.trim());
    setMessage('');
  };

  return (
    <form onSubmit={submit} className="border-t border-slate-200 bg-white p-4">
      <div className="mx-auto flex max-w-4xl items-end gap-3 rounded-2xl border border-slate-300 bg-slate-50 p-2 focus-within:border-emerald-500">
        <textarea
          className="max-h-32 min-h-12 flex-1 resize-none bg-transparent px-3 py-3 outline-none"
          placeholder="Nhập yêu cầu chỉnh sửa, ví dụ: Đổi sofa thành màu nâu..."
          value={message}
          disabled={disabled}
          onChange={(event) => setMessage(event.target.value)}
        />
        <button disabled={disabled || !message.trim()} className="mb-1 rounded-xl bg-emerald-500 p-3 text-white transition hover:bg-emerald-400 disabled:opacity-50">
          <SendHorizontal size={20} />
        </button>
      </div>
    </form>
  );
}
