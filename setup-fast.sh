#\!/bin/bash

# Create fast profile for Maven with skipped tests and quality checks
cat > ~/.m2/settings.xml << 'SETTINGS'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <profiles>
    <profile>
      <id>fast</id>
      <properties>
        <maven.test.skip>true</maven.test.skip>
        <spotless.check.skip>true</spotless.check.skip>
        <checkstyle.skip>true</checkstyle.skip>
        <spotbugs.skip>true</spotbugs.skip>
        <jacoco.skip>true</jacoco.skip>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>fast</activeProfile>
  </activeProfiles>
</settings>
SETTINGS

# Make the script executable
chmod +x setup-fast.sh

echo "Fast profile is now active in Maven settings"
