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

- The chosen sensitivity of `2f` amplifies the rotation effect, ensuring users distinctly notice the hat's movement. This can be adjusted to make the movement less or more sensitive.

### Plane Renderer

- Visibility of the AR plane during the hat placement helps user interaction. Once the hat is placed, it's hidden to prevent distractions and for a realistic visualization. This would be removed in future iterations that use facial recognition.

### Additional Utility

- **Quaternion Extension**: The custom function `toRoll()` extracts the roll angle which allows for the hat's accurate orientation adjustment back and forth.

## Limitations

### Yaw Handling

A notable limitation is the yaw motion. Ideally, when you move around the hat, it should remain facing forward, maintaining its orientation to the real world. Current implementation needs refinement here.

## Future Work

1. **Face Detection**: future iterations would aim to detect the user's face, placing the hat more accurately instead of the current plane detection approach.
2. **Stable Yaw Handling**: refining yaw detection and ensuring the hat stays put when the user orbits around it.
