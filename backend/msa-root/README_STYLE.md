Google Java style automation

This repository uses Spotless with google-java-format to apply the Google Java style across all subprojects.

Files added
- config/idea/intellij-java-google-style.xml - IntelliJ code style XML (for manual IDE import).

How to run (Windows, cmd.exe)

1. Create a branch for the style changes:
   git checkout -b style/spotless-google-format

2. Apply formatting to all subprojects:
   .\gradlew.bat spotlessApply

3. Check formatting (CI should run this before merging):
   .\gradlew.bat spotlessCheck

4. Build the project to ensure no compilation issues:
   .\gradlew.bat build

5. Commit changes:
   git add -A
   git commit -m "style: apply Google Java style via Spotless (google-java-format)"
   git push -u origin HEAD

Notes and limitations
- google-java-format is opinionated and may not match every setting from the IntelliJ XML exactly. The IntelliJ XML is provided for developers to import into the IDE for parity.
- If you require exact parity with IntelliJ formatting, consider using an Eclipse formatter profile with Spotless or keep the XML as an IDE-only requirement.
