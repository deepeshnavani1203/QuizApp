# Quizify — Core Logic (Hinglish)

---

## 1. Login / Register kaise kaam karta hai?

Jab user email aur password enter karta hai:

```
User → email + password type karta hai
     ↓
Background Thread start (UI freeze na ho isliye)
     ↓
HTTP POST → /api/users/login
     ↓
Backend MongoDB mein user dhundta hai
bcrypt.compare() se password match karta hai
     ↓
Response: { userId, name, email }
     ↓
SharedPreferences mein save → app yaad rakhta hai
     ↓
MainActivity open
```

**Kyun background thread?**
Android mein network call main thread pe karo toh app crash ho jaata hai.
Isliye `new Thread(() -> { ... }).start()` use kiya.

**Password secure kaise?**
Backend pe `bcrypt.hash()` se hash store hota hai.
Login pe `bcrypt.compare()` se match — plain text kabhi nahi dikhta.

---

## 2. Questions Kahan Se Aate Hain?

```
User "Java" category select karta hai
     ↓
QuizActivity open → Skeleton loading dikhta hai
     ↓
Background thread mein:

  Step 1 → GET /api/quizzes
           Saari quizzes ki list aati hai

  Step 2 → "Java" ko map karo "Java Basics" mein
           (backend ka title alag hai)

  Step 3 → Matching quiz ka _id nikalo

  Step 4 → GET /api/quizzes/:id
           Full quiz with all questions aata hai

  Step 5 → JSON parse karo → Question objects banao

  Step 6 → Difficulty filter (Easy/Medium/Hard)

  Step 7 → Shuffle karo, max 20 lo

     ↓
UI thread pe questions display
```

**Agar backend nahi mila?**
Pehle offline garbage questions aate the ("Question 1, Option A B C D").
Ab hum ne woh hata diya — sirf error dialog aata hai "Retry" ke saath.

---

## 3. Timer Kaise Kaam Karta Hai?

```java
CountDownTimer(duration, 1000) {
    onTick()   → har second screen update
    onFinish() → time khatam → auto next question
}
```

| Difficulty | Time |
|---|---|
| Easy | 30 seconds |
| Medium | 25 seconds |
| Hard | 20 seconds |

Har naye question pe timer **reset** hota hai.
Time khatam + answer nahi diya → Hard mode mein **-1 penalty**.

---

## 4. Scoring Logic

```
Sahi answer    → +1
Galat answer   → Easy/Medium: 0  |  Hard: -1
Time khatam    → Easy/Medium: 0  |  Hard: -1
```

`sessionScore` variable real-time track karta hai.
Quiz end pe score backend + local SQLite dono mein save hota hai.

---

## 5. Confidence Meter ⚡ — Unique Feature

**Idea:** Fast answer = Confident, Slow answer = Doubtful

```
Question display hota hai
     ↓
questionStartTime = System.currentTimeMillis()  ← time note karo

User Next dabata hai
     ↓
timeTakenMs = currentTime - questionStartTime  ← kitna time laga
Question object mein save

Quiz khatam → ResultActivity mein:
     ↓
Threshold = time limit ka aadha
  Easy:   15s  (30/2)
  Medium: 12.5s (25/2)
  Hard:   10s  (20/2)
     ↓
timeTakenMs <= threshold  →  CONFIDENT ✓
timeTakenMs >  threshold  →  DOUBTFUL  ✗
     ↓
confidencePct = (confidentCount / answeredCount) × 100
```

**Display:**
- ≥75% → 💪 Green
- ≥40% → 🤔 Orange
- <40%  → 😅 Red

Progress bar 0 se final % tak **animate** hoti hai 1.2 seconds mein.

> **Viva line:** *"This feature analyzes user confidence based on response time."*

---

## 6. Result Backend Ko Kaise Submit Hota Hai?

```
Quiz khatam → finishQuiz()
     ↓
Local SQLite mein save (offline backup)
     ↓
Background thread:
POST /api/results/submit
{
  userId, quizId, score,
  totalQuestions, correctAnswers, wrongAnswers,
  timeTaken, topicPerformance[]
}
     ↓
Backend:
  → Result document MongoDB mein save
  → User.quizHistory[] mein entry add
  → Analytics recalculate:
      avgScore, weakTopics, strengths
```

---

## 7. Profile — History Kaise Dikhti Hai?

```
ProfileActivity open
     ↓
GET /api/users/:id/history
Backend → User ka quizHistory[] return karta hai
populate() se quiz title bhi aata hai
     ↓
Android mein parse → RecyclerView mein display
     ↓
Agar backend nahi mila → Local SQLite se dikhao
```

### Clear History kaise kaam karta hai?

```
"Clear History" button → Confirmation dialog
     ↓
Background thread:

  1. DELETE /api/users/:id/history
     Backend pe:
       Result.deleteMany({userId})     ← saare results delete
       User.findByIdAndUpdate($set):   ← atomic update
         quizHistory: []
         analytics sab zero

  2. Local SQLite clearHistory()

  3. SharedPreferences stats reset
     ↓
UI: empty list, "0 quizzes", "0%"
```

