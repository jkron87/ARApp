package com.jenkron.arapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
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
    private val rotationSensitivity = 2f
    private var initialRollInDegrees: Float? = null


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

        modelNode = ArModelNode(sceneView.engine,PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/hat.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(0f)
            )
            {
                sceneView.planeRenderer.isVisible = true
                it.materialInstances[0]
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
            }
            rotation = Rotation(0f, 0f, 0f)
        }

        sceneView.addChild(modelNode)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

   private fun placeModel(){
       modelNode.anchor()
       sceneView.planeRenderer.isVisible = false

   }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL)
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
            val quaternion = Quaternion(orientation[0], orientation[1], orientation[2], 1f)
            val currentRollInDegrees = radiansToDegrees(quaternion.toRoll()) * rotationSensitivity

            if (initialRollInDegrees == null) {
                initialRollInDegrees = currentRollInDegrees
            }

            val adjustedRoll = currentRollInDegrees - (initialRollInDegrees ?: 0f)
            modelNode.rotation = Rotation(0f, 0f, adjustedRoll)
        }
    }

    private fun radiansToDegrees(radians: Float): Float {
        return radians * (180.0f / kotlin.math.PI.toFloat())
    }
}


fun Quaternion.toRoll(): Float {
    // Convert quaternion to roll angle in radians
    val sinr = 2 * (w * x + y * z)
    val cosr = 1 - 2 * (x * x + y * y)
    val roll = kotlin.math.atan2(sinr, cosr)
    return if (roll < 0) roll + 2 * kotlin.math.PI.toFloat() else roll
}
