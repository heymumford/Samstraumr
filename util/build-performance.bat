@echo off
:: Filename: build-performance.bat
:: Purpose: Windows-specific script that optimizes Maven build performance based on system resources.
:: Goals:
::   - Ensure that build performance is optimized for Windows environments
::   - Ensure that system resources (CPU cores, memory) are detected and utilized optimally
::   - Ensure that test runs are configured for parallel execution where possible
:: Dependencies:
::   - Windows Management Instrumentation Command-line (WMIC)
::   - Maven build system with appropriate profiles
::   - Windows command shell environment
:: Assumptions:
::   - Windows environment with WMIC available
::   - Maven is installed and accessible on the system PATH
::   - System has adequate resources for running the build

echo ====== Samstraumr Performance Build ======
echo.

:: Set CPU cores based on available processors
for /f "tokens=2 delims==" %%a in ('wmic cpu get NumberOfLogicalProcessors /value') do set LOGICAL_CORES=%%a
for /f "tokens=2 delims==" %%a in ('wmic cpu get NumberOfCores /value') do set PHYSICAL_CORES=%%a
for /f "tokens=2 delims==" %%a in ('wmic ComputerSystem get TotalPhysicalMemory /value') do set TOTAL_MEMORY_BYTES=%%a

:: Calculate memory in MB
set /a MEMORY_MB=%TOTAL_MEMORY_BYTES:~0,-6%
:: Set thread count to logical cores minus 1
set /a THREAD_COUNT=%LOGICAL_CORES%-1
if %THREAD_COUNT% LSS 1 set THREAD_COUNT=1
:: Set parallel count to 75% of logical cores
set /a PARALLEL_COUNT=%LOGICAL_CORES%*75/100
if %PARALLEL_COUNT% LSS 1 set PARALLEL_COUNT=1
:: Set Maven memory to 40% of total memory, with a cap of 4GB
set /a MAVEN_MEMORY_MB=%MEMORY_MB%*40/100
if %MAVEN_MEMORY_MB% GTR 4096 set MAVEN_MEMORY_MB=4096
if %MAVEN_MEMORY_MB% LSS 1024 set MAVEN_MEMORY_MB=1024

echo System Information:
echo - Physical CPU Cores: %PHYSICAL_CORES%
echo - Logical CPU Cores: %LOGICAL_CORES%
echo - Total Memory: %MEMORY_MB%MB
echo - Maven Memory: %MAVEN_MEMORY_MB%MB
echo - Thread Count: %THREAD_COUNT%
echo - Parallel Count: %PARALLEL_COUNT%
echo.

:: Set up MAVEN_OPTS for optimal performance
set MAVEN_OPTS=-Xmx%MAVEN_MEMORY_MB%m -XX:+UseG1GC -XX:+TieredCompilation

echo Running ATL tests with optimized settings
echo.

:: Execute Maven with optimal settings
mvn clean test -P atl-tests -P skip-quality-checks ^
    -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true ^
    -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false ^
    -Dcucumber.execution.parallel.config.dynamic.factor=0.75 ^
    -T %THREAD_COUNT% ^
    -Dmaven.artifact.threads=%THREAD_COUNT%

echo.
echo Build completed.
echo Test reports available at:
echo - HTML Report: target/cucumber-reports/cucumber.html
echo - JSON Report: target/cucumber-reports/cucumber.json
echo - XML Report: target/cucumber-reports/cucumber.xml

pause