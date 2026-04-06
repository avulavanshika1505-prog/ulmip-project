import { useState, useEffect } from "react";
import { api } from "../services/api";

const PRIORITIES = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];
const STATUSES = ["ALL", "TODO", "IN_PROGRESS", "COMPLETED", "CANCELLED"];
const PRIORITY_COLORS = { LOW: "var(--text-muted)", MEDIUM: "var(--blue)", HIGH: "var(--yellow)", CRITICAL: "var(--red)" };
const STATUS_BADGES = {
  TODO: "badge-muted",
  IN_PROGRESS: "badge-accent",
  COMPLETED: "badge-green",
  CANCELLED: "badge-red",
};

export default function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [showModal, setShowModal] = useState(false);
  const [editTask, setEditTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ title: "", description: "", priority: "MEDIUM", status: "TODO", category: "", dueDate: "", estimatedMinutes: 30 });

  const load = () => api.getTasks().then(setTasks).catch(console.error).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditTask(null); setForm({ title: "", description: "", priority: "MEDIUM", status: "TODO", category: "", dueDate: "", estimatedMinutes: 30 }); setShowModal(true); };
  const openEdit = (t) => { setEditTask(t); setForm({ title: t.title, description: t.description || "", priority: t.priority, status: t.status, category: t.category || "", dueDate: t.dueDate ? t.dueDate.substring(0, 16) : "", estimatedMinutes: t.estimatedMinutes || 30 }); setShowModal(true); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { ...form, dueDate: form.dueDate || null };
    if (editTask) await api.updateTask(editTask.id, payload);
    else await api.createTask(payload);
    setShowModal(false);
    load();
  };

  const handleDelete = async (id) => {
    if (!confirm("Delete this task?")) return;
    await api.deleteTask(id);
    load();
  };

  const toggleStatus = async (task) => {
    const next = task.status === "TODO" ? "IN_PROGRESS" : task.status === "IN_PROGRESS" ? "COMPLETED" : "TODO";
    await api.updateTaskStatus(task.id, next);
    load();
  };

  const filtered = filter === "ALL" ? tasks : tasks.filter(t => t.status === filter);

  if (loading) return <div className="loading"><div className="spinner"/><span>Loading tasks...</span></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Tasks</h1>
          <p className="page-subtitle">{tasks.length} tasks · Sorted by priority score</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>+ New Task</button>
      </div>

      <div className="filter-chips">
        {STATUSES.map(s => (
          <div key={s} className={`chip ${filter === s ? "active" : ""}`} onClick={() => setFilter(s)}>
            {s === "ALL" ? "All" : s.replace("_", " ")} {s !== "ALL" && `(${tasks.filter(t => t.status === s).length})`}
          </div>
        ))}
      </div>

      {filtered.length === 0 ? (
        <div className="empty-state"><div className="empty-state-icon">✓</div><div className="empty-state-text">No tasks found</div></div>
      ) : (
        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
          {filtered.map(task => (
            <div key={task.id} className="card" style={{ padding: "14px 18px", cursor: "pointer" }}>
              <div className="flex items-center gap-12">
                <div
                  onClick={() => toggleStatus(task)}
                  style={{
                    width: 20, height: 20, borderRadius: 4, flexShrink: 0, cursor: "pointer",
                    border: `2px solid ${task.status === "COMPLETED" ? "var(--green)" : "var(--border)"}`,
                    background: task.status === "COMPLETED" ? "var(--green)" : "transparent",
                    display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, color: "white"
                  }}
                >
                  {task.status === "COMPLETED" && "✓"}
                </div>

                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 4 }}>
                    <span style={{ fontSize: 14, fontWeight: 500, textDecoration: task.status === "COMPLETED" ? "line-through" : "none", color: task.status === "COMPLETED" ? "var(--text-muted)" : "var(--text-primary)" }}>
                      {task.title}
                    </span>
                    <span className={`badge ${STATUS_BADGES[task.status]}`}>{task.status.replace("_"," ")}</span>
                  </div>
                  {task.description && <div style={{ fontSize: 12, color: "var(--text-muted)", marginBottom: 6 }}>{task.description}</div>}
                  <div style={{ display: "flex", gap: 12, fontSize: 11, color: "var(--text-muted)" }}>
                    <span style={{ color: PRIORITY_COLORS[task.priority] }}>● {task.priority}</span>
                    {task.category && <span>⬡ {task.category}</span>}
                    {task.dueDate && <span>⏱ {new Date(task.dueDate).toLocaleDateString("en-IN", { month: "short", day: "numeric" })}</span>}
                    {task.estimatedMinutes && <span>◷ {task.estimatedMinutes}m</span>}
                    <span style={{ color: "var(--accent)", fontFamily: "monospace" }}>Score: {task.autoPriorityScore?.toFixed(0)}</span>
                  </div>
                </div>

                <div style={{ display: "flex", gap: 6, flexShrink: 0 }}>
                  <button className="btn btn-sm btn-secondary btn-icon" onClick={() => openEdit(task)}>✎</button>
                  <button className="btn btn-sm btn-danger btn-icon" onClick={() => handleDelete(task.id)}>✕</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">{editTask ? "Edit Task" : "New Task"}</div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Title *</label>
                <input className="form-input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} placeholder="Task title" required />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-textarea" value={form.description} onChange={e => setForm({...form, description: e.target.value})} placeholder="Details..." />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Priority</label>
                  <select className="form-select" value={form.priority} onChange={e => setForm({...form, priority: e.target.value})}>
                    {PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select className="form-select" value={form.status} onChange={e => setForm({...form, status: e.target.value})}>
                    {["TODO", "IN_PROGRESS", "COMPLETED", "CANCELLED"].map(s => <option key={s} value={s}>{s.replace("_"," ")}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <input className="form-input" value={form.category} onChange={e => setForm({...form, category: e.target.value})} placeholder="Academic, Health..." />
                </div>
                <div className="form-group">
                  <label className="form-label">Est. Minutes</label>
                  <input className="form-input" type="number" value={form.estimatedMinutes} onChange={e => setForm({...form, estimatedMinutes: parseInt(e.target.value)})} />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Due Date</label>
                <input className="form-input" type="datetime-local" value={form.dueDate} onChange={e => setForm({...form, dueDate: e.target.value})} />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{editTask ? "Update" : "Create"} Task</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
