@echo off
:: Script to optimize build performance on Windows
:: Uses optimal settings for CI/CD and local development

echo Running ATL tests with optimized settings
echo.

:: Set CPU cores based on available processors
for /f "tokens=2 delims==" %%a in ('wmic cpu get NumberOfCores /value') do set CPU_CORES=%%a
set /a PARALLELLISM=%CPU_CORES%-2
if %PARALLELLISM% LSS 1 set PARALLELLISM=1

echo CPU Cores: %CPU_CORES%
echo Parallelism: %PARALLELLISM%
echo.

:: Set memory allocation based on available memory
set MAVEN_XMX=2048m

:: Run build with optimized settings
set MAVEN_OPTS=-Xmx%MAVEN_XMX% -XX:+UseG1GC -XX:+TieredCompilation -XX:TieredStopAtLevel=1

mvn clean test -P atl-tests -P skip-quality-checks ^
    -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true ^
    -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false ^
    -Dcucumber.execution.parallel.config.fixed.parallelism=%PARALLELLISM% ^
    -Dcucumber.execution.parallel.config.fixed.max-pool-size=%CPU_CORES% ^
    -T %CPU_CORES% ^
    -Dmaven.artifact.threads=%PARALLELLISM%

echo.
echo Build completed.
echo Test reports available at:
echo - HTML Report: target/cucumber-reports/cucumber.html
echo - JSON Report: target/cucumber-reports/cucumber.json
echo - XML Report: target/cucumber-reports/cucumber.xml

pause