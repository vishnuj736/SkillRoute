import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';

const COURSE_ID = 1;

const AdminDashboard = () => {
    const { logout } = useAuth();
    const [analytics, setAnalytics] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchAnalytics();
    }, []);

    const fetchAnalytics = async () => {
        try {
            const res = await api.get(
                `/api/admin/analytics/course/${COURSE_ID}`);
            setAnalytics(res.data);
        } catch (err) {
            console.error('Failed to fetch analytics', err);
        } finally {
            setLoading(false);
        }
    };

    const getHealthColor = (status) => {
        switch (status) {
            case 'HEALTHY': return 'bg-green-100 text-green-700';
            case 'AT_RISK': return 'bg-yellow-100 text-yellow-700';
            case 'INACTIVE': return 'bg-red-100 text-red-700';
            default: return 'bg-gray-100 text-gray-700';
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-indigo-600 text-lg">Loading analytics...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-indigo-600 text-white px-6 py-4 flex
                           justify-between items-center">
                <h1 className="text-xl font-bold">SkillRoute Admin</h1>
                <button
                    onClick={logout}
                    className="text-sm bg-indigo-700 px-4 py-2 rounded-lg
                             hover:bg-indigo-800 transition-colors"
                >
                    Logout
                </button>
            </nav>

            <div className="max-w-6xl mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold text-gray-800 mb-6">
                    Course Analytics Dashboard
                </h2>

                {analytics && (
                    <>
                        {/* Metric Cards */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                            <div className="bg-white rounded-xl shadow-sm p-6">
                                <p className="text-sm text-gray-500 mb-1">
                                    Total Learners
                                </p>
                                <p className="text-4xl font-bold text-indigo-600">
                                    {analytics.totalLearners}
                                </p>
                            </div>
                            <div className="bg-white rounded-xl shadow-sm p-6">
                                <p className="text-sm text-gray-500 mb-1">
                                    Completion Rate
                                </p>
                                <p className="text-4xl font-bold text-green-600">
                                    {analytics.completionRate}%
                                </p>
                            </div>
                            <div className="bg-white rounded-xl shadow-sm p-6">
                                <p className="text-sm text-gray-500 mb-1">
                                    Drop-off Module
                                </p>
                                <p className="text-lg font-bold text-red-500">
                                    {analytics.dropOffModuleTitle}
                                </p>
                                <p className="text-sm text-gray-400">
                                    {analytics.dropOffCount} learners stopped here
                                </p>
                            </div>
                        </div>

                        {/* Learner Health Score Table */}
                        <div className="bg-white rounded-xl shadow-sm p-6">
                            <h3 className="text-lg font-semibold text-gray-700 mb-4">
                                Learner Health Scores
                            </h3>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm">
                                    <thead>
                                        <tr className="border-b border-gray-100">
                                            <th className="text-left py-3 px-4
                                                         text-gray-500 font-medium">
                                                Learner
                                            </th>
                                            <th className="text-left py-3 px-4
                                                         text-gray-500 font-medium">
                                                Progress
                                            </th>
                                            <th className="text-left py-3 px-4
                                                         text-gray-500 font-medium">
                                                Health Score
                                            </th>
                                            <th className="text-left py-3 px-4
                                                         text-gray-500 font-medium">
                                                Status
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {analytics.learnerHealthScores?.map(learner => (
                                            <tr key={learner.userId}
                                                className="border-b border-gray-50
                                                         hover:bg-gray-50">
                                                <td className="py-3 px-4">
                                                    <p className="font-medium
                                                               text-gray-800">
                                                        {learner.learnerName}
                                                    </p>
                                                    <p className="text-gray-400 text-xs">
                                                        {learner.learnerEmail}
                                                    </p>
                                                </td>
                                                <td className="py-3 px-4">
                                                    <div className="flex items-center gap-2">
                                                        <div className="w-24 bg-gray-200
                                                                      rounded-full h-2">
                                                            <div
                                                                className="bg-indigo-500 h-2
                                                                         rounded-full"
                                                                style={{
                                                                    width: `${learner.completionPercentage}%`
                                                                }}
                                                            />
                                                        </div>
                                                        <span className="text-gray-600">
                                                            {learner.completionPercentage}%
                                                        </span>
                                                    </div>
                                                </td>
                                                <td className="py-3 px-4 font-semibold
                                                             text-gray-700">
                                                    {learner.healthScore}
                                                </td>
                                                <td className="py-3 px-4">
                                                    <span className={`px-3 py-1 rounded-full
                                                        text-xs font-medium
                                                        ${getHealthColor(learner.healthStatus)}`}>
                                                        {learner.healthStatus}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default AdminDashboard;