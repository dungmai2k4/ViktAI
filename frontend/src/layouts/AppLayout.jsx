import Sidebar from '../components/Sidebar.jsx';

export default function AppLayout({ children, sidebarProps }) {
  return (
    <div className="grid h-screen grid-cols-1 overflow-hidden md:grid-cols-[20rem_1fr]">
      <div className="hidden md:block">
        <Sidebar {...sidebarProps} />
      </div>
      <div className="min-h-0">{children}</div>
    </div>
  );
}
