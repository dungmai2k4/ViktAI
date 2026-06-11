export default function MessageList({ messages }) {
  return (
    <div className="space-y-4">
      {messages.map((message) => {
        const isUser = message.role === 'USER';
        return (
          <div key={message.id || `${message.role}-${message.createdAt}`} className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[82%] rounded-2xl px-4 py-3 text-sm leading-6 ${isUser ? 'bg-slate-950 text-white' : 'bg-white text-slate-800 shadow-sm ring-1 ring-slate-200'}`}>
              <p className="mb-1 text-xs font-semibold opacity-70">{isUser ? 'Bạn' : 'ViktAI'}</p>
              <p className="whitespace-pre-wrap">{message.content}</p>
            </div>
          </div>
        );
      })}
    </div>
  );
}
