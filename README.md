# AR Project

An AR Android application allowing users to place and interact with a 3D hat model in an AR space. The hat rotates in response to the device's rotations, simulating the experience of wearing it.

- [Architecture](#architecture)
- [AR Implementation](#ar-implementation)
- [User Interaction](#user-interaction)
- [3D Model Management](#3d-model-management)
- [Sensor Integration](#sensor-integration)
- [Rotation Sensitivity](#rotation-sensitivity)
- [Plane Renderer](#plane-renderer)
- [Additional Utility](#additional-utility)
- [Limitations](#limitations)
- [Future Work](#future-work)
- [Running the Application](#running-the-application)

### Architecture

- **MainActivity**: The heart of the AR functionality. It manages the AR scene, handles interactions, and connects to device sensors.

### AR Implementation

- **ArSceneView**: From the `io.github.sceneview.ar` package, it's the primary space where the 3D models are rendered and interacted with. This component was chosen for its flexibility and ease of integration.

- **ArModelNode**: Represents our 3D hat. ArModelNode provides the necessary functionalities to interact naturally with the AR environment.

### User Interaction

- **placeButton**: This extended floating action button gives users the power to dictate when and where the hat appears and anchor it to a fixed position and orientation in the world. Once the hat is "worn," the button is hidden.

### 3D Model Management

- using a `.glb` format (named `hat.glb`) was a conscious choice since it supports both 3D geometry and animations, allowing for future enhancements. It's loaded asynchronously.

- **PlacementMode.INSTANT**: for an intuitive feel, the hat is immediately placed in the AR environment upon user interaction, as opposed to DRAG_AND_DROP, TAP_TO_PLACE, or SURFACE_SNAP.

### Sensor Integration

- **SensorManager**: Necessary for achieving the AR effect. By tapping into the device's rotation vector sensor, the hat's orientation changes according to the device's orientation, mimicking real-world behavior.

### Rotation Sensitivity

- The sensitivity of `.5f` can be adjusted to make the movement less or more sensitive.

- Initially, I implemented rotations using quaternions. However, I observed inconsistencies in achieving the desired roll motion. As a result, I decided to use a rotation matrix, which provided a more robust and precise rotational behavior.

### Plane Renderer

- Visibility of the AR plane during the hat placement helps user interaction. Once the hat is placed, it's hidden to prevent distractions and for a realistic visualization. This would be removed in future iterations that use facial recognition.

## Limitations

### Yaw Handling

A notable limitation is the yaw motion. Ideally, when you move around the hat, it should remain facing forward, maintaining its orientation to the real world. Current implementation needs refinement here.

## Future Work

1. **Face Detection**: future iterations would aim to detect the user's face, placing the hat more accurately instead of the current plane detection approach.
2. **Stable Yaw Handling**: refining yaw detection and ensuring the hat stays put when the user orbits around it.

## Running the Application

### Prerequisites

- **Java Development Kit (JDK):** JDK 8 is recommended. [Download here](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html).

- **Android Studio:** You'll need Android Studio to run and modify the app. [Download here](https://developer.android.com/studio).

- **Android SDK:** Make sure to install Android SDK Platform 33 or higher.

- **Android Device:** The app supports Android devices running Android 10 (API level 29) or higher, as the `minSdk` version specified is 29. The app will not run in an emulator since it doesn't have the movement sensors needed for AR.

### Setup Instructions

1. **Open in Android Studio:** Launch Android Studio and open the cloned project.

2. **Sync Gradle:** Click on the 'Sync Now' option (usually appears at the top) to sync the Gradle build file. This will ensure all dependencies and SDKs specified in `build.gradle` are correctly fetched.

3. **Connect an android device:** enable USB debugging on your device to connect to Android Studio.

4. **Run the App:** click on the green 'Run' button in Android Studio, or use the `Shift + F10` shortcut.