**Kyun `$set` use kiya?**
Direct `user.analytics.field = 0` Mongoose mein fail ho sakta hai
agar subdocument properly initialized nahi.
`$set` atomic hai — hamesha kaam karta hai.

---

## 8. Notifications 🔔

### Result Notification — Quiz ke baad turant:
```
ResultActivity → postResultNotification()
     ↓
Android 13+ pe permission check
     ↓
NotificationCompat.Builder:
  Title: "Quiz Complete – Java"
  Body:  "Score: 18/20 · Confidence: 75% 💪"
     ↓
Phone pe notification aati hai
```

### Daily Reminder — Roz 8 baje:
```
MainActivity → scheduleDailyReminder(context, 20)
     ↓
AlarmManager.setRepeating() → har 24 ghante
     ↓
ReminderReceiver.onReceive() fire hota hai
     ↓
"Time to practice! 🧠 Keep your streak alive"
```

---

## 9. Do Not Disturb 🔕

**Problem pehle:** Quiz ke beech dialog aata tha → onPause trigger → quiz submit ho jaata tha.

**Fix:** Permission sirf **ek baar login pe** maango.

```
Login success
     ↓
hasDndBeenAsked() → pehli baar?
     ↓
Dialog: "2 permissions chahiye — Notifications + DND"
     ↓
POST_NOTIFICATIONS request
     ↓
DND Settings open → user manually allow karta hai
     ↓
Flag save: dnd_permission_asked = true
(Dobara kabhi nahi poochega)
```

**Quiz ke dauran:**
```
Questions load → SecurityHelper.enableDnd(true)
                 → Phone silent (INTERRUPTION_FILTER_NONE)
     ↓
Quiz khatam  → SecurityHelper.enableDnd(false)
                 → Normal wapas (INTERRUPTION_FILTER_ALL)
```

---

## 10. Google Calendar 📅

```
"Add to Calendar" button dabao
     ↓
CalendarHelper.addPracticeReminder()
     ↓
Intent.ACTION_INSERT fire
Type: "vnd.android.cursor.item/event"
     ↓
System Calendar app khulta hai pre-filled:
  Title:  "Quizify – Revise Java Basics"
  Desc:   "Last attempt: 18/20 (90%)"
  Start:  Kal 10:00 AM
  End:    Kal 10:30 AM
  Alarm:  ON
     ↓
User confirm → Calendar mein save
```

**Koi permission nahi chahiye** — hum calendar app ko intent bhejte hain,
directly database mein nahi likhte.

---

## 11. Screenshot Prevention 🔒

```java
getWindow().setFlags(FLAG_SECURE, FLAG_SECURE)
```

Bas ek line — quiz screen secure ho jaati hai:
- Screenshot nahi le sakte
- Screen recording mein black screen
- Recent apps mein blurred

---

## 12. Local + Backend — Dono Kyun?

```
Quiz complete
     ↓
┌──────────────────────┐    ┌──────────────────────┐
│   Local SQLite       │    │   MongoDB Backend     │
│   (Offline backup)   │    │   (Main storage)      │
│   DatabaseHelper     │    │   Result + User model │
└──────────────────────┘    └──────────────────────┘
```

**History dikhane ka rule:**
- Backend ne response diya → woh dikhao (chahe empty bhi)
- Backend nahi mila → Local SQLite se dikhao

Internet nahi hai toh bhi history dikhti hai.

---

## 13. IP Address Problem — Kyun Hoti Hai?

```
Phone hotspot ON
     ↓
Laptop hotspot se connect → Laptop ko IP milti hai
e.g. 172.22.19.34
     ↓
BackendService.java mein BASE_URL = "http://172.22.19.34:5000"
     ↓
App phone pe run → same network pe hai → connect hota hai
```

**Problem:** Har baar network change hone pe IP change hoti hai.
College WiFi → alag IP. Phone hotspot → alag IP.

**Solution:** Laptop pe static IP set karo router settings mein.

---

## Architecture — Ek Nazar Mein

```
┌─────────────────────────────────────────┐
│            Android App (Java)           │
│                                         │
│  Activities  →  BackendService (HTTP)   │
│  Adapters    →  DatabaseHelper (SQLite) │
│  Layouts     →  SharedPreferences       │
│              →  NotificationHelper      │
│              →  CalendarHelper          │
│              →  SecurityHelper (DND)    │
└──────────────────┬──────────────────────┘
                   │ HTTP REST
┌──────────────────▼──────────────────────┐
│         Node.js + Express Backend       │
│                                         │
│  /api/users   → Login, Register, History│
│  /api/quizzes → Questions fetch         │
│  /api/results → Score submit            │
│                                         │
│  MongoDB (Mongoose) — Data store        │
│  bcrypt — Password hashing              │
└─────────────────────────────────────────┘
```
