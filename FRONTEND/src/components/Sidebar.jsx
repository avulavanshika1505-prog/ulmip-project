// ============================================================
// Sidebar Component
// ============================================================
import { useState, useEffect } from "react";
import { api } from "../services/api";

const NAV_ITEMS = [
  {
    section: "Overview",
    items: [{ id: "dashboard", icon: "⊞", label: "Dashboard" }]
  },
  {
    section: "Productivity",
    items: [
      { id: "tasks", icon: "✓", label: "Tasks" },
      { id: "goals", icon: "◎", label: "Goals" },
    ]
  },
  {
    section: "Well-Being",
    items: [
      { id: "health", icon: "♡", label: "Health" },
      { id: "finance", icon: "₹", label: "Finance" },
    ]
  },
  {
    section: "Growth",
    items: [
      { id: "learning", icon: "◈", label: "Learning" },
      { id: "suggestions", icon: "◆", label: "AI Insights" },
    ]
  }
];

export default function Sidebar({ activePage, setActivePage, isOpen, onToggle }) {
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    api.getSuggestionCount().then(d => setUnreadCount(d.unread)).catch(() => {});
  }, []);

  return (
    <nav className={`sidebar ${isOpen ? "" : "collapsed"}`}>
      <div className="sidebar-logo">
        <div className="logo-icon">⬡</div>
        {isOpen && <div className="logo-text">ULM<span>IP</span></div>}
      </div>

      <div className="sidebar-nav">
        {NAV_ITEMS.map(section => (
          <div className="nav-section" key={section.section}>
            {isOpen && <div className="nav-section-label">{section.section}</div>}
            {section.items.map(item => (
              <div
                key={item.id}
                className={`nav-item ${activePage === item.id ? "active" : ""}`}
                onClick={() => setActivePage(item.id)}
                title={!isOpen ? item.label : ""}
              >
                <span className="nav-icon">{item.icon}</span>
                {isOpen && <span className="nav-label">{item.label}</span>}
                {isOpen && item.id === "suggestions" && unreadCount > 0 && (
                  <span className="nav-badge">{unreadCount}</span>
                )}
              </div>
            ))}
          </div>
        ))}
      </div>
    </nav>
  );
}
