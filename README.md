# Scrollosophy 💡

Wir verbringen täglich Stunden mit sinnlosem Scrollen – Instagram, TikTok, YouTube Shorts. Kurze Inhalte, die uns zwar bespassen, aber uns keinen Mehrwert bieten.

**Scrollosophy** ist die bewusste Alternative: Statt unsinnige Videos bekommst du Weisheiten und Zitate von grossen Denkern um den eigenen Horizont zu erweitern.

Das UI ist bewusst minimalistisch, keine Ablenkungen, nur das Zitat und du.

Und weil Inspiration und Weiterbildung ein Ritual sein soll, erinnert dich **Scrollosophy** jeden Morgen daran, ein neues Zitat zu entdecken. Keine Werbung, kein Algorithmus – nur Wissen, das dich wirklich weiterbringt. Den eigenen Horizont erweitern statt zu verblöden. Das ist **Scrollosophy**.

## 🛠️ Technologien

-   Kotlin
-   Jetpack Compose
-   Google's Cronet

## 📋 Anforderungen

-   Android SDK 35 oder höher (Android 15+)
-   Android Studio Meerkat oder neuer

## ⚙️ Setup & Installation

1. Klone das Repo:

```bash
git clone https://github.com/jannismilz/scrollosophy.git
```

2. Öffne das Projekt in Android Studio

3. Builde das Projekt:

```bash
./gradlew build
```

4. Lasse die App auf deinem Gerät oder im Emulator laufen

5. Linter laufen lassen:

```bash
./gradlew ktlintCheck
```

## 📚 Projektstruktur

-   **data**: Beinhaltet Repositories und Daten Objekte

-   **components**: UI Komponenten

-   **ui**: Theme und Styling

## 📱 Verwendung

1. App starten
2. Fange an zu Scrollen!

## 🔒 Berechtigungen

Folgende Berechtigungen werden gebraucht:

-   `INTERNET`: Um aufs Internet zuzugreifen
-   `POST_NOTIFICATIONS`: Für Benachrichtigungen (Android 13+)
