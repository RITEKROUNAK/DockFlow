# DockFlow

A beautiful Jetpack Compose Android app for landscape "Dock Mode" featuring a music player and digital clock.

## Features

- **Universal Music Control**: Works with any music app (Spotify, YouTube Music, etc.) using Android's MediaSession API
- **Glassmorphic Design**: Beautiful blurred album art background
- **Music Player**: 
  - Album art display with rounded corners
  - Playback controls (Play/Pause, Next, Previous)
  - Progress bar with time tracking
  - Animated audio visualizer
- **Digital Clock**: Large, modern clock with date display
- **Theme Switching**: Three beautiful themes (Neon, Minimal, Analog) - long-press the clock to switch
- **Always On**: Screen stays on while the app is active
- **Immersive Mode**: Full-screen experience with hidden system bars

## Requirements

- Android 8.0 (API 26) or higher
- Notification Listener permission (required for music control)

## Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on your device or emulator
4. Grant Notification Listener permission when prompted
5. Play music from any app (Spotify, YouTube Music, etc.)
6. Enjoy your dock mode!

## Usage

- **Music Controls**: Use the on-screen buttons to control playback
- **Theme Switching**: Long-press the clock to cycle through themes (Neon → Minimal → Analog)
- **Landscape Mode**: The app is locked to landscape orientation for optimal dock experience

## Architecture

- **Jetpack Compose**: Modern declarative UI
- **Material3**: Latest Material Design components
- **MediaSession API**: Universal music control
- **Kotlin Coroutines & Flow**: Reactive state management
- **DataStore**: Theme preference persistence

## Project Structure

```
app/
├── src/main/java/com/dockflow/
│   ├── MainActivity.kt                    # Main entry point
│   ├── data/
│   │   └── model/
│   │       └── MediaMetadata.kt          # Media data models
│   ├── service/
│   │   └── MediaSessionService.kt        # Media session listener
│   ├── ui/
│   │   ├── components/
│   │   │   ├── AudioVisualizer.kt        # Animated visualizer
│   │   │   ├── ClockSection.kt           # Clock display
│   │   │   ├── GlassmorphicBackground.kt # Blurred background
│   │   │   └── MusicPlayerSection.kt     # Music player UI
│   │   ├── screens/
│   │   │   ├── DockModeScreen.kt         # Main screen
│   │   │   └── PermissionScreen.kt       # Permission request
│   │   └── theme/
│   │       ├── Color.kt                  # Theme colors
│   │       ├── Theme.kt                  # Theme definitions
│   │       ├── ThemeManager.kt           # Theme management
│   │       └── Type.kt                   # Typography
│   └── res/                              # Resources
```

## Themes

### Neon Theme (Default)
- Vibrant cyan and magenta colors
- Dark background with glowing accents
- Perfect for nighttime use

### Minimal Theme
- Clean, professional design
- Light background with subtle colors
- Great for daytime use

### Analog Theme
- Warm, vintage aesthetic
- Brown and beige tones
- Cozy and nostalgic feel

## License

This project is open source and available for personal and commercial use.
