# 🌱 GreenWatch

<div align="center">

![GreenWatch Logo](app/src/main/res/drawable/logo.png)

**A Smart Plant Growth Monitoring Android Application**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![API Level](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

_Track, monitor, and nurture your plants with intelligent growth insights_

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Technology](#-technology-stack) • [Contributing](#-contributing)

</div>

---

## 📱 Overview

GreenWatch is a comprehensive plant growth monitoring application that empowers users to track their plants' health and development over time. Using advanced camera integration and intelligent analysis, the app provides personalized care suggestions and maintains a detailed growth history for each plant.

## ✨ Features

### 📸 **Smart Camera Integration**

- **Professional Plant Photography**: Built-in camera with optimized settings for plant photography
- **Photography Tips**: Integrated guidance for capturing high-quality plant photos
- **Focus Assistance**: Smart focus indicators and grid lines for perfect shots
- **Real-time Preview**: Live camera preview with instant capture capabilities

### 📊 **Growth Tracking & Analysis**

- **Daily Growth Monitoring**: Track plant development with timestamped entries
- **Health Status Assessment**: Intelligent analysis of plant condition
- **Progress Visualization**: Visual timeline of plant growth journey
- **Data Persistence**: Secure cloud storage with Firebase Firestore

### 🎯 **Intelligent Care Suggestions**

- **Personalized Recommendations**: Tailored care tips based on plant status
- **Health-Based Guidance**: Specific suggestions for different plant conditions:
  - Nutrient deficiency management
  - Watering schedule optimization
  - Pest and disease prevention
  - Environmental condition adjustments

### 📈 **Comprehensive History Management**

- **Growth Timeline**: Chronological view of all plant development stages
- **Detailed Entry Views**: Expanded information for each growth record
- **Photo Gallery**: Visual history of plant transformation
- **Search & Filter**: Easy navigation through growth data

### 🔒 **Data Management**

- **Cloud Synchronization**: Automatic backup with Firebase
- **Offline Capability**: Local data storage for uninterrupted usage
- **Data Security**: Secure authentication and data protection

## 🛠 Technology Stack

### **Frontend**

- **Language**: Java
- **Framework**: Android SDK (API 24+)
- **UI Components**: Material Design 3
- **Camera**: CameraX Library
- **Architecture**: MVVM Pattern

### **Backend & Database**

- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Storage**: Firebase Storage
- **Real-time Sync**: Firebase Real-time Database

### **Development Tools**

- **Build System**: Gradle with Kotlin DSL
- **IDE**: Android Studio
- **Version Control**: Git
- **Dependency Management**: Gradle Version Catalogs

### **Key Dependencies**

```gradle
// CameraX for advanced camera functionality
implementation 'androidx.camera:camera-core:1.3.1'
implementation 'androidx.camera:camera-camera2:1.3.1'
implementation 'androidx.camera:camera-lifecycle:1.3.1'
implementation 'androidx.camera:camera-view:1.3.1'

// Firebase for backend services
implementation 'com.google.firebase:firebase-firestore'
implementation 'com.google.firebase:firebase-database'

// Material Design for modern UI
implementation 'com.google.android.material:material:1.13.0'
```

## 🚀 Installation

### **Prerequisites**

- Android Studio Arctic Fox or later
- Android SDK 24 (Android 7.0) or higher
- Java 11 or higher
- Firebase project setup

### **Setup Instructions**

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/GreenWatch.git
   cd GreenWatch
   ```

2. **Open in Android Studio**

   ```bash
   # Open Android Studio and select "Open an existing project"
   # Navigate to the cloned directory and open it
   ```

3. **Firebase Configuration**

   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Add an Android app to your Firebase project
   - Download `google-services.json` and place it in `app/` directory
   - Enable Firestore Database and Authentication in Firebase Console

4. **Build and Run**
   ```bash
   # Sync project with Gradle files
   # Connect your Android device or start an emulator
   # Click "Run" or press Ctrl+R (Cmd+R on Mac)
   ```

### **Required Permissions**

The app requires the following permissions:

- `CAMERA`: For capturing plant photos
- `WRITE_EXTERNAL_STORAGE`: For saving images (Android 8.1 and below)
- `READ_MEDIA_IMAGES`: For accessing saved images (Android 9+)

## 📖 Usage

### **Getting Started**

1. **Launch the App**: Open GreenWatch from your app drawer
2. **Grant Permissions**: Allow camera and storage access when prompted
3. **Start Monitoring**: Tap "Capture Plant Photo" to begin tracking

### **Capturing Plant Photos**

1. **Open Camera**: Tap the camera button on the main screen
2. **Position Plant**: Frame your plant properly using the guidelines
3. **Focus**: Tap on the plant to focus the camera
4. **Capture**: Press the shutter button to take the photo
5. **Review**: Preview your photo and submit or retake if needed

### **Viewing Growth History**

1. **Access History**: Tap "Growth History" on the main screen
2. **Browse Entries**: Scroll through your plant's development timeline
3. **View Details**: Tap any entry to see detailed information and care suggestions
4. **Track Progress**: Monitor your plant's health status over time

### **Understanding Health Status**

- 🟢 **Healthy**: Plant is thriving, continue current care
- 🟡 **Needs Attention**: Minor adjustments needed
- 🟠 **Water Stress**: Adjust watering schedule
- 🔴 **Nutrient Deficiency**: Consider fertilization
- 🟣 **Pest/Disease**: Immediate action required

## 🏗 Project Structure

```
GreenWatch/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/greenwatch/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── CameraActivity.java
│   │   │   │   │   ├── PreviewActivity.java
│   │   │   │   │   ├── GrowthHistoryActivity.java
│   │   │   │   │   └── GrowthDetailActivity.java
│   │   │   │   ├── adapters/
│   │   │   │   │   └── GrowthHistoryAdapter.java
│   │   │   │   ├── models/
│   │   │   │   │   └── GrowthEntry.java
│   │   │   │   └── utils/
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   └── xml/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### **Development Guidelines**

- Follow Android development best practices
- Use meaningful commit messages
- Add comments for complex logic
- Test on multiple Android versions
- Maintain consistent code style

### **Code Style**

- Use Java naming conventions
- Indent with 4 spaces
- Maximum line length of 120 characters
- Add Javadoc for public methods

## 🐛 Issues & Support

### **Reporting Issues**

Found a bug or have a feature request? Please create an issue on GitHub:

1. Go to the [Issues](https://github.com/yourusername/GreenWatch/issues) page
2. Click "New Issue"
3. Choose the appropriate template
4. Provide detailed information

### **Common Issues**

- **Camera not working**: Check if camera permissions are granted
- **Photos not saving**: Verify storage permissions
- **App crashing**: Check device compatibility (Android 7.0+)
- **Firebase errors**: Ensure `google-services.json` is properly configured

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


## 🙏 Acknowledgments

- **Firebase Team**: For providing excellent backend services
- **CameraX Team**: For the powerful camera library
- **Material Design Team**: For beautiful UI components
- **Open Source Community**: For inspiration and support

## 📞 Contact

- **Project Repository**: [https://github.com/munnaa0/GreenWatch](https://github.com/munnaa0/GreenWatch)
- **Issues**: [https://github.com/munnaa0/GreenWatch/issues](https://github.com/munnaa0/GreenWatch/issues)
- **Discussions**: [https://github.com/munnaa0/GreenWatch/discussions](https://github.com/munnaa0/GreenWatch/discussions)

---

<div align="center">

**Made with ❤️ for plant lovers everywhere**

⭐ **If you find GreenWatch helpful, please give it a star!** ⭐

</div>
