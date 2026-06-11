export default function ImageViewer({ imageUrl, description }) {
  if (!imageUrl) {
    return (
      <div className="flex min-h-80 items-center justify-center rounded-3xl border border-dashed border-slate-300 bg-white text-slate-500">
        Ảnh render sẽ hiển thị tại đây.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-lg shadow-slate-200/70">
      <img src={imageUrl} alt="Kết quả thiết kế nội thất" className="h-[420px] w-full object-cover" />
      <div className="border-t border-slate-200 p-5">
        <h2 className="mb-2 text-lg font-bold text-slate-950">Mô tả kết quả</h2>
        <p className="leading-7 text-slate-700">{description}</p>
      </div>
    </div>
  );
}
