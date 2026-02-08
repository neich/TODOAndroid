# Android Application Development — AI Agent Instructions

> Technical guide for Copilot / Claude AI agents developing an Android application.
> Derived from the PDS course Android Development reference architecture (2026).

---

## 1. Mandatory Constraints

| Constraint | Requirement |
|-----------|------------|
| **Language** | **Java 8+** with desugaring. **NO Kotlin, NO Jetpack Compose, NO KMP.** |
| **Base class** | Always extend `AppCompatActivity` (never `android.app.Activity`) |
| **Libraries** | AndroidX / Jetpack only (never legacy Support Library) |
| **Architecture** | MVVM + Single-Activity + Repository pattern |
| **minSdk** | API 24 (Android 7.0) — covers 98%+ devices |
| **targetSdk** | API 35+ (Google Play requirement since Aug 2025) |
| **compileSdk** | API 36 (Android 16, latest stable) |

---

## 2. Architecture Overview

### 2.1 MVVM — Model-View-ViewModel

Google's recommended architecture. Strict separation of concerns across three layers:

```
┌─────────────┐     user action      ┌─────────────┐     request data    ┌─────────────┐
│    View      │ ──────────────────>  │  ViewModel   │ ─────────────────> │    Model     │
│  (Fragment)  │ <──────────────────  │  (LiveData)  │ <───────────────── │ (Repository) │
└─────────────┘     observe state     └─────────────┘     return data     └─────────────┘
```

**Unidirectional data flow**: State flows down (ViewModel → UI), events flow up (UI → ViewModel).

### 2.2 Layer Responsibilities

| Layer | Component | Responsibility | Rules |
|-------|-----------|---------------|-------|
| **UI** | Fragment / Activity | Render UI, capture user input, observe LiveData | No business logic. No direct data access. |
| **State** | ViewModel | Hold UI state, expose via LiveData, call Repository | Never import `android.view.*` or `android.widget.*`. Never hold Context/View references. |
| **Data** | Repository | Decide data source (cache, Room, network), handle threading | Single source of truth. Interchangeable data sources. |
| **Persistence** | Room DAO / DataStore | Execute queries, store data | Annotated interfaces. No UI awareness. |
| **Network** | Retrofit Service | API calls | Return data to Repository. |

### 2.3 Single-Activity Architecture

- **One Activity** (`MainActivity` extending `AppCompatActivity`) hosting all Fragments.
- `FragmentContainerView` in the Activity layout as the Fragment host.
- **Navigation Component** manages all Fragment transactions and back stack.
- **No manual** `FragmentManager.beginTransaction()` needed.
- Safe Args plugin for type-safe argument passing between Fragments.

---

## 3. Jetpack Components (Java)

### 3.1 View Binding

**Mandatory** for all view access. Replaces `findViewById()`.

```java
// Enable in build.gradle
android {
    buildFeatures {
        viewBinding = true
    }
}
```

**Activity usage:**
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textTitle.setText("Hello");
        binding.buttonSubmit.setOnClickListener(v -> handleSubmit());
    }
}
```

**Fragment usage:**
```java
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setup UI, observers, listeners here
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
```

### 3.2 ViewModel

Holds UI state, survives configuration changes (rotation, language).

```java
public class UserViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<List<User>> getUsers() { return users; }

    public void loadUsers() {
        repository.fetchUsers(result -> users.postValue(result));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Release resources (executors, connections)
    }
}
```

**Obtain in Fragment:**
```java
UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
// Shared (Activity-scoped):
UserViewModel shared = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
```

**ViewModel rules:**
- Never pass `Context`, `View`, `Activity`, or `Fragment` references.
- Use `AndroidViewModel` only if `Application` context is needed.
- One ViewModel per screen (Fragment). Use Activity-scoped ViewModel for cross-Fragment communication.
- Override `onCleared()` to shut down ExecutorService, close connections.
- Does **not** survive process death — use `SavedStateHandle` for critical UI state.

### 3.3 LiveData

Lifecycle-aware observable data holder. The reactive bridge between ViewModel and UI.

```java
// In ViewModel — encapsulation pattern
private final MutableLiveData<String> userName = new MutableLiveData<>();
public LiveData<String> getUserName() { return userName; }

// Writing (ViewModel/Repository)
userName.setValue("Alice");       // Main thread only
userName.postValue("Alice");     // Any thread (background-safe)

