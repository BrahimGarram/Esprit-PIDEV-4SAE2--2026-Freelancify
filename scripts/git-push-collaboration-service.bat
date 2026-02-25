@echo off
echo =====================================================
echo Pushing to collaboration-service branch
echo =====================================================

echo.
echo Step 1: Checking current branch...
git branch --show-current

echo.
echo Step 2: Checking git status...
git status

echo.
echo Step 3: Adding all changes...
git add .

echo.
echo Step 4: Committing changes...
git commit -m "feat: Add workspace collaboration features with dashboard, milestones, sprints, tasks, and team management"

echo.
echo Step 5: Checking if collaboration-service branch exists...
git rev-parse --verify collaboration-service >nul 2>&1
if %errorlevel% equ 0 (
    echo Branch exists, switching to it...
    git checkout collaboration-service
) else (
    echo Branch doesn't exist, creating it...
    git checkout -b collaboration-service
)

echo.
echo Step 6: Merging changes from current branch...
git merge - --no-ff -m "Merge workspace collaboration features"

echo.
echo Step 7: Pushing to remote...
git push origin collaboration-service

echo.
echo =====================================================
echo Push completed!
echo =====================================================
echo.
pause
