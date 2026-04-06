import { useState, useEffect } from "react";
import { api } from "../services/api";

// ============================================================
// HEALTH PAGE
// ============================================================
export function Health() {
  const [logs, setLogs] = useState([]);
  const [today, setToday] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ sleepHours: 7, sleepQuality: 7, waterMl: 0, steps: 0, exerciseMinutes: 0, exerciseType: "", mood: 7, energyLevel: 7, stressLevel: 5, caloriesIntake: 0, notes: "" });

  const load = async () => {
    const [l, t] = await Promise.all([api.getHealthLogs(), api.getTodayHealth()]);
    setLogs(l);
    if (t) { setToday(t); setForm({ sleepHours: Number(t.sleepHours) || 7, sleepQuality: t.sleepQuality || 7, waterMl: t.waterMl || 0, steps: t.steps || 0, exerciseMinutes: t.exerciseMinutes || 0, exerciseType: t.exerciseType || "", mood: t.mood || 7, energyLevel: t.energyLevel || 7, stressLevel: t.stressLevel || 5, caloriesIntake: t.caloriesIntake || 0, notes: t.notes || "" }); }
    setLoading(false);
  };
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.logHealth(form);
    setShowModal(false);
    load();
  };

  const Metric = ({ label, value, max, color, unit = "" }) => (
    <div style={{ marginBottom: 16 }}>
      <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
        <span style={{ color: "var(--text-secondary)" }}>{label}</span>
        <span style={{ color, fontFamily: "monospace" }}>{value}{unit}</span>
      </div>
      <div className="progress-bar">
        <div className="progress-fill" style={{ width: `${Math.min((value / max) * 100, 100)}%`, background: color }} />
      </div>
    </div>
  );

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Health Tracker</h1>
          <p className="page-subtitle">Monitor your physical and mental well-being</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>{today ? "Update Today" : "+ Log Today"}</button>
      </div>

      {today ? (
        <div className="grid-2 mb-24">
          <div className="card">
            <div className="card-header"><div className="card-title">Today's Overview</div><span className="badge badge-green">Logged</span></div>
            <Metric label="Sleep" value={today.sleepHours} max={10} color="var(--purple)" unit="h" />
            <Metric label="Sleep Quality" value={today.sleepQuality} max={10} color="var(--blue)" unit="/10" />
            <Metric label="Mood" value={today.mood} max={10} color="var(--pink)" unit="/10" />
            <Metric label="Energy" value={today.energyLevel} max={10} color="var(--yellow)" unit="/10" />
            <Metric label="Stress" value={today.stressLevel} max={10} color="var(--red)" unit="/10" />
          </div>
          <div className="card">
            <div className="card-header"><div className="card-title">Activity & Nutrition</div></div>
            <Metric label="Water" value={today.waterMl} max={2000} color="var(--cyan)" unit="ml" />
            <Metric label="Steps" value={today.steps} max={10000} color="var(--green)" unit="" />
            <Metric label="Exercise" value={today.exerciseMinutes} max={60} color="var(--accent)" unit="min" />
            {today.caloriesIntake > 0 && <Metric label="Calories" value={today.caloriesIntake} max={2500} color="var(--orange)" unit="kcal" />}
            {today.notes && <div style={{ fontSize: 12, color: "var(--text-muted)", marginTop: 12, padding: "10px", background: "var(--bg-hover)", borderRadius: 6 }}>{today.notes}</div>}
          </div>
        </div>
      ) : (
        <div className="card mb-24" style={{ textAlign: "center", padding: 40 }}>
          <div style={{ fontSize: 40, marginBottom: 12 }}>♡</div>
          <div style={{ fontSize: 15, marginBottom: 8 }}>No health data logged today</div>
          <div style={{ fontSize: 13, color: "var(--text-muted)", marginBottom: 20 }}>Track your mood, sleep, and activities for AI-driven insights</div>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>Log Today's Health</button>
        </div>
      )}

      {logs.length > 1 && (
        <div className="card">
          <div className="card-header"><div className="card-title">Recent Logs</div></div>
          <div className="scroll-list">
            {logs.slice(0, 14).map(log => (
              <div key={log.id} className="list-item">
                <div style={{ width: 80, flexShrink: 0, fontSize: 12, color: "var(--text-muted)", fontFamily: "monospace" }}>
                  {new Date(log.logDate).toLocaleDateString("en-IN", { month: "short", day: "numeric" })}
                </div>
                <div style={{ flex: 1, display: "flex", gap: 16, fontSize: 12 }}>
                  {[
                    { label: "Mood", val: log.mood, color: "var(--pink)" },
                    { label: "Sleep", val: `${log.sleepHours}h`, color: "var(--purple)" },
                    { label: "Steps", val: log.steps?.toLocaleString(), color: "var(--green)" },
                    { label: "Stress", val: log.stressLevel, color: "var(--red)" },
                  ].map(m => (
                    <span key={m.label}><span style={{ color: "var(--text-muted)" }}>{m.label}: </span><span style={{ color: m.color, fontFamily: "monospace" }}>{m.val}</span></span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">Log Health - Today</div>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Sleep Hours</label><input className="form-input" type="number" step="0.5" min="0" max="24" value={form.sleepHours} onChange={e => setForm({...form, sleepHours: parseFloat(e.target.value)})} /></div>
                <div className="form-group"><label className="form-label">Sleep Quality (1-10)</label><input className="form-input" type="number" min="1" max="10" value={form.sleepQuality} onChange={e => setForm({...form, sleepQuality: parseInt(e.target.value)})} /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Mood (1-10)</label><input className="form-input" type="number" min="1" max="10" value={form.mood} onChange={e => setForm({...form, mood: parseInt(e.target.value)})} /></div>
                <div className="form-group"><label className="form-label">Energy (1-10)</label><input className="form-input" type="number" min="1" max="10" value={form.energyLevel} onChange={e => setForm({...form, energyLevel: parseInt(e.target.value)})} /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Stress Level (1-10)</label><input className="form-input" type="number" min="1" max="10" value={form.stressLevel} onChange={e => setForm({...form, stressLevel: parseInt(e.target.value)})} /></div>
                <div className="form-group"><label className="form-label">Water (ml)</label><input className="form-input" type="number" min="0" value={form.waterMl} onChange={e => setForm({...form, waterMl: parseInt(e.target.value)})} /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Steps</label><input className="form-input" type="number" min="0" value={form.steps} onChange={e => setForm({...form, steps: parseInt(e.target.value)})} /></div>
                <div className="form-group"><label className="form-label">Exercise (min)</label><input className="form-input" type="number" min="0" value={form.exerciseMinutes} onChange={e => setForm({...form, exerciseMinutes: parseInt(e.target.value)})} /></div>
              </div>
              <div className="form-group"><label className="form-label">Notes</label><textarea className="form-textarea" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} placeholder="How are you feeling today?" /></div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save Log</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// ============================================================
// FINANCE PAGE
// ============================================================
export function Finance() {
  const [transactions, setTransactions] = useState([]);
  const [summary, setSummary] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ type: "EXPENSE", amount: "", category: "", description: "", transactionDate: new Date().toISOString().split("T")[0], paymentMethod: "" });

  const CATEGORIES = ["Food", "Transport", "Rent", "Health", "Learning", "Entertainment", "Shopping", "Bills", "Salary", "Freelance", "Other"];

  const load = () => Promise.all([api.getTransactions(), api.getFinanceSummary()]).then(([t, s]) => { setTransactions(t); setSummary(s); }).catch(console.error).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.createTransaction({ ...form, amount: parseFloat(form.amount) });
    setShowModal(false);
    setForm({ type: "EXPENSE", amount: "", category: "", description: "", transactionDate: new Date().toISOString().split("T")[0], paymentMethod: "" });
    load();
  };

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Finance Tracker</h1>
          <p className="page-subtitle">Monthly overview & transactions</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>+ Add Transaction</button>
      </div>

      {summary && (
        <div className="grid-3 mb-24">
          <div className="stat-card">
            <div className="stat-icon" style={{ background: "var(--green-muted)" }}>↑</div>
            <div className="stat-info">
              <div className="stat-value text-green">₹{Number(summary.income || 0).toLocaleString("en-IN")}</div>
              <div className="stat-label">Monthly Income</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: "var(--red-muted)" }}>↓</div>
            <div className="stat-info">
              <div className="stat-value text-red">₹{Number(summary.expense || 0).toLocaleString("en-IN")}</div>
              <div className="stat-label">Monthly Expense</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: "var(--accent-glow)" }}>≡</div>
            <div className="stat-info">
              <div className="stat-value text-accent">₹{Number(summary.balance || 0).toLocaleString("en-IN")}</div>
              <div className="stat-label">Net Balance</div>
            </div>
          </div>
        </div>
      )}

      {summary?.expenseByCategory && Object.keys(summary.expenseByCategory).length > 0 && (
        <div className="card mb-24">
          <div className="card-header"><div className="card-title">Expense Breakdown</div></div>
          {Object.entries(summary.expenseByCategory).map(([cat, amt]) => {
            const pct = summary.expense > 0 ? (amt / summary.expense) * 100 : 0;
            return (
              <div key={cat} style={{ marginBottom: 12 }}>
                <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
                  <span style={{ color: "var(--text-secondary)" }}>{cat}</span>
                  <span style={{ fontFamily: "monospace", color: "var(--text-primary)" }}>₹{Number(amt).toLocaleString("en-IN")} ({pct.toFixed(0)}%)</span>
                </div>
                <div className="progress-bar"><div className="progress-fill" style={{ width: `${pct}%`, background: "var(--accent)" }} /></div>
              </div>
            );
          })}
        </div>
      )}

      <div className="card">
        <div className="card-header"><div className="card-title">Recent Transactions</div></div>
        <div className="scroll-list">
          {transactions.length === 0 ? (
            <div className="empty-state"><div className="empty-state-icon">₹</div><div className="empty-state-text">No transactions yet</div></div>
          ) : transactions.map(t => (
            <div key={t.id} className="list-item">
              <div style={{ width: 32, height: 32, borderRadius: 8, background: t.type === "INCOME" ? "var(--green-muted)" : "var(--red-muted)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 16, flexShrink: 0 }}>
                {t.type === "INCOME" ? "↑" : "↓"}
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{t.category}</div>
                <div style={{ fontSize: 11, color: "var(--text-muted)" }}>{t.description} · {new Date(t.transactionDate).toLocaleDateString("en-IN", { month: "short", day: "numeric" })}</div>
              </div>
              <div style={{ fontFamily: "monospace", fontSize: 14, fontWeight: 600, color: t.type === "INCOME" ? "var(--green)" : "var(--red)" }}>
                {t.type === "INCOME" ? "+" : "-"}₹{Number(t.amount).toLocaleString("en-IN")}
              </div>
              <button className="btn btn-sm btn-danger btn-icon" onClick={() => api.deleteTransaction(t.id).then(load)}>✕</button>
            </div>
          ))}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">Add Transaction</div>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Type</label>
                  <select className="form-select" value={form.type} onChange={e => setForm({...form, type: e.target.value})}>
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Amount (₹)</label>
                  <input className="form-input" type="number" step="0.01" min="0" value={form.amount} onChange={e => setForm({...form, amount: e.target.value})} required />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select className="form-select" value={form.category} onChange={e => setForm({...form, category: e.target.value})} required>
                    <option value="">Select...</option>
                    {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Date</label>
                  <input className="form-input" type="date" value={form.transactionDate} onChange={e => setForm({...form, transactionDate: e.target.value})} required />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <input className="form-input" value={form.description} onChange={e => setForm({...form, description: e.target.value})} placeholder="Optional note..." />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Add Transaction</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// ============================================================
// GOALS PAGE
// ============================================================
export function Goals() {
  const [goals, setGoals] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [progressModal, setProgressModal] = useState(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ title: "", description: "", category: "PERSONAL", targetValue: "", currentValue: 0, unit: "", targetDate: "" });
  const [progressVal, setProgressVal] = useState(0);

  const load = () => api.getGoals().then(setGoals).catch(console.error).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.createGoal({ ...form, targetValue: parseFloat(form.targetValue) });
    setShowModal(false);
    load();
  };

  const handleProgress = async (e) => {
    e.preventDefault();
    await api.updateGoalProgress(progressModal.id, parseFloat(progressVal));
    setProgressModal(null);
    load();
  };

  const CATEGORY_COLORS = { ACADEMIC: "var(--blue)", HEALTH: "var(--green)", FINANCE: "var(--yellow)", CAREER: "var(--accent)", PERSONAL: "var(--purple)", SKILL: "var(--cyan)" };

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Goals</h1>
          <p className="page-subtitle">{goals.filter(g => g.status === "ACTIVE").length} active goals</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>+ New Goal</button>
      </div>

      {goals.length === 0 ? (
        <div className="empty-state"><div className="empty-state-icon">◎</div><div className="empty-state-text">Set your first goal to get started</div></div>
      ) : (
        <div className="grid-auto">
          {goals.map(goal => {
            const pct = goal.targetValue > 0 ? Math.min((goal.currentValue / goal.targetValue) * 100, 100) : 0;
            const color = CATEGORY_COLORS[goal.category] || "var(--accent)";
            return (
              <div key={goal.id} className="card">
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 12 }}>
                  <div>
                    <div style={{ fontSize: 14, fontWeight: 600, marginBottom: 4 }}>{goal.title}</div>
                    <span className="badge" style={{ background: color + "22", color }}>{goal.category}</span>
                  </div>
                  <button className="btn btn-sm btn-danger btn-icon" onClick={() => api.deleteGoal(goal.id).then(load)}>✕</button>
                </div>

                {goal.description && <div style={{ fontSize: 12, color: "var(--text-muted)", marginBottom: 12 }}>{goal.description}</div>}

                <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
                  <span style={{ color: "var(--text-secondary)" }}>Progress</span>
                  <span style={{ color, fontFamily: "monospace", fontWeight: 600 }}>{pct.toFixed(0)}%</span>
                </div>
                <div className="progress-bar mb-8">
                  <div className="progress-fill" style={{ width: `${pct}%`, background: color }} />
                </div>
                <div className="flex justify-between" style={{ fontSize: 11, color: "var(--text-muted)", marginBottom: 14 }}>
                  <span>{goal.currentValue} / {goal.targetValue} {goal.unit}</span>
                  {goal.targetDate && <span>Due: {new Date(goal.targetDate).toLocaleDateString("en-IN", { month: "short", day: "numeric" })}</span>}
                </div>

                <div style={{ display: "flex", gap: 8 }}>
                  <button className="btn btn-sm btn-secondary" style={{ flex: 1 }} onClick={() => { setProgressModal(goal); setProgressVal(goal.currentValue); }}>
                    Update Progress
                  </button>
                  {goal.status === "COMPLETED" && <span className="badge badge-green">✓ Done</span>}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">New Goal</div>
            <form onSubmit={handleSubmit}>
              <div className="form-group"><label className="form-label">Goal Title *</label><input className="form-input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} required /></div>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select className="form-select" value={form.category} onChange={e => setForm({...form, category: e.target.value})}>
                    {["ACADEMIC","HEALTH","FINANCE","CAREER","PERSONAL","SKILL"].map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
                <div className="form-group"><label className="form-label">Unit</label><input className="form-input" value={form.unit} onChange={e => setForm({...form, unit: e.target.value})} placeholder="kg, INR, projects..." /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Target Value</label><input className="form-input" type="number" value={form.targetValue} onChange={e => setForm({...form, targetValue: e.target.value})} required /></div>
                <div className="form-group"><label className="form-label">Target Date</label><input className="form-input" type="date" value={form.targetDate} onChange={e => setForm({...form, targetDate: e.target.value})} /></div>
              </div>
              <div className="form-group"><label className="form-label">Description</label><textarea className="form-textarea" value={form.description} onChange={e => setForm({...form, description: e.target.value})} /></div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Create Goal</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {progressModal && (
        <div className="modal-overlay" onClick={() => setProgressModal(null)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ maxWidth: 360 }}>
            <div className="modal-title">Update Progress: {progressModal.title}</div>
            <form onSubmit={handleProgress}>
              <div className="form-group">
                <label className="form-label">Current Value ({progressModal.unit})</label>
                <input className="form-input" type="number" step="any" value={progressVal} onChange={e => setProgressVal(e.target.value)} required />
                <div style={{ fontSize: 11, color: "var(--text-muted)", marginTop: 4 }}>Target: {progressModal.targetValue} {progressModal.unit}</div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setProgressModal(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Update</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// ============================================================
// LEARNING PAGE
// ============================================================
export function Learning() {
  const [courses, setCourses] = useState([]);
  const [stats, setStats] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ title: "", platform: "", category: "", totalModules: 1, completedModules: 0, totalHours: "", spentHours: 0, status: "NOT_STARTED", startDate: "", targetCompletionDate: "" });

  const load = () => Promise.all([api.getCourses(), api.getLearningStats()]).then(([c, s]) => { setCourses(c); setStats(s); }).catch(console.error).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.createCourse({ ...form, totalHours: parseFloat(form.totalHours) || 0 });
    setShowModal(false);
    load();
  };

  const updateModules = async (course, mod) => {
    await api.updateCourse(course.id, { ...course, completedModules: mod, status: mod >= course.totalModules ? "COMPLETED" : mod > 0 ? "IN_PROGRESS" : "NOT_STARTED" });
    load();
  };

  const STATUS_COLORS = { NOT_STARTED: "var(--text-muted)", IN_PROGRESS: "var(--accent)", COMPLETED: "var(--green)", PAUSED: "var(--yellow)" };

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Learning</h1>
          <p className="page-subtitle">Track your courses and skill development</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>+ Add Course</button>
      </div>

      {stats && (
        <div className="grid-4 mb-24">
          {[
            { label: "Total Hours", value: `${Number(stats.totalHours || 0).toFixed(1)}h`, color: "var(--accent)" },
            { label: "Total Courses", value: stats.totalCourses, color: "var(--blue)" },
            { label: "Completed", value: stats.completed, color: "var(--green)" },
            { label: "In Progress", value: stats.inProgress, color: "var(--yellow)" },
          ].map(s => (
            <div key={s.label} className="card" style={{ textAlign: "center", padding: "16px" }}>
              <div style={{ fontSize: 24, fontWeight: 700, fontFamily: "monospace", color: s.color }}>{s.value}</div>
              <div style={{ fontSize: 12, color: "var(--text-secondary)", marginTop: 4 }}>{s.label}</div>
            </div>
          ))}
        </div>
      )}

      {courses.length === 0 ? (
        <div className="empty-state"><div className="empty-state-icon">◈</div><div className="empty-state-text">Add your first course to start tracking</div></div>
      ) : (
        <div className="grid-auto">
          {courses.map(course => {
            const pct = course.totalModules > 0 ? Math.min((course.completedModules / course.totalModules) * 100, 100) : 0;
            const statusColor = STATUS_COLORS[course.status] || "var(--text-muted)";
            return (
              <div key={course.id} className="card">
                <div style={{ marginBottom: 12 }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                    <div style={{ fontSize: 14, fontWeight: 600, marginBottom: 4, flex: 1 }}>{course.title}</div>
                    <span className="badge" style={{ background: statusColor + "22", color: statusColor, flexShrink: 0, marginLeft: 8 }}>{course.status.replace("_"," ")}</span>
                  </div>
                  <div style={{ fontSize: 12, color: "var(--text-muted)" }}>
                    {course.platform && <span>{course.platform}</span>}
                    {course.category && <span> · {course.category}</span>}
                  </div>
                </div>

                <div className="flex justify-between mb-4" style={{ fontSize: 13 }}>
                  <span style={{ color: "var(--text-secondary)" }}>Modules</span>
                  <span style={{ color: statusColor, fontFamily: "monospace" }}>{course.completedModules}/{course.totalModules}</span>
                </div>
                <div className="progress-bar mb-8">
                  <div className="progress-fill" style={{ width: `${pct}%`, background: statusColor }} />
                </div>

                <div className="flex justify-between mb-12" style={{ fontSize: 11, color: "var(--text-muted)" }}>
                  <span>{pct.toFixed(0)}% complete</span>
                  {course.totalHours && <span>{Number(course.spentHours || 0).toFixed(1)}h / {course.totalHours}h</span>}
                </div>

                <div style={{ display: "flex", gap: 6 }}>
                  <button className="btn btn-sm btn-secondary" onClick={() => updateModules(course, Math.max(0, course.completedModules - 1))}>−</button>
                  <div style={{ flex: 1, textAlign: "center", fontSize: 12, padding: "5px 0", color: "var(--text-secondary)" }}>module {course.completedModules}</div>
                  <button className="btn btn-sm btn-primary" onClick={() => updateModules(course, Math.min(course.totalModules, course.completedModules + 1))}>+</button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">Add Course</div>
            <form onSubmit={handleSubmit}>
              <div className="form-group"><label className="form-label">Course Title *</label><input className="form-input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} required /></div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Platform</label><input className="form-input" value={form.platform} onChange={e => setForm({...form, platform: e.target.value})} placeholder="Udemy, Coursera..." /></div>
                <div className="form-group"><label className="form-label">Category</label><input className="form-input" value={form.category} onChange={e => setForm({...form, category: e.target.value})} placeholder="Backend, AI/ML..." /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Total Modules</label><input className="form-input" type="number" min="1" value={form.totalModules} onChange={e => setForm({...form, totalModules: parseInt(e.target.value)})} /></div>
                <div className="form-group"><label className="form-label">Total Hours</label><input className="form-input" type="number" step="0.5" value={form.totalHours} onChange={e => setForm({...form, totalHours: e.target.value})} /></div>
              </div>
              <div className="form-row">
                <div className="form-group"><label className="form-label">Start Date</label><input className="form-input" type="date" value={form.startDate} onChange={e => setForm({...form, startDate: e.target.value})} /></div>
                <div className="form-group"><label className="form-label">Target Completion</label><input className="form-input" type="date" value={form.targetCompletionDate} onChange={e => setForm({...form, targetCompletionDate: e.target.value})} /></div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Add Course</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// ============================================================
// SUGGESTIONS PAGE
// ============================================================
export function Suggestions() {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => api.getSuggestions().then(setSuggestions).catch(console.error).finally(() => setLoading(false));
  useEffect(() => { load(); }, []);

  const markRead = async (id) => {
    await api.markSuggestionRead(id);
    load();
  };

  const TYPE_CONFIG = {
    STRESS: { icon: "⚡", color: "var(--red)", label: "Stress Alert" },
    HEALTH: { icon: "♡", color: "var(--green)", label: "Health" },
    PRODUCTIVITY: { icon: "◆", color: "var(--accent)", label: "Productivity" },
    FINANCE: { icon: "₹", color: "var(--yellow)", label: "Finance" },
    LEARNING: { icon: "◈", color: "var(--blue)", label: "Learning" },
    SCHEDULE: { icon: "⊞", color: "var(--purple)", label: "Schedule" },
  };

  if (loading) return <div className="loading"><div className="spinner"/></div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">AI Insights</h1>
          <p className="page-subtitle">{suggestions.length} personalized recommendations</p>
        </div>
      </div>

      {suggestions.length === 0 ? (
        <div className="empty-state"><div className="empty-state-icon">◆</div><div className="empty-state-text">No new suggestions. Keep logging your activities!</div></div>
      ) : (
        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          {suggestions.map(s => {
            const cfg = TYPE_CONFIG[s.suggestionType] || { icon: "◆", color: "var(--accent)", label: s.suggestionType };
            return (
              <div key={s.id} className="card" style={{ borderLeft: `4px solid ${cfg.color}` }}>
                <div style={{ display: "flex", alignItems: "flex-start", gap: 16 }}>
                  <div style={{ width: 40, height: 40, borderRadius: 10, background: cfg.color + "22", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 18, flexShrink: 0 }}>
                    {cfg.icon}
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 6 }}>
                      <span style={{ fontSize: 14, fontWeight: 600 }}>{s.title}</span>
                      <span className="badge" style={{ background: cfg.color + "22", color: cfg.color, fontSize: 10 }}>{cfg.label}</span>
                      <span style={{ fontSize: 11, color: "var(--text-muted)", marginLeft: "auto" }}>Priority: {s.priority}/10</span>
                    </div>
                    <div style={{ fontSize: 13, color: "var(--text-secondary)", lineHeight: 1.7 }}>{s.content}</div>
                    <div style={{ marginTop: 12 }}>
                      <button className="btn btn-sm btn-secondary" onClick={() => markRead(s.id)}>Mark as Read</button>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
