import { useState, useEffect } from "react";

export default function Header({ user, onLogout, onMenuToggle }) {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const timeStr = time.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", second: "2-digit" });
  const dateStr = time.toLocaleDateString("en-IN", { weekday: "short", month: "short", day: "numeric" });

  return (
    <header className="header">
      <button className="header-menu-btn" onClick={onMenuToggle}>☰</button>
      <div className="header-title">Unified Life Management Intelligence Platform</div>
      <div className="header-time">{dateStr} · {timeStr}</div>
      <div className="header-user">
        <div className="user-avatar">{user?.name?.[0] || "U"}</div>
        <span className="user-name">{user?.name}</span>
      </div>
      <button className="logout-btn" onClick={onLogout}>Sign Out</button>
    </header>
  );
}