// Observing (Fragment — in onViewCreated)
viewModel.getUserName().observe(getViewLifecycleOwner(),
    name -> binding.textName.setText(name));
```

**Critical rules:**
- Always use `getViewLifecycleOwner()` in Fragments (not `this`) to prevent duplicate observers.
- Observe in `onViewCreated()`, not `onCreate()` or `onCreateView()`.
- `MutableLiveData` stays **private** inside ViewModel; expose as `LiveData` (read-only).
- `postValue()` coalesces rapid calls — only the last value is delivered.

**Transformations:**
```java
LiveData<String> displayName = Transformations.map(userLiveData,
    user -> user.getFirstName() + " " + user.getLastName());

LiveData<List<Task>> tasks = Transformations.switchMap(selectedUserId,
    id -> repository.getTasksForUser(id));
```

### 3.4 Room (SQLite ORM)

Compile-time verified SQL. Three components: `@Entity`, `@Dao`, `@Database`.

**Entity:**
```java
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_name")
    public String name;

    public String email;
}
```

**DAO:**
```java
@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY user_name ASC")
    LiveData<List<User>> getAllUsers();  // Observable — auto-updates UI

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);  // Must run off main thread

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(int userId);
}
```

**Database:**
```java
@Database(entities = {User.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class, "app_database")
                        .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

**Threading:** Room forbids main-thread queries. Use `ExecutorService` in the Repository for insert/update/delete. `LiveData` return types are handled automatically by Room.

**Auto-migrations (Room 2.4+):**
```java
@Database(
    entities = {User.class},
    version = 2,
    autoMigrations = { @AutoMigration(from = 1, to = 2) }
)
```

### 3.5 Navigation Component

Declarative Fragment navigation via XML graph. Replaces manual FragmentManager.

**Setup (build.gradle):**
```groovy
dependencies {
    implementation "androidx.navigation:navigation-fragment:2.8.x"
    implementation "androidx.navigation:navigation-ui:2.8.x"
}
// Safe Args plugin
plugins {
    id "androidx.navigation.safeargs"
}
```

**Activity layout:**
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

**Navigate from Fragment:**
```java
NavController navController = Navigation.findNavController(view);
navController.navigate(R.id.action_home_to_detail);

// With Safe Args:
HomeFragmentDirections.ActionHomeToDetail action =
    HomeFragmentDirections.actionHomeToDetail(userId);
navController.navigate(action);
```

### 3.6 WorkManager (Background Tasks)

For deferrable, guaranteed background work. Survives app restarts. Replaces Services, JobScheduler, AlarmManager for most use cases.

```java
OneTimeWorkRequest uploadWork = new OneTimeWorkRequest.Builder(UploadWorker.class)
    .setConstraints(new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build())
    .build();

WorkManager.getInstance(context).enqueue(uploadWork);
```

---

## 4. UI Guidelines

### 4.1 Layout System

| Guideline | Requirement |
|-----------|------------|
| **Root layout** | `ConstraintLayout` (flat hierarchy, performant) |
| **Lists** | Always `RecyclerView` (never `ListView`) |
| **Fragment host** | `FragmentContainerView` (not `<fragment>` tag) |
| **Units** | `dp` for dimensions, `sp` for text sizes |
| **Sizing** | `wrap_content`, `match_parent`, or fixed `dp` |
| **Strings** | Always in `res/values/strings.xml` (never hardcoded) |

### 4.2 Material Design 3 Widgets

Use Material Design 3 components from `com.google.android.material`:

| Category | Use | Avoid |
|----------|-----|-------|
| **Text input** | `TextInputLayout` + `TextInputEditText` | Plain `EditText` |
| **Buttons** | `MaterialButton` | Plain `Button` |
| **Cards** | `MaterialCardView` | Plain `CardView` |
| **Switches** | `SwitchMaterial` | Plain `Switch` |
| **Toolbar** | `MaterialToolbar` | Legacy `ActionBar` |

### 4.3 ConstraintLayout Features

- **Chains**: Distribute Views evenly (`spread`, `packed`, `weighted`).
- **Guidelines**: Invisible reference lines (%, dp) for alignment.
- **Barriers**: Dynamic edges based on sibling dimensions.
- **Flow**: Virtual layout for wrapping/grid arrangements.
- **Groups**: Control visibility of multiple Views at once.
- Every View needs ≥ 1 horizontal + 1 vertical constraint.

### 4.4 Resource Qualifiers

Provide layout variants for different configurations:

| Qualifier | Description | Example folder |
|-----------|-------------|---------------|
| Landscape | Rotated device | `res/layout-land/` |
| Tablet | Screen width ≥ 600dp | `res/layout-sw600dp/` |
| Dark mode | Night theme | `res/layout-night/` |
| API level | Version-specific | `res/layout-v26/` |

---

## 5. Event Handling

### 5.1 Listeners (Java 8 Lambdas)

```java
// Click
binding.button.setOnClickListener(v -> doAction());

// Long click
binding.button.setOnLongClickListener(v -> { doAction(); return true; });

// Text change
binding.editText.addTextChangedListener(new TextWatcher() { ... });

// Focus change
binding.editText.setOnFocusChangeListener((v, hasFocus) -> { ... });
```

**Rules:**
- Always set listeners programmatically via View Binding.
- **Never** use `android:onClick` in XML (not type-safe, breaks with Fragments/ProGuard).
- Use Java 8 lambdas for concise syntax (enable via desugaring).

### 5.2 Modern APIs (AndroidX)

| Old API (Deprecated) | Modern Replacement |
|----------------------|-------------------|
| `onBackPressed()` | `OnBackPressedDispatcher` (register callbacks with lifecycle awareness) |
| `onCreateOptionsMenu()` | `MenuProvider` via `addMenuProvider()` (lifecycle-aware) |
| `startActivityForResult()` | Activity Result API (`registerForActivityResult()`) |

---

## 6. Threading

### 6.1 Main Thread Rule

**NEVER** perform these on the main thread:
- Network calls
- Database queries (except Room LiveData returns)
- Heavy computation
- File I/O

Blocking the main thread causes ANR (Application Not Responding) after ~5 seconds.

### 6.2 ExecutorService (Java)

The standard threading solution. Replaces deprecated `AsyncTask`.

```java
// In Repository or ViewModel
private final ExecutorService executor = Executors.newSingleThreadExecutor();

public void insertUser(User user) {
    executor.execute(() -> {
        dao.insert(user);
        // For UI updates:
        // runOnUiThread(() -> showSuccess());
    });
}

// Shutdown in onCleared() or onDestroy()
executor.shutdown();
```

**Thread pool options:**
| Method | Use case |
|--------|----------|
| `newSingleThreadExecutor()` | Sequential background work |
| `newFixedThreadPool(n)` | Parallel tasks with controlled concurrency |
| `newCachedThreadPool()` | Many short-lived tasks |

### 6.3 Threading by Component

| Component | Threading |
|-----------|----------|
| **ViewModel** | Delegates to Repository. No direct threading. |
| **Repository** | `ExecutorService` for write operations (insert/update/delete). |
| **Room LiveData queries** | Handled automatically by Room. |
| **Network (Retrofit)** | Uses its own background thread. Callback on main thread. |
| **WorkManager** | Runs on background thread automatically. |
| **UI updates from background** | `runOnUiThread()` or `Handler(Looper.getMainLooper())` |

---

## 7. Data & Storage

### 7.1 Storage Decision Guide

| Data type | Solution |
|-----------|----------|
| User preferences / settings | **DataStore** (Preferences DataStore) |
| Structured / relational data | **Room** (SQLite ORM) |
| App-private files | Internal storage (`getFilesDir()`) |
| User-visible media | Scoped storage / `MediaStore` API |
| Temporary/cache | `getCacheDir()` |

### 7.2 Room Reactive Data Flow

Room's killer feature: LiveData queries **automatically re-execute** when the underlying table changes.

```
Any Component (Service, WorkManager, etc.)
    │
    ▼  insert/update/delete via ExecutorService
┌───────────┐
│  Room DB   │──── InvalidationTracker detects change
└───────────┘
    │
    ▼  LiveData re-queries automatically
┌───────────┐
│ ViewModel  │──── LiveData emits updated list
└───────────┘
    │
    ▼  Observer fires on main thread
┌───────────┐
│  Fragment  │──── RecyclerView updates
└───────────┘
```

**Key insight:** Components are **fully decoupled**. A Service writing to Room and a Fragment displaying data never reference each other. Room + LiveData act as the reactive bridge.

---

## 8. Services (Limited Use)

### 8.1 When to Use Services

**Use Foreground Service ONLY for:**
- Music/media playback
- Real-time location tracking (GPS)
- Active VoIP calls
- Ongoing file transfers the user is aware of

**DO NOT use Services for:**
- Network requests → Retrofit + ExecutorService
- Periodic sync → WorkManager
- One-time background tasks → WorkManager
- Short async operations → ExecutorService in Repository

### 8.2 Foreground Service Requirements (API 34+)

- Must display persistent notification (API 26+).
- Must declare `foregroundServiceType` in manifest (API 34+).
- Call `startForeground()` within 5 seconds of `startForegroundService()`.
- Declare `FOREGROUND_SERVICE` permission + type-specific permissions.

---

## 9. Context Usage

| Context type | Use for | Access |
|-------------|---------|--------|
| **Application** | Databases, repositories, DI, analytics | `getApplicationContext()` |
| **Activity** | UI operations, dialogs, themes, layout inflation | `this` (in Activity), `requireActivity()` (in Fragment) |

**Memory leak prevention:**
- **NEVER** store Activity Context in ViewModels, Repositories, static fields, or long-lived objects.
- Use Application Context for anything that outlives an Activity.
- ViewModel should not hold any Context reference (use `AndroidViewModel` only if `Application` context is strictly needed).

---

## 10. Manifest Requirements (2026)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".AppApplication"
        android:theme="@style/Theme.MyApp"
        android:supportsRtl="true"
        android:allowBackup="true">

        <!-- Single Activity -->
        <activity
            android:name=".ui.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Services must declare exported + foregroundServiceType -->
        <service
            android:name=".services.MusicService"
            android:exported="false"
            android:foregroundServiceType="mediaPlayback" />
    </application>
</manifest>
```

**Key rules:**
- `android:exported` is **mandatory** since API 31 for components with intent-filters.
- `foregroundServiceType` is **mandatory** since API 34 for foreground services.
- Use `SplashScreen` API (AndroidX) instead of custom splash Activities (deprecated API 31).
- `usesCleartextTraffic="true"` only for development (HTTP). Production must use HTTPS.

---

## 11. Intents & Navigation

### 11.1 In-App Navigation

Use **Navigation Component** exclusively. No `startActivity()` for in-app screens.

```java
// Navigate with Safe Args
HomeFragmentDirections.ActionHomeToDetail action =
    HomeFragmentDirections.actionHomeToDetail(userId, userName);
Navigation.findNavController(view).navigate(action);
```

### 11.2 External Intents

```java
// Open URL
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"));
if (intent.resolveActivity(getPackageManager()) != null) {
    startActivity(intent);
}

// Share text
Intent shareIntent = new Intent(Intent.ACTION_SEND);
shareIntent.setType("text/plain");
shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this out!");
startActivity(Intent.createChooser(shareIntent, "Share via"));
```

### 11.3 Receiving Results

Use **Activity Result API** (replaces deprecated `startActivityForResult`):

```java
ActivityResultLauncher<Intent> launcher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == RESULT_OK) {
            // Handle result
        }
    });
