const ProgressBar = ({ percentage, completedModules, totalModules }) => {
    return (
        <div className="w-full">
            <div className="flex justify-between mb-1">
                <span className="text-sm font-medium text-indigo-600">
                    {completedModules} / {totalModules} modules completed
                </span>
                <span className="text-sm font-medium text-indigo-600">
                    {percentage}%
                </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
                <div
                    className="bg-indigo-600 h-3 rounded-full transition-all duration-500"
                    style={{ width: `${percentage}%` }}
                />
            </div>
        </div>
    );
};

export default ProgressBar;