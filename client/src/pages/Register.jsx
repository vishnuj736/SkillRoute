import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';

const Register = () => {
    const [form, setForm] = useState({
        name: '', email: '', password: '', role: 'ROLE_LEARNER'
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const res = await api.post('/api/auth/register', form);
            login(res.data.token);
            navigate(form.role === 'ROLE_ADMIN' ? '/admin' : '/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center">
            <div className="max-w-md w-full bg-white rounded-xl shadow-md p-8">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-indigo-600">SkillRoute</h1>
                    <p className="text-gray-500 mt-2">Create your account</p>
                </div>

                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Full Name
                        </label>
                        <input
                            type="text"
                            required
                            value={form.name}
                            onChange={e => setForm({...form, name: e.target.value})}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2
                                     focus:outline-none focus:ring-2 focus:ring-indigo-500"
                            placeholder="Vishnu J"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Email
                        </label>
                        <input
                            type="email"
                            required
                            value={form.email}
                            onChange={e => setForm({...form, email: e.target.value})}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2
                                     focus:outline-none focus:ring-2 focus:ring-indigo-500"
                            placeholder="you@example.com"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Password
                        </label>
                        <input
                            type="password"
                            required
                            value={form.password}
                            onChange={e => setForm({...form, password: e.target.value})}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2
                                     focus:outline-none focus:ring-2 focus:ring-indigo-500"
                            placeholder="••••••••"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Role
                        </label>
                        <select
                            value={form.role}
                            onChange={e => setForm({...form, role: e.target.value})}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2
                                     focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        >
                            <option value="ROLE_LEARNER">Learner</option>
                            <option value="ROLE_ADMIN">Admin</option>
                        </select>
                    </div>
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-indigo-600 text-white py-2 rounded-lg
                                 font-medium hover:bg-indigo-700 transition-colors
                                 disabled:opacity-50"
                    >
                        {loading ? 'Creating account...' : 'Create Account'}
                    </button>
                </form>

                <p className="text-center text-sm text-gray-500 mt-6">
                    Already have an account?{' '}
                    <Link to="/login"
                          className="text-indigo-600 hover:underline font-medium">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Register;