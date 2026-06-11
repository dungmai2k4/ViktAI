export default function MessageList({ messages }) {
  return (
    <div className="space-y-4">
      {messages.map((message) => {
        const isUser = message.role === 'USER';
        return (
          <div key={message.id || `${message.role}-${message.createdAt}`} className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[82%] rounded-2xl px-4 py-3 text-sm leading-6 ${isUser ? 'bg-slate-950 text-white' : 'bg-white text-slate-800 shadow-sm ring-1 ring-slate-200'}`}>
              <p className="mb-1 text-xs font-semibold opacity-70">{isUser ? 'Bạn' : 'ViktAI'}</p>
              {message.imageUrls?.length > 0 && (
                <div className="mb-3 grid grid-cols-2 gap-2">
                  {message.imageUrls.map((imageUrl, index) => (
                    <img
                      key={`${message.id || message.createdAt}-image-${index}`}
                      src={imageUrl}
                      alt={`Ảnh tham chiếu ${index + 1}`}
                      className="h-28 w-full rounded-xl object-cover ring-1 ring-black/10"
                    />
                  ))}
                </div>
              )}
              <p className="whitespace-pre-wrap">{message.content}</p>
            </div>
          </div>
        );
      })}
    </div>
  );
}
