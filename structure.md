# Quizify — Project Structure (Hinglish)

---

## Root Level

```
QuizApp/
├── app/                  ← Android app ka saara code
├── QuizAppBE/            ← Node.js backend server
├── summary.md            ← Full feature documentation
├── logic_hinglish.md     ← Core logic explanation
└── structure.md          ← Yeh file
```

---

## Android App — `app/src/main/`

```
app/src/main/
├── AndroidManifest.xml   ← App ki ID card — permissions, activities register
├── java/                 ← Saara Java code
└── res/                  ← Saare resources (layouts, images, colors)
```

---

## Java Code — `java/com/example/quizapp/`

### `MainActivity.java`
Dashboard — categories grid, notification bell, calendar icon, bottom nav

---

### `models/` — Data ke blueprints

| File | Kya hai |
|---|---|
| `Question.java` | Ek question — text, options, correct index, topic, difficulty, timeTakenMs |
| `QuizResult.java` | History ka ek entry — quiz name, score, total, date |
| `Quiz.java` | Quiz ka basic info — id, title, category |
| `User.java` | User info — name, email |
| `ResultResponse.java` | Backend se analytics response — weakTopics, strengths, avgScore |

---

### `ui/` — Screens (Activities)

#### `ui/auth/` — Login/Register screens
| File | Kya karta hai |
|---|---|
| `SplashActivity.java` | App open hone pe logo + animation, phir login ya dashboard |
| `LoginActivity.java` | Email/password login, ek baar permissions maangta hai |
| `RegisterActivity.java` | Naya account banao |

#### `ui/quiz/` — Quiz screen
| File | Kya karta hai |
|---|---|
| `QuizActivity.java` | Main quiz — questions load, timer, scoring, DND, submit |
| `OptionAdapter.java` | Answer options ki RecyclerView list |

#### `ui/result/` — Result screen
| File | Kya karta hai |
|---|---|
| `ResultActivity.java` | Score, confidence meter, notification fire, calendar button |

#### `ui/review/` — Answer review
| File | Kya karta hai |
|---|---|
| `ReviewActivity.java` | Quiz ke baad saare questions review karo |
| `ReviewAdapter.java` | Har question — tera answer vs sahi answer |

#### `ui/profile/` — Profile screen
| File | Kya karta hai |
|---|---|
| `ProfileActivity.java` | Name, email, stats, history list, clear history, logout |
| `HistoryAdapter.java` | History ki RecyclerView — quiz name, score, date |

#### `ui/topic/` — Category cards
| File | Kya karta hai |
|---|---|
| `CategoryAdapter.java` | Dashboard pe category cards ki grid |

---

### `utils/` — Helper classes (kaam ke tools)

| File | Kya karta hai |
|---|---|
| `BackendService.java` | **Saare HTTP calls** — login, signup, quiz fetch, result submit, history |
| `DatabaseHelper.java` | **Local SQLite** — history save/read/clear offline |
| `SharedPreferencesManager.java` | **User session** — userId, name, email, login state store |
| `SecurityHelper.java` | **DND + Screenshot** — quiz ke dauran phone silent, screen secure |
| `NotificationHelper.java` | **Notifications** — result notif + daily reminder + ReminderReceiver |
| `CalendarHelper.java` | **Calendar intent** — revision event calendar mein add |
| `QuestionBank.java` | Offline question bank (ab use nahi hota) |
| `QuizData.java` | QuestionBank ka raw data |
| `SecurityManager.java` | Extra security utilities |

---

## Resources — `res/`

### `res/layout/` — Screen designs (XML)

| File | Kiska layout |
|---|---|
| `activity_splash.xml` | Splash screen — logo, app name |
| `activity_login.xml` | Login form |
| `activity_register.xml` | Register form |
| `activity_main.xml` | Dashboard — header, category grid, bottom nav |
| `activity_quiz.xml` | Quiz screen — timer, question card, options |
| `activity_result.xml` | Result — score, confidence meter, buttons |
| `activity_review.xml` | Review screen |
| `activity_profile.xml` | Profile — avatar, stats, history list |
| `skeleton_quiz.xml` | Loading placeholder jab questions fetch ho rahe hain |
| `item_category.xml` | Ek category card (dashboard grid) |
| `item_option.xml` | Ek answer option |
| `item_history.xml` | Ek history entry (profile) |
| `item_review.xml` | Ek review card |

### `res/drawable/` — Images aur icons

| File | Kya hai |
|---|---|
| `app_logo.png` | App ka logo |
| `ic_notification.xml` | Bell icon (dashboard) |
| `ic_calendar.xml` | Calendar icon (dashboard) |
| `ic_java.xml` | Java category icon |
| `ic_python.xml` | Python category icon |
| `ic_quiz.xml` | Quiz icon (notifications mein) |
| `confidence_progress.xml` | Confidence meter ka gradient progress bar |
| `option_background_selector.xml` | Answer option ka selected/normal background |
| `circle_bg.xml` | Round background shape |

### `res/values/` — App ke values

| File | Kya hai |
|---|---|
| `colors.xml` | Saare colors — primary, success, error, etc. |
| `strings.xml` | App name aur text strings |
| `themes.xml` | App ka overall theme — Material Design 3 |

### `res/menu/`
| File | Kya hai |
|---|---|
| `bottom_nav_menu.xml` | Bottom navigation ke 3 items — Test, Learn, Profile |

---

## Backend — `QuizAppBE/`

```
QuizAppBE/
├── index.js          ← Server start, MongoDB connect, routes register
├── .env              ← Secret config — MONGO_URI, PORT
├── seed.js           ← Database mein sample questions daalne ka script
├── models/           ← MongoDB schemas
└── routes/           ← API endpoints
```

### `models/` — MongoDB Schemas

| File | Kya store karta hai |
|---|---|
| `User.js` | name, email, password(hashed), quizHistory[], analytics{} |
| `Quiz.js` | title, category, questions[] (text, options, correct, difficulty) |
| `Result.js` | userId, quizId, score, correctAnswers, timeTaken, topicPerformance[] |

### `routes/` — API Endpoints

| File | Routes |
|---|---|
| `user.js` | POST /signup, POST /login, GET /:id, GET /:id/history, DELETE /:id/history, GET /:id/analytics |
| `quiz.js` | GET / (list), GET /:id (full quiz with questions) |
| `result.js` | POST /submit, GET /user/:userId |

---

## Quick Map — Koi Feature Dhundna Ho Toh

| Feature | File |
|---|---|
| Login logic | `ui/auth/LoginActivity.java` |
| Questions fetch | `ui/quiz/QuizActivity.java` → `fetchQuestionsFromBackend()` |
| Timer | `ui/quiz/QuizActivity.java` → `startTimer()` |
| Scoring | `ui/quiz/QuizActivity.java` → `handleNextClick()` |
| Confidence meter | `ui/result/ResultActivity.java` |
| Notification | `utils/NotificationHelper.java` |
| Calendar | `utils/CalendarHelper.java` |
| DND | `utils/SecurityHelper.java` |
| History show | `ui/profile/ProfileActivity.java` → `setupHistory()` |
| History clear | `ui/profile/ProfileActivity.java` → `clearHistory()` + `QuizAppBE/routes/user.js` |
| All HTTP calls | `utils/BackendService.java` |
| Local storage | `utils/DatabaseHelper.java` |
| User session | `utils/SharedPreferencesManager.java` |
| Backend server | `QuizAppBE/index.js` |
| Quiz API | `QuizAppBE/routes/quiz.js` |
| Result save | `QuizAppBE/routes/result.js` |
