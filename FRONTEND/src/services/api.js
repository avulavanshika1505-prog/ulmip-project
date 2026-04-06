// ============================================================
// ULMIP API Service
// ============================================================

const BASE_URL = "http://localhost:8080/api";

const getToken = () => localStorage.getItem("ulmip_token");

const headers = () => ({
  "Content-Type": "application/json",
  Authorization: `Bearer ${getToken()}`,
});

const handleResponse = async (res) => {
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: "Network error" }));
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  return res.json();
};

export const api = {
  // Auth
  login: (data) =>
    fetch(`${BASE_URL}/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(data) }).then(handleResponse),
  register: (data) =>
    fetch(`${BASE_URL}/auth/register`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(data) }).then(handleResponse),

  // Dashboard
  getDashboardOverview: () =>
    fetch(`${BASE_URL}/dashboard/overview`, { headers: headers() }).then(handleResponse),

  // Tasks
  getTasks: () => fetch(`${BASE_URL}/tasks`, { headers: headers() }).then(handleResponse),
  getTaskStats: () => fetch(`${BASE_URL}/tasks/stats`, { headers: headers() }).then(handleResponse),
  createTask: (data) =>
    fetch(`${BASE_URL}/tasks`, { method: "POST", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  updateTask: (id, data) =>
    fetch(`${BASE_URL}/tasks/${id}`, { method: "PUT", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  updateTaskStatus: (id, status) =>
    fetch(`${BASE_URL}/tasks/${id}/status`, { method: "PATCH", headers: headers(), body: JSON.stringify({ status }) }).then(handleResponse),
  deleteTask: (id) =>
    fetch(`${BASE_URL}/tasks/${id}`, { method: "DELETE", headers: headers() }),

  // Health
  getHealthLogs: () => fetch(`${BASE_URL}/health`, { headers: headers() }).then(handleResponse),
  getTodayHealth: () => fetch(`${BASE_URL}/health/today`, { headers: headers() }).then(handleResponse),
  logHealth: (data) =>
    fetch(`${BASE_URL}/health`, { method: "POST", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  getHealthRange: (start, end) =>
    fetch(`${BASE_URL}/health/range?start=${start}&end=${end}`, { headers: headers() }).then(handleResponse),

  // Finance
  getTransactions: () => fetch(`${BASE_URL}/finance/transactions`, { headers: headers() }).then(handleResponse),
  createTransaction: (data) =>
    fetch(`${BASE_URL}/finance/transactions`, { method: "POST", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  deleteTransaction: (id) =>
    fetch(`${BASE_URL}/finance/transactions/${id}`, { method: "DELETE", headers: headers() }),
  getFinanceSummary: () => fetch(`${BASE_URL}/finance/summary`, { headers: headers() }).then(handleResponse),

  // Goals
  getGoals: () => fetch(`${BASE_URL}/goals`, { headers: headers() }).then(handleResponse),
  createGoal: (data) =>
    fetch(`${BASE_URL}/goals`, { method: "POST", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  updateGoalProgress: (id, currentValue) =>
    fetch(`${BASE_URL}/goals/${id}/progress`, { method: "PUT", headers: headers(), body: JSON.stringify({ currentValue }) }).then(handleResponse),
  deleteGoal: (id) =>
    fetch(`${BASE_URL}/goals/${id}`, { method: "DELETE", headers: headers() }),

  // Learning
  getCourses: () => fetch(`${BASE_URL}/learning`, { headers: headers() }).then(handleResponse),
  createCourse: (data) =>
    fetch(`${BASE_URL}/learning`, { method: "POST", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  updateCourse: (id, data) =>
    fetch(`${BASE_URL}/learning/${id}`, { method: "PUT", headers: headers(), body: JSON.stringify(data) }).then(handleResponse),
  getLearningStats: () => fetch(`${BASE_URL}/learning/stats`, { headers: headers() }).then(handleResponse),

  // Suggestions
  getSuggestions: () => fetch(`${BASE_URL}/suggestions`, { headers: headers() }).then(handleResponse),
  markSuggestionRead: (id) =>
    fetch(`${BASE_URL}/suggestions/${id}/read`, { method: "PATCH", headers: headers() }).then(handleResponse),
  getSuggestionCount: () => fetch(`${BASE_URL}/suggestions/count`, { headers: headers() }).then(handleResponse),
};
