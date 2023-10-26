package com.jenkron.arapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sceneView: ArSceneView
    lateinit var placeButton: ExtendedFloatingActionButton
    lateinit var modelNode: ArModelNode
    lateinit var sensorManager: SensorManager
    private var lastKnownOrientation: FloatArray? = null
    private var initialRotationMatrix = FloatArray(9)
    private val rotationSensitivity = .5f
    private var initialRollInDegrees: Float? = null
    private var isModelPlaced: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        placeButton = findViewById(R.id.place)

        placeButton.setOnClickListener {
            placeModel()
        }

        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/hat.glb",
                scaleToUnits = .5f,
                centerOrigin = Position(0f)
            )
            {
                sceneView.planeRenderer.isVisible = true
                it.materialInstances[0]
            }
            rotation = Rotation(0f, 0f, 0f)
        }

        sceneView.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_UP) {
                placeButton.isGone = false
            }
            false
        }

        sceneView.addChild(modelNode)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private fun placeModel() {
        modelNode.anchor()
        modelNode.rotation = Rotation(0f, 0f, 0f)
        sceneView.planeRenderer.isVisible = false
        isModelPlaced = true
        placeButton.isGone = true

        lastKnownOrientation?.let {
            SensorManager.getRotationMatrixFromVector(initialRotationMatrix, it)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            lastKnownOrientation = event.values
            updateModelRotationBasedOnOrientation()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun updateModelRotationBasedOnOrientation() {
        lastKnownOrientation?.let { orientation ->
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, orientation)

            val orientationValues = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationValues)
            val rollInDegrees = radiansToDegrees(orientationValues[2]) * rotationSensitivity

            if (initialRollInDegrees == null) {
                initialRollInDegrees = rollInDegrees
            }

            val adjustedRoll = rollInDegrees - (initialRollInDegrees ?: 0f)

            // Invert the rotation direction to match the phone tilt
            modelNode.rotation = Rotation(0f, 0f, -adjustedRoll)
        }
    }


    private fun radiansToDegrees(radians: Float): Float {
        return radians * (180.0f / kotlin.math.PI.toFloat())
    }
}