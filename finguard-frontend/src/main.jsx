import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  AlertTriangle,
  ArrowRight,
  BadgeCheck,
  CircleDollarSign,
  Eye,
  LogOut,
  Plus,
  RefreshCw,
  Shield,
  ShieldAlert,
  ShieldCheck,
  WalletCards,
} from 'lucide-react';
import './styles.css';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const TRANSACTION_CATEGORIES = [
  'Groceries',
  'Entertainment',
  'Travel',
  'Utilities',
  'Food',
  'Shopping',
  'Health',
  'Education',
  'Other',
];

const initialTransactionForm = {
  amount: '',
  category: 'Groceries',
  merchant: '',
  transactionTime: new Date().toISOString().slice(0, 16),
};

function apiRequest(path, { token, method = 'GET', body } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  }).then(async (response) => {
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) {
      const message = data?.messages?.join(', ') || 'Request failed';
      throw new Error(message);
    }
    return data;
  });
}

function App() {
  const [auth, setAuth] = useState(() => {
    const stored = localStorage.getItem('finguard.auth');
    return stored ? JSON.parse(stored) : null;
  });
  const [transactions, setTransactions] = useState([]);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');

  const token = auth?.token;

  const flaggedCount = useMemo(
    () => transactions.filter((transaction) => transaction.flagged).length,
    [transactions],
  );

  const latestSignal = transactions[0];
  const safetyState = latestSignal?.riskLevel || (flaggedCount > 0 ? 'Attention' : 'Stable');

  const totalVisibleSpend = useMemo(
    () => transactions.reduce((sum, transaction) => sum + Number(transaction.amount), 0),
    [transactions],
  );

  async function loadDashboard() {
    if (!token) return;
    setLoading(true);
    setError('');
    try {
      const [profileData, transactionData] = await Promise.all([
        apiRequest('/api/profile/spending', { token }),
        apiRequest('/api/transactions', { token }),
      ]);
      setProfile(profileData);
      setTransactions(transactionData);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, [token]);

  function handleAuth(nextAuth) {
    localStorage.setItem('finguard.auth', JSON.stringify(nextAuth));
    setAuth(nextAuth);
    setNotice(`Signed in as ${nextAuth.username}`);
  }

  function handleLogout() {
    localStorage.removeItem('finguard.auth');
    setAuth(null);
    setTransactions([]);
    setProfile(null);
    setNotice('');
    setError('');
  }

  async function createTransaction(form) {
    setLoading(true);
    setError('');
    setNotice('');
    try {
      await apiRequest('/api/transactions', {
        token,
        method: 'POST',
        body: {
          amount: Number(form.amount),
          category: form.category,
          merchant: form.merchant,
          transactionTime: form.transactionTime,
        },
      });
      setNotice('Transaction saved');
      await loadDashboard();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  if (!auth) {
    return <AuthScreen onAuth={handleAuth} />;
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand-row">
          <div className="brand-mark" aria-hidden="true">
            <Shield size={24} />
          </div>
          <div>
            <h1>FinGuard</h1>
            <p>{auth.email}</p>
          </div>
        </div>
        <nav className="nav-stack" aria-label="Dashboard">
          <a href="#overview">Overview</a>
          <a href="#new-transaction">New transaction</a>
          <a href="#history">History</a>
        </nav>
        <button className="ghost-button" type="button" onClick={handleLogout}>
          <LogOut size={18} />
          Sign out
        </button>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">AI financial safety companion</p>
            <h2>Monitor transactions, detect risk, and choose the safest next step.</h2>
          </div>
          <button className="icon-button" type="button" onClick={loadDashboard} title="Refresh dashboard">
            <RefreshCw size={19} />
          </button>
        </header>

        {(notice || error) && (
          <div className={error ? 'status-banner error' : 'status-banner'}>
            {error || notice}
          </div>
        )}

        <section className="metrics-grid" id="overview">
          <Metric
            icon={safetyState === 'Stable' ? <ShieldCheck size={21} /> : <ShieldAlert size={21} />}
            label="Safety state"
            value={safetyState}
            tone={safetyState === 'Stable' ? 'ok' : 'alert'}
          />
          <Metric
            icon={<AlertTriangle size={21} />}
            label="Signals needing review"
            value={flaggedCount}
            tone={flaggedCount > 0 ? 'alert' : 'ok'}
          />
          <Metric
            icon={<WalletCards size={21} />}
            label="Transactions monitored"
            value={profile?.transactionCount ?? transactions.length}
          />
          <Metric
            icon={<CircleDollarSign size={21} />}
            label="Month watched"
            value={formatMoney(profile?.currentMonthSpend || 0)}
          />
        </section>

        <SafetyBrief transaction={latestSignal} totalVisibleSpend={totalVisibleSpend} />

        <section className="content-grid">
          <TransactionForm onSubmit={createTransaction} loading={loading} />
          <RiskPanel transactions={transactions} />
        </section>

        <TransactionTable transactions={transactions} loading={loading} />
      </section>
    </main>
  );
}

function AuthScreen({ onAuth }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({
    username: '',
    email: '',
    usernameOrEmail: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function submitAuth(event) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const path = mode === 'login' ? '/api/auth/login' : '/api/auth/register';
      const body =
        mode === 'login'
          ? { usernameOrEmail: form.usernameOrEmail, password: form.password }
          : { username: form.username, email: form.email, password: form.password };
      const response = await apiRequest(path, { method: 'POST', body });
      onAuth(response);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-layout">
      <section className="auth-visual">
        <div className="brand-lockup">
          <div className="brand-mark large" aria-hidden="true">
            <Shield size={34} />
          </div>
          <h1>FinGuard</h1>
        </div>
        <p className="auth-promise">
          An AI safety layer for your money: it watches transaction patterns, explains what changed,
          and points you to the lowest-risk next move.
        </p>
        <div className="signal-board" aria-hidden="true">
          <div className="signal-row strong">
            <span>Safety state</span>
            <b>Stable</b>
          </div>
          <div className="signal-row alert">
            <span>Unusual payment</span>
            <b>Review</b>
          </div>
          <div className="signal-row">
            <span>Safest next step</span>
            <b>Verify</b>
          </div>
        </div>
      </section>

      <section className="auth-panel">
        <div className="mode-switch" role="tablist" aria-label="Authentication mode">
          <button className={mode === 'login' ? 'active' : ''} type="button" onClick={() => setMode('login')}>
            Login
          </button>
          <button className={mode === 'register' ? 'active' : ''} type="button" onClick={() => setMode('register')}>
            Register
          </button>
        </div>

        <form onSubmit={submitAuth} className="auth-form">
          <h2>{mode === 'login' ? 'Welcome back' : 'Create account'}</h2>
          {mode === 'register' && (
            <>
              <label>
                Username
                <input
                  value={form.username}
                  onChange={(event) => setForm({ ...form, username: event.target.value })}
                  required
                  minLength={3}
                />
              </label>
              <label>
                Email
                <input
                  type="email"
                  value={form.email}
                  onChange={(event) => setForm({ ...form, email: event.target.value })}
                  required
                />
              </label>
            </>
          )}
          {mode === 'login' && (
            <label>
              Username or email
              <input
                value={form.usernameOrEmail}
                onChange={(event) => setForm({ ...form, usernameOrEmail: event.target.value })}
                required
              />
            </label>
          )}
          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              required
              minLength={8}
            />
          </label>
          {error && <div className="status-banner error">{error}</div>}
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Please wait' : mode === 'login' ? 'Login' : 'Register'}
            <ArrowRight size={18} />
          </button>
        </form>
      </section>
    </main>
  );
}

function Metric({ icon, label, value, tone }) {
  return (
    <article className={`metric ${tone || ''}`}>
      <div className="metric-icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function SafetyBrief({ transaction, totalVisibleSpend }) {
  const hasSignal = Boolean(transaction);
  return (
    <section className={`safety-brief ${transaction?.flagged ? 'alert' : ''}`}>
      <div className="brief-main">
        <div className="brief-icon" aria-hidden="true">
          {transaction?.flagged ? <ShieldAlert size={24} /> : <ShieldCheck size={24} />}
        </div>
        <div>
          <p className="eyebrow">Latest safety read</p>
          <h3>{hasSignal ? transaction.whatChanged : 'FinGuard is ready to monitor your first transaction.'}</h3>
        </div>
      </div>
      <div className="brief-action">
        <span>Safest next step</span>
        <strong>{hasSignal ? transaction.safestNextStep : 'Add a transaction to establish your baseline.'}</strong>
      </div>
      <div className="brief-stat">
        <span>Visible spend watched</span>
        <strong>{formatMoney(totalVisibleSpend)}</strong>
      </div>
    </section>
  );
}

function TransactionForm({ onSubmit, loading }) {
  const [form, setForm] = useState(initialTransactionForm);

  function submit(event) {
    event.preventDefault();
    onSubmit(form);
    setForm(initialTransactionForm);
  }

  return (
    <section className="panel" id="new-transaction">
      <div className="section-heading">
        <h3>Add transaction to monitor</h3>
        <Plus size={20} />
      </div>
      <form className="transaction-form" onSubmit={submit}>
        <label>
          Amount
          <input
            type="number"
            min="0.01"
            step="0.01"
            value={form.amount}
            onChange={(event) => setForm({ ...form, amount: event.target.value })}
            required
          />
        </label>
        <label>
          Category
          <select value={form.category} onChange={(event) => setForm({ ...form, category: event.target.value })}>
            {TRANSACTION_CATEGORIES.map((category) => (
              <option key={category}>{category}</option>
            ))}
          </select>
        </label>
        <label>
          Merchant
          <input
            value={form.merchant}
            onChange={(event) => setForm({ ...form, merchant: event.target.value })}
            required
          />
        </label>
        <label>
          Time
          <input
            type="datetime-local"
            value={form.transactionTime}
            onChange={(event) => setForm({ ...form, transactionTime: event.target.value })}
            required
          />
        </label>
        <button className="primary-button" type="submit" disabled={loading}>
          Monitor
          <BadgeCheck size={18} />
        </button>
      </form>
    </section>
  );
}

function RiskPanel({ transactions }) {
  const latest = transactions[0];
  return (
    <section className="panel risk-panel">
      <div className="section-heading">
        <h3>Signal detail</h3>
        <Eye size={20} />
      </div>
      <div className="risk-meter" style={{ '--risk': latest ? latest.riskScore : 0 }}>
        <span>{latest ? Math.round(latest.riskScore * 100) : 0}</span>
      </div>
      <div className="risk-copy">
        <strong>{latest?.riskLevel || 'Stable'} risk score</strong>
        <p>{latest ? `${latest.merchant} · ${latest.category}` : 'No transactions yet'}</p>
      </div>
      <div className="next-step-card">
        <span>What changed</span>
        <strong>{latest?.whatChanged || 'No unusual change detected yet.'}</strong>
      </div>
      <div className="next-step-card">
        <span>Next step</span>
        <strong>{latest?.safestNextStep || 'Start monitoring by adding a transaction.'}</strong>
      </div>
    </section>
  );
}

function TransactionTable({ transactions, loading }) {
  return (
    <section className="table-section" id="history">
      <div className="section-heading">
        <h3>Transaction history</h3>
        <span>{loading ? 'Loading' : `${transactions.length} rows`}</span>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Merchant</th>
              <th>Category</th>
              <th>Time</th>
              <th>Amount</th>
              <th>AI read</th>
              <th>Risk</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {transactions.length === 0 ? (
              <tr>
                <td colSpan="7" className="empty-cell">No transactions</td>
              </tr>
            ) : (
              transactions.map((transaction) => (
                <tr key={transaction.id}>
                  <td>{transaction.merchant}</td>
                  <td>{transaction.category}</td>
                  <td>{formatDate(transaction.transactionTime)}</td>
                  <td>{formatMoney(transaction.amount)}</td>
                  <td>{transaction.riskLevel || 'Stable'}</td>
                  <td>{Math.round(transaction.riskScore * 100)}%</td>
                  <td>
                    <span className={transaction.flagged ? 'pill alert' : 'pill ok'}>
                      {transaction.flagged ? 'Flagged' : 'Clear'}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function formatMoney(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(Number(value || 0));
}

function formatDate(value) {
  if (!value) return '';
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

createRoot(document.getElementById('root')).render(<App />);
