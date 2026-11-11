# ARIA

**Academic Reminder & Information Assistant** is a native Android task-management app designed to help students organize coursework, track progress, and build productive habits.

ARIA provides a distraction-free workflow for creating tasks, prioritizing deadlines, reviewing completed work, and protecting access with a local account and PIN.

## Download and install the app

The latest published APK is available from the [`apkrelease` GitHub release](https://github.com/mshdptts/appdevproject/releases/tag/apkrelease).

**[Download ARIA.V1.5.0.apk](ARIA.V1.5.0.SDK33.apk)**

### Install on an Android device

1. Open the download link on an Android phone or tablet.
2. Download `ARIA.V1.5.0.apk`.
3. Open the downloaded APK.
4. If Android asks for permission, allow the browser or file manager to **install unknown apps**.
5. Confirm the installation and open **ARIA**.

For security, disable the “install unknown apps” permission again after installation if you do not need it. Android 15 or newer is required for the current release.

### Verify the APK

The published APK SHA-256 checksum is:

```text
91304c8943f3100a056db562c0d54cb11434c94b63bdcd08810c00caf08d734
```

On Linux or macOS, verify a downloaded file with:

```bash
sha256sum ARIA.V1.5.0.apk
```

On Windows PowerShell:

```powershell
Get-FileHash .\ARIA.V1.5.0.apk -Algorithm SHA256
```

## Features

### Account and access

- Create a local account with a username, password, and security question.
- Set a six-digit PIN for faster access after the first login.
- Unlock the app with the PIN on returning visits.
- Reset a forgotten PIN using the account’s security question.
- Lock the app from the profile screen.
- Reset the local account and its stored data from the profile screen.

### Task management

- Create tasks with a title, description, due date, and priority.
- Use Low, Medium, and Urgent priority levels.
- View tasks from the home dashboard or the dedicated task list.
- Search and filter tasks by status.
- Open a task to view its complete details.
- Edit tasks and mark them complete.
- Keep completed-task history even when a completed task is deleted.

### Progress tracking

- See current completion streaks.
- Review weekly and all-time completion totals.
- Browse a calendar of task activity.
- View upcoming due dates and completed dates.
- Receive motivational dashboard content based on workload.

## Requirements

### To install the published APK

- Android 15 (API 35) or newer.
- Permission to install APKs from the browser or file manager used for the download.
- Approximately 60 MB of free storage for the current APK.

### To build from source

- Android Studio with Android SDK support.
- JDK 11 or newer.
- Android SDK platforms and build tools for API 37.
- Internet access on the first build so Gradle can download dependencies.

The project includes the Gradle wrapper, so a separate Gradle installation is not required.

## Build from source

Clone the repository:

```bash
git clone https://github.com/mshdptts/appdevproject.git
cd appdevproject
```

Build the debug APK:

```bash
./gradlew assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

On Windows, use:

```powershell
.\gradlew.bat assembleDebug
```

Install the debug build on a connected device or emulator with Android Debug Bridge:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

To run the unit and instrumented test tasks:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

`connectedAndroidTest` requires a running emulator or a connected Android device with USB debugging enabled.

## Open in Android Studio

1. Open Android Studio.
2. Select **Open** and choose the cloned repository directory.
3. Allow Android Studio to sync the Gradle project.
4. Select the `app` run configuration.
5. Start an Android 15 or newer emulator, or connect a compatible device.
6. Click **Run**.

## Project structure

```text
.
├── app/
│   └── src/
│       ├── androidTest/       Instrumented Android tests
│       ├── main/
│       │   ├── java/.../
│       │   │   ├── data/      SQLite models, session, and database access
│       │   │   ├── navigation/ Navigation routes
│       │   │   ├── ui/        Compose screens, components, and theme
│       │   │   ├── utils/     Shared utility functions
│       │   │   └── viewmodel/ Screen state and database operations
│       │   └── res/           Android resources and app branding
│       └── test/              Local unit tests
├── gradle/
│   ├── libs.versions.toml     Dependency and plugin versions
│   └── wrapper/               Gradle wrapper configuration
└── README.md
```

## Technical overview

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Architecture:** Compose screens with ViewModels and a local data layer
- **Persistence:** SQLite through `SQLiteOpenHelper`
- **Application ID:** `com.example.studentaskmanager`
- **Namespace:** `com.example.studentaskmanager`
- **Minimum SDK:** 35
- **Target and compile SDK:** 37
- **Java compatibility:** Java 11
- **Gradle:** 9.5.0

All task, account, PIN, and completion-history data is stored locally on the device. ARIA does not provide cloud synchronization or server-side account recovery. Clearing app data or using **Reset Account** removes the local account and its locally stored data.

## First-use workflow

1. Launch ARIA and create a local account.
2. Choose a password and security question.
3. Set a six-digit PIN when prompted.
4. Add tasks with due dates and priorities.
5. Mark tasks complete from the home or task list screens.
6. Review streaks, weekly totals, and calendar history under **Progress**.
7. Use **Profile** to lock the app, change the PIN, or reset the local account.

## Release information

| Item | Value |
| --- | --- |
| Release tag | [`apkrelease`](https://github.com/mshdptts/appdevproject/releases/tag/apkrelease) |
| APK | `ARIA.V1.5.0.apk` |
| Release APK size | Approximately 60 MB |
| Published version label | ARIA v1.0.0 |
| Release date | November 2026 |

Planned improvements include push notifications for due dates, profile customization, and dark mode.

## License

No license has been specified for this repository yet. Until a license is added, all rights are reserved by the copyright holder.
