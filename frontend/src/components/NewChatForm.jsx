import { useMemo, useState } from 'react';
import { ImagePlus, Sparkles } from 'lucide-react';

const styles = ['Cổ điển', 'Tân cổ điển', 'Cách tân', 'Đông Dương'];
const wallInputs = [
  { key: 'front', label: 'Ảnh mặt trước' },
  { key: 'left', label: 'Ảnh mặt trái' },
  { key: 'right', label: 'Ảnh mặt phải' },
  { key: 'back', label: 'Ảnh mặt sau' },
];

export default function NewChatForm({ onSubmit, loading }) {
  const [designType, setDesignType] = useState('FOUR_WALLS');
  const [style, setStyle] = useState('Đông Dương');
  const [description, setDescription] = useState('');
  const [files, setFiles] = useState({});

  const requiredInputs = useMemo(() => (designType === 'FOUR_WALLS' ? wallInputs : [{ key: 'floorPlan', label: 'Ảnh sơ đồ mặt bằng' }]), [designType]);

  const handleSubmit = (event) => {
    event.preventDefault();
    const uploadFiles = requiredInputs.map((input) => files[input.key]).filter(Boolean);
    onSubmit({ style, designType, description, files: uploadFiles });
  };

  return (
    <section className="mx-auto flex h-full max-w-4xl flex-col justify-center px-4 py-10">
      <div className="mb-8 text-center">
        <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-emerald-100 text-emerald-700">
          <Sparkles size={28} />
        </div>
        <h1 className="text-3xl font-bold text-slate-950 md:text-5xl">ViktAI Interior Studio</h1>
        <p className="mt-3 text-slate-600">Tạo render nội thất phong cách Việt Nam từ ảnh căn phòng hoặc sơ đồ mặt bằng.</p>
      </div>

      <form onSubmit={handleSubmit} className="rounded-3xl border border-slate-200 bg-white p-6 shadow-xl shadow-slate-200/60">
        <div className="grid gap-5 md:grid-cols-2">
          <label className="space-y-2">
            <span className="text-sm font-semibold text-slate-700">Loại đầu vào</span>
            <select className="w-full rounded-xl border border-slate-300 px-4 py-3" value={designType} onChange={(event) => { setDesignType(event.target.value); setFiles({}); }}>
              <option value="FOUR_WALLS">4 mặt căn phòng</option>
              <option value="FLOOR_PLAN">1 sơ đồ mặt bằng</option>
            </select>
          </label>

          <label className="space-y-2">
            <span className="text-sm font-semibold text-slate-700">Phong cách</span>
            <select className="w-full rounded-xl border border-slate-300 px-4 py-3" value={style} onChange={(event) => setStyle(event.target.value)}>
              {styles.map((item) => <option key={item}>{item}</option>)}
            </select>
          </label>
        </div>

        <div className="mt-5 grid gap-4 md:grid-cols-2">
          {requiredInputs.map((input) => (
            <label key={input.key} className="flex cursor-pointer flex-col items-center justify-center rounded-2xl border-2 border-dashed border-slate-300 bg-slate-50 p-5 text-center transition hover:border-emerald-400 hover:bg-emerald-50">
              <ImagePlus className="mb-2 text-emerald-600" />
              <span className="font-medium text-slate-700">{input.label}</span>
              <span className="mt-1 max-w-full truncate text-xs text-slate-500">{files[input.key]?.name || 'Chọn ảnh JPG/PNG'}</span>
              <input className="hidden" type="file" accept="image/*" required onChange={(event) => setFiles((current) => ({ ...current, [input.key]: event.target.files[0] }))} />
            </label>
          ))}
        </div>

        <label className="mt-5 block space-y-2">
          <span className="text-sm font-semibold text-slate-700">Mô tả</span>
          <textarea
            className="min-h-28 w-full rounded-xl border border-slate-300 px-4 py-3"
            placeholder="Ví dụ: Phòng khách sang trọng, nhiều ánh sáng tự nhiên."
            value={description}
            onChange={(event) => setDescription(event.target.value)}
          />
        </label>

        <button disabled={loading} className="mt-6 w-full rounded-xl bg-slate-950 px-5 py-3 font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60">
          {loading ? 'Đang tạo thiết kế...' : 'Tạo thiết kế'}
        </button>
      </form>
    </section>
  );
}
