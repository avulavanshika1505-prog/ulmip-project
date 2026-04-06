import { useState, useEffect, createContext, useContext } from "react";
import Dashboard from "./pages/Dashboard";
import Tasks from "./pages/Tasks";
import Health from "./pages/Health";
import Finance from "./pages/Finance";
import Goals from "./pages/Goals";
import Learning from "./pages/Learning";
import Suggestions from "./pages/Suggestions";
import Login from "./pages/Login";
import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import { api } from "./services/api";

// ============================================================
// AUTH CONTEXT
// ============================================================
export const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

const PAGES = {
  dashboard: Dashboard,
  tasks: Tasks,
  health: Health,
  finance: Finance,
  goals: Goals,
  learning: Learning,
  suggestions: Suggestions,
};

export default function App() {
  const [auth, setAuth] = useState(() => {
    const token = localStorage.getItem("ulmip_token");
    const user = localStorage.getItem("ulmip_user");
    return token && user ? { token, user: JSON.parse(user) } : null;
  });
  const [activePage, setActivePage] = useState("dashboard");
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const login = (data) => {
    localStorage.setItem("ulmip_token", data.token);
    localStorage.setItem("ulmip_user", JSON.stringify({ name: data.name, email: data.email, userId: data.userId }));
    setAuth({ token: data.token, user: { name: data.name, email: data.email, userId: data.userId } });
  };

  const logout = () => {
    localStorage.removeItem("ulmip_token");
    localStorage.removeItem("ulmip_user");
    setAuth(null);
  };

  if (!auth) {
    return (
      <AuthContext.Provider value={{ auth, login, logout }}>
        <Login />
      </AuthContext.Provider>
    );
  }

  const PageComponent = PAGES[activePage] || Dashboard;

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      <div className="app-shell">
        <Sidebar
          activePage={activePage}
          setActivePage={setActivePage}
          isOpen={sidebarOpen}
          onToggle={() => setSidebarOpen(!sidebarOpen)}
        />
        <div className={`main-area ${sidebarOpen ? "sidebar-open" : "sidebar-closed"}`}>
          <Header
            user={auth.user}
            onLogout={logout}
            onMenuToggle={() => setSidebarOpen(!sidebarOpen)}
          />
          <main className="page-content">
            <PageComponent />
          </main>
        </div>
      </div>
    </AuthContext.Provider>
  );
}
