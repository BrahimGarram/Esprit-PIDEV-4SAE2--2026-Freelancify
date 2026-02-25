@echo off
echo =====================================================
echo Git Push to collaboration-service Branch
echo =====================================================

echo.
echo Current status:
git status

echo.
echo =====================================================
echo STEP 1: Stage all changes
echo =====================================================
git add .

echo.
echo =====================================================
echo STEP 2: Commit changes
echo =====================================================
set /p commit_message="Enter commit message (or press Enter for default): "
if "%commit_message%"=="" (
    git commit -m "feat: workspace collaboration features - dashboard, milestones, sprints, tasks, kanban, team management, and UI improvements"
) else (
    git commit -m "%commit_message%"
)

echo.
echo =====================================================
echo STEP 3: Switch to collaboration-service branch
echo =====================================================
git checkout collaboration-service 2>nul
if %errorlevel% neq 0 (
    echo Branch doesn't exist locally, creating it...
    git checkout -b collaboration-service
)

echo.
echo =====================================================
echo STEP 4: Push to remote
echo =====================================================
git push -u origin collaboration-service

echo.
echo =====================================================
echo SUCCESS! Changes pushed to collaboration-service branch
echo =====================================================
echo.
echo View your branch at:
echo https://github.com/BrahimGarram/freelance-management-platform/tree/collaboration-service
echo.
pause