launcher.launch(intent);
```

---

## 12. build.gradle Configuration

```groovy
android {
    compileSdk 36
    defaultConfig {
        minSdk 24
        targetSdk 35
    }

    buildFeatures {
        viewBinding true
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
        coreLibraryDesugaringEnabled true
    }
}

dependencies {
    coreLibraryDesugaring "com.android.tools:desugar_jdk_libs:2.1.x"

    // AndroidX Core
    implementation "androidx.appcompat:appcompat:1.7.x"
    implementation "com.google.android.material:material:1.12.x"
    implementation "androidx.constraintlayout:constraintlayout:2.2.x"

    // Lifecycle (ViewModel + LiveData)
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.8.x"
    implementation "androidx.lifecycle:lifecycle-livedata:2.8.x"

    // Navigation
    implementation "androidx.navigation:navigation-fragment:2.8.x"
    implementation "androidx.navigation:navigation-ui:2.8.x"

    // Room
    implementation "androidx.room:room-runtime:2.6.x"
    annotationProcessor "androidx.room:room-compiler:2.6.x"

    // WorkManager
    implementation "androidx.work:work-runtime:2.9.x"

    // Networking (Retrofit)
    implementation "com.squareup.retrofit2:retrofit:2.11.x"
    implementation "com.squareup.retrofit2:converter-gson:2.11.x"

    // Image loading
    implementation "com.github.bumptech.glide:glide:4.16.x"
}
```

---

## 13. Project Package Structure

```
com.example.myapp/
├── data/
│   ├── local/
│   │   ├── AppDatabase.java          // @Database
│   │   ├── UserDao.java              // @Dao
│   │   └── entity/
│   │       └── User.java             // @Entity
│   ├── remote/
│   │   ├── ApiService.java           // Retrofit interface
│   │   └── dto/
│   │       └── UserResponse.java     // Network DTOs
│   └── repository/
│       └── UserRepository.java       // Single source of truth
├── ui/
│   ├── MainActivity.java             // Single Activity host
│   ├── home/
│   │   ├── HomeFragment.java
│   │   └── HomeViewModel.java
│   ├── detail/
│   │   ├── DetailFragment.java
│   │   └── DetailViewModel.java
│   └── adapter/
│       └── UserAdapter.java          // RecyclerView adapter
├── service/
│   └── MyFirebaseMessagingService.java
├── util/
│   └── AppExecutors.java             // Centralized ExecutorService
└── AppApplication.java               // Custom Application class
```

---

## 14. Lifecycle Quick Reference

### Activity Lifecycle
```
onCreate → onStart → onResume → [RUNNING] → onPause → onStop → onDestroy
```

| Callback | Do | Don't |
|----------|----|-------|
| `onCreate()` | Inflate layout, bind views, setup ViewModel | Heavy computation |
| `onStart()` | Register LiveData observers, start animations | — |
| `onResume()` | Resume camera/sensors, enable input | — |
| `onPause()` | Release camera/sensors, pause animations | Heavy operations |
| `onStop()` | Save persistent data, unregister receivers | — |
| `onDestroy()` | Final cleanup, release all resources | — |

### Fragment Lifecycle
```
onAttach → onCreate → onCreateView → onViewCreated → onStart → onResume
→ onPause → onStop → onDestroyView → onDestroy → onDetach
```

**Two lifecycles:** Fragment lifecycle + View lifecycle (shorter). Always use `getViewLifecycleOwner()` for LiveData observers.

---

## 15. Prohibited Patterns

| Pattern | Why | Replacement |
|---------|-----|-------------|
| `findViewById()` | Not type-safe, verbose | **View Binding** |
| `AsyncTask` | Deprecated API 30 | **ExecutorService** |
| `SharedPreferences` (new code) | Sync I/O, corruption-prone | **DataStore** |
| `startActivityForResult()` | Deprecated | **Activity Result API** |
| `onBackPressed()` | Deprecated API 33 | **OnBackPressedDispatcher** |
| `onCreateOptionsMenu()` | Not lifecycle-aware | **MenuProvider** |
| `android:onClick` in XML | Fragile, breaks with Fragments | **Programmatic listeners** |
| `<fragment>` tag in XML | Deprecated | **FragmentContainerView** |
| `android.app.Fragment` | Deprecated | **androidx.fragment.app.Fragment** |
| Background Service | Restricted API 26+ | **WorkManager** |
| `LocalBroadcastManager` | Removed from AndroidX | **LiveData / ViewModel** |
| Manual FragmentManager | Error-prone | **Navigation Component** |
| Store Activity Context in ViewModel | Memory leak | **Application Context or none** |
| Observe LiveData with `this` in Fragment | Duplicate observers | **getViewLifecycleOwner()** |
| Network/DB on main thread | ANR crash | **ExecutorService / Room LiveData** |

---

## 16. Permissions (2026)

| Permission type | Behavior |
|----------------|----------|
| **Normal** (INTERNET, FOREGROUND_SERVICE) | Auto-granted at install |
| **Dangerous** (CAMERA, LOCATION, CONTACTS) | Runtime request required (API 23+) |
| **Media** (API 33+) | Granular: `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO` |
| **POST_NOTIFICATIONS** (API 33+) | Runtime request required |

Use `ActivityCompat.requestPermissions()` or Activity Result API with `RequestPermission` contract.

---

## 17. Testing Expectations

| Test type | Tool | Target |
|-----------|------|--------|
| Unit tests | JUnit 4/5, Mockito | ViewModel, Repository logic |
| Room tests | `Room.inMemoryDatabaseBuilder()` | DAO queries |
| LiveData tests | `InstantTaskExecutorRule` | LiveData emissions |
| UI tests | Espresso | Fragment interactions |
| Process death | `adb shell am kill <package>` | SavedStateHandle restoration |
