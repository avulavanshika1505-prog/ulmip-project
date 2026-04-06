import { useState, useEffect } from "react";
import { api } from "../services/api";
import { useAuth } from "../App";

const StatCard = ({ icon, label, value, color, sub }) => (
  <div className="stat-card">
    <div className="stat-icon" style={{ background: color + "22" }}>{icon}</div>
    <div className="stat-info">
      <div className="stat-value" style={{ color }}>{value ?? "—"}</div>
      <div className="stat-label">{label}</div>
      {sub && <div className="stat-change">{sub}</div>}
    </div>
  </div>
);

export default function Dashboard() {
  const { auth } = useAuth();
  const [data, setData] = useState(null);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.getDashboardOverview(),
      api.getSuggestions(),
    ]).then(([overview, sugg]) => {
      setData(overview);
      setSuggestions(sugg.slice(0, 3));
    }).catch(console.error).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading"><div className="spinner"/><span>Loading your dashboard...</span></div>;

  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return "Good Morning";
    if (h < 18) return "Good Afternoon";
    return "Good Evening";
  };

  const taskTotal = (data?.tasksTodo || 0) + (data?.tasksInProgress || 0) + (data?.tasksCompleted || 0);
  const completionRate = taskTotal > 0 ? Math.round((data?.tasksCompleted / taskTotal) * 100) : 0;

  const suggestionColors = {
    STRESS: "var(--red)",
    HEALTH: "var(--green)",
    PRODUCTIVITY: "var(--accent)",
    FINANCE: "var(--yellow)",
    LEARNING: "var(--blue)",
    SCHEDULE: "var(--purple)",
  };

  return (
    <div>
      {/* Greeting */}
      <div style={{ marginBottom: 28 }}>
        <h1 className="page-title">{greeting()}, {auth.user.name.split(" ")[0]} 👋</h1>
        <p className="page-subtitle">Here's your life overview for {new Date().toLocaleDateString("en-IN", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}</p>
      </div>

      {/* Primary stats */}
      <div className="grid-4 mb-24">
        <StatCard icon="✓" label="Tasks Completed" value={data?.tasksCompleted} color="var(--green)"
          sub={`${data?.tasksInProgress || 0} in progress`} />
        <StatCard icon="♡" label="Mood Today" value={data?.todayMood ? `${data.todayMood}/10` : "Not logged"} color="var(--pink)"
          sub={data?.todayStress ? `Stress: ${data.todayStress}/10` : ""} />
        <StatCard icon="₹" label="Monthly Balance" value={data?.monthlyBalance ? `₹${Number(data.monthlyBalance).toLocaleString("en-IN")}` : "₹0"} color="var(--accent)"
          sub={`Income: ₹${Number(data?.monthlyIncome || 0).toLocaleString("en-IN")}`} />
        <StatCard icon="◈" label="Learning Hours" value={data?.totalLearningHours ? `${Number(data.totalLearningHours).toFixed(1)}h` : "0h"} color="var(--blue)"
          sub={`${data?.activeGoals || 0} active goals`} />
      </div>

      <div className="grid-2">
        {/* Task progress */}
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Task Overview</div>
              <div className="card-subtitle">{completionRate}% completion rate</div>
            </div>
            <span className="badge badge-accent">{taskTotal} total</span>
          </div>

          {[
            { label: "To Do", count: data?.tasksTodo, color: "var(--yellow)" },
            { label: "In Progress", count: data?.tasksInProgress, color: "var(--accent)" },
            { label: "Completed", count: data?.tasksCompleted, color: "var(--green)" },
          ].map(item => (
            <div key={item.label} style={{ marginBottom: 14 }}>
              <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
                <span style={{ color: "var(--text-secondary)" }}>{item.label}</span>
                <span className="font-mono" style={{ color: item.color }}>{item.count || 0}</span>
              </div>
              <div className="progress-bar">
                <div className="progress-fill" style={{
                  width: taskTotal > 0 ? `${((item.count || 0) / taskTotal) * 100}%` : "0%",
                  background: item.color
                }} />
              </div>
            </div>
          ))}
        </div>

        {/* Health snapshot */}
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Health Snapshot</div>
              <div className="card-subtitle">Today's metrics</div>
            </div>
          </div>

          {data?.todayMood ? (
            <div>
              {[
                { label: "Mood", value: data.todayMood, max: 10, color: "var(--pink)", unit: "/10" },
                { label: "Energy", value: data.todayEnergy || 0, max: 10, color: "var(--yellow)", unit: "/10" },
                { label: "Steps", value: data.todaySteps || 0, max: 10000, color: "var(--green)", unit: "" },
                { label: "Water", value: data.todayWater || 0, max: 2000, color: "var(--blue)", unit: "ml" },
              ].map(m => (
                <div key={m.label} style={{ marginBottom: 14 }}>
                  <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
                    <span style={{ color: "var(--text-secondary)" }}>{m.label}</span>
                    <span className="font-mono" style={{ color: m.color }}>{m.value}{m.unit}</span>
                  </div>
                  <div className="progress-bar">
                    <div className="progress-fill" style={{ width: `${Math.min((m.value / m.max) * 100, 100)}%`, background: m.color }} />
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state" style={{ padding: 24 }}>
              <div className="empty-state-icon">♡</div>
              <div className="empty-state-text">No health data logged today</div>
            </div>
          )}
        </div>
      </div>

      {/* AI Suggestions */}
      {suggestions.length > 0 && (
        <div className="card" style={{ marginTop: 16 }}>
          <div className="card-header">
            <div className="card-title">◆ AI Insights</div>
            <span className="badge badge-accent">{data?.unreadSuggestions || 0} new</span>
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {suggestions.map(s => (
              <div key={s.id} style={{
                padding: "14px 16px",
                background: "var(--bg-hover)",
                borderRadius: "var(--radius-sm)",
                borderLeft: `3px solid ${suggestionColors[s.suggestionType] || "var(--accent)"}`,
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6 }}>
                  <span style={{ fontSize: 13, fontWeight: 600 }}>{s.title}</span>
                  <span className="badge badge-muted" style={{ fontSize: 10 }}>{s.suggestionType}</span>
                </div>
                <div style={{ fontSize: 12, color: "var(--text-secondary)", lineHeight: 1.6 }}>{s.content}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
