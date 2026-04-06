import { useState } from "react";
import { useAuth } from "../App";
import { api } from "../services/api";

export default function Login() {
  const { login } = useAuth();
  const [tab, setTab] = useState("login");
  const [form, setForm] = useState({ name: "", email: "", password: "", timezone: "Asia/Kolkata" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = tab === "login"
        ? await api.login({ email: form.email, password: form.password })
        : await api.register(form);
      login(data);
    } catch (err) {
      setError(err.message || "Authentication failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-logo">
          <div className="login-logo-icon">⬡</div>
          <div className="login-logo-title">ULMIP</div>
          <div className="login-logo-sub">Unified Life Management Intelligence Platform</div>
        </div>

        <div className="login-tabs">
          <div className={`login-tab ${tab === "login" ? "active" : ""}`} onClick={() => setTab("login")}>Sign In</div>
          <div className={`login-tab ${tab === "register" ? "active" : ""}`} onClick={() => setTab("register")}>Register</div>
        </div>

        {error && <div className="login-error">⚠ {error}</div>}

        <form onSubmit={handleSubmit}>
          {tab === "register" && (
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input className="form-input" value={form.name} onChange={e => setForm({...form, name: e.target.value})} placeholder="Arjun Sharma" required />
            </div>
          )}
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input className="form-input" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} placeholder="you@example.com" required />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input className="form-input" type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})} placeholder="••••••••" required />
          </div>

          <button type="submit" className="btn btn-primary w-full" style={{ width: "100%", justifyContent: "center", padding: "10px", marginTop: "8px" }} disabled={loading}>
            {loading ? "Please wait..." : tab === "login" ? "Sign In to ULMIP" : "Create Account"}
          </button>
        </form>

        <div style={{ marginTop: 20, textAlign: "center", fontSize: 12, color: "var(--text-muted)" }}>
          Demo: arjun@ulmip.ai / Password@123
        </div>
      </div>
    </div>
  );
}
