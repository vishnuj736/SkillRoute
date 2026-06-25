import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import ProgressBar from '../components/ProgressBar';
import api from '../api/axiosConfig';

const COURSE_ID = 1;
const POLL_INTERVAL = 10000; // 10 seconds

const LearnerDashboard = () => {
    const { logout } = useAuth();
    const [progress, setProgress] = useState(null);
    const [learningPath, setLearningPath] = useState(null);
    const [loading, setLoading] = useState(true);
    const [generating, setGenerating] = useState(false);
    const [completing, setCompleting] = useState(null);

    // Fetch progress on mount and poll every 10s
    useEffect(() => {
        fetchProgress();
        fetchLearningPath();

        const interval = setInterval(fetchProgress, POLL_INTERVAL);
        return () => clearInterval(interval);
    }, []);

    const fetchProgress = async () => {
        try {
            const res = await api.get(
                `/api/learner/progress/course/${COURSE_ID}`);
            setProgress(res.data);
        } catch (err) {
            console.error('Failed to fetch progress', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchLearningPath = async () => {
        try {
            const res = await api.get(
                `/api/learner/learning-path/view/${COURSE_ID}`);
            if (res.data?.pathJson) {
                setLearningPath(JSON.parse(res.data.pathJson));
            }
        } catch (err) {
            // Path not generated yet — that's fine
        }
    };

    const generatePath = async () => {
        setGenerating(true);
        try {
            const res = await api.post(
                `/api/learner/learning-path/generate/${COURSE_ID}`);
            if (res.data?.pathJson) {
                setLearningPath(JSON.parse(res.data.pathJson));
            }
        } catch (err) {
            alert('Please complete the assessment first');
        } finally {
            setGenerating(false);
        }
    };

    const markComplete = async (moduleId) => {
        setCompleting(moduleId);
        try {
            const res = await api.post(
                `/api/learner/progress/complete/${moduleId}`);
            setProgress(res.data);
        } catch (err) {
            console.error('Failed to mark complete', err);
        } finally {
            setCompleting(null);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-indigo-600 text-lg">Loading...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <nav className="bg-indigo-600 text-white px-6 py-4 flex
                           justify-between items-center">
                <h1 className="text-xl font-bold">SkillRoute</h1>
                <button
                    onClick={logout}
                    className="text-sm bg-indigo-700 px-4 py-2 rounded-lg
                             hover:bg-indigo-800 transition-colors"
                >
                    Logout
                </button>
            </nav>

            <div className="max-w-4xl mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold text-gray-800 mb-6">
                    My Learning Dashboard
                </h2>

                {/* Progress Card */}
                {progress && (
                    <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
                        <h3 className="text-lg font-semibold text-gray-700 mb-4">
                            Course Progress
                        </h3>
                        <ProgressBar
                            percentage={progress.completionPercentage}
                            completedModules={progress.completedModules}
                            totalModules={progress.totalModules}
                        />
                        <p className="text-xs text-gray-400 mt-2">
                            Updates every 10 seconds automatically
                        </p>
                    </div>
                )}

                {/* Generate Path Button */}
                {!learningPath && (
                    <div className="bg-indigo-50 rounded-xl p-6 mb-6
                                  border border-indigo-100 text-center">
                        <p className="text-gray-600 mb-4">
                            Generate your personalized learning path based
                            on your assessment results.
                        </p>
                        <button
                            onClick={generatePath}
                            disabled={generating}
                            className="bg-indigo-600 text-white px-6 py-2
                                     rounded-lg font-medium hover:bg-indigo-700
                                     transition-colors disabled:opacity-50"
                        >
                            {generating ? 'Generating...' : 'Generate My Path'}
                        </button>
                    </div>
                )}

                {/* Personalized Module List */}
                {learningPath && (
                    <div className="bg-white rounded-xl shadow-sm p-6">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-lg font-semibold text-gray-700">
                                Your Personalized Path
                            </h3>
                            <span className="text-xs bg-indigo-100 text-indigo-600
                                           px-3 py-1 rounded-full font-medium">
                                {learningPath.algorithm}
                            </span>
                        </div>
                        <div className="space-y-3">
                            {learningPath.path?.map((module, index) => (
                                <div key={module.moduleId}
                                     className="flex items-center justify-between
                                               p-4 border border-gray-100 rounded-lg
                                               hover:border-indigo-200 transition-colors">
                                    <div className="flex items-center gap-3">
                                        <span className="w-8 h-8 bg-indigo-100
                                                       text-indigo-600 rounded-full
                                                       flex items-center justify-center
                                                       text-sm font-bold">
                                            {module.order}
                                        </span>
                                        <div>
                                            <p className="font-medium text-gray-800">
                                                {module.title}
                                            </p>
                                            <p className="text-xs text-gray-500">
                                                {module.reason}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <span className={`text-xs px-2 py-1 rounded-full
                                            ${module.difficultyTag === 'Beginner'
                                                ? 'bg-green-100 text-green-600'
                                                : module.difficultyTag === 'Intermediate'
                                                ? 'bg-yellow-100 text-yellow-600'
                                                : 'bg-red-100 text-red-600'}`}>
                                            {module.difficultyTag}
                                        </span>
                                        <button
                                            onClick={() => markComplete(module.moduleId)}
                                            disabled={completing === module.moduleId}
                                            className="text-xs bg-green-500 text-white
                                                     px-3 py-1 rounded-lg hover:bg-green-600
                                                     transition-colors disabled:opacity-50"
                                        >
                                            {completing === module.moduleId
                                                ? '...' : '✓ Complete'}
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default LearnerDashboard;