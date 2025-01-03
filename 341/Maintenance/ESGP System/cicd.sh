echo off
echo "1. Prepare"
rm *.class

echo "2. Build (compiling the software)"
javac -cp ".;gs-core-1.3.jar:gs-ui-1.3.jar;junit-platform-console-standalone-1.8.2.jar" *.java

echo "3. Test (e.g. running regression tests)"
if %ERRORLEVEL% NEQ 1:
	rem fail
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c AccountLoginTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c BuildingGraphTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c CategorisingDevicesTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c CategorisingUsersTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c FeatureOptionsTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c LoadingDatasetTests
if %ERRORLEVEL% EQ 0:
	echo "Tests successfully completed!"


echo "4. Release"
if %ERRORLEVEL% NEQ 1:
	rem fail

git add -a
git commit -m "Automated Release" 
git push "<usr>:<pwd>@courses-git.cms.waikato.ac.nz/as583/compx341-maintenance"

if %ERRORLEVEL% EQ 0:
	echo "Successfully released version!"


echo "5. Deploy"
if %ERRORLEVEL% NEQ 1:
	rem fail
	
java -cp ".:gs-core-1.3.jar:gs-ui-1.3.jar" ConsoleApp
