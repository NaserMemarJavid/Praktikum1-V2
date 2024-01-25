package com.lern1.mysensors

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lern1.mysensors.ui.theme.MySensorsTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest: LocationRequest


    private var lat  by mutableStateOf("")
    private var lon by  mutableStateOf("")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        setContent {
            MySensorsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Gps(lat, lon)
                        Accelerometer()
                        Gyroscope()
                       Magnetometer()
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun createLocationRequest() {
       // locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).build()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.lastLocation?.let { location ->
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                    println("Latitude: $lat, Longitude: $lon")
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {/*
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )*/
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }




    private fun getLocation() {

        if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {

                        lat  = location.latitude.toString()

                        println("lati*"+location.latitude)
                        println("lati**$lat")

                        lon = location.longitude.toString()

                        println("long*"+location.longitude)
                        println("long**$lon")
                    }
                    else{
                        lat  = "-1.1"
                        lon = "-1.1"
                    }
                }
        }
        else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
        }


    }
}

private fun saveDataToFile(context: Context, fileName: String, dataList: List<String>) {
    try {
        val file = File(context.filesDir, fileName)
        val fileWriter = FileWriter(file)

        for (data in dataList) {
            fileWriter.append(data)
        }

        fileWriter.flush()
        fileWriter.close()

        // Optionally, display a toast or log that data has been saved.
        Toast.makeText(context, "Data saved in file", Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
        Toast.makeText(context, "Error saving data", Toast.LENGTH_SHORT).show()
    }
}
private fun readDataFromFile(context: Context, fileName: String): List<String> {
    val dataList = mutableListOf<String>()
    try {
        val file = File(context.filesDir, fileName)
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)

        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            dataList.add(line!!)
        }

        bufferedReader.close()
        fileReader.close()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
        Toast.makeText(context, "Error reading data", Toast.LENGTH_SHORT).show()
    }
    return dataList
}

private fun showDataDialog(context: Context, dataList: List<String>) {
    AlertDialog.Builder(context)
        .setTitle("Accelerometer Data")
        .setMessage(dataList.joinToString(separator = "\n"))
        .setPositiveButton("OK") { _, _ -> }
        .show()
}



@Composable
fun Gps(lat:String, lon:String) {

    println("posX$lat")
    println("posY$lon")


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
        //.background(color = Color.Gray)
    ){
        Column() {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =  Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ){
                Text(text = "GPS", color = Color.Black )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =  Arrangement.SpaceBetween,
            ) {
                Column() {
                    Row {
                        Text(text = "X:   ",
                            modifier = Modifier.width(20.dp))
                        Text(text = lat,
                            modifier = Modifier.width(100.dp))
                    }
                    Row {
                        Text(text = "Y:   ",
                            modifier = Modifier.width(20.dp))
                        Text(text = lon,
                            modifier = Modifier.width(100.dp))
                    }
                }
                Column {
                    Button(onClick = { }, content = { Text(text = "Save")})
                    Button(onClick = { }, content = { Text(text = "show")})
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Phase:")
                Slider(value =20f, onValueChange = {} )
                Text(text = "0")
            }
        }
    }

} // End of GPS Composable

@Composable
fun Accelerometer() {

    val ctx = LocalContext.current
    val sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var acx = remember { mutableStateOf(-1f) }
    var acy = remember { mutableStateOf(-1f) }
    var acz = remember { mutableStateOf(-1f) }

    val accelerationDataList = remember { mutableListOf<String>() }


    val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                acx.value = event.values[0]; acy.value = event.values[1]; acz.value = event.values[2]

                val timestamp = System.currentTimeMillis()
                val accX = event.values[0]
                val accY = event.values[1]
                val accZ = event.values[2]

                // Format the data as a string
                val data = "Timestamp: $timestamp\n" +
                        "accX: $accX\n" +
                        "accY: $accY\n" +
                        "accZ: $accZ\n\n"

                // Add the data to the list
                accelerationDataList.add(data)
            }
        }
    }
    DisposableEffect(sensorManager) {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorDelay = 10000000

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, sensorDelay)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
        //.background(color = Color.Gray)
    ){
        Column() {
            Row (
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween, verticalAlignment     = Alignment.CenterVertically
            ){
                Text(text = "Accelerometer", color = Color.Black )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween,
            ) {
                Column() {
                    Text("accX: ${acx.value}\naccY: ${acy.value}\naccZ: ${acz.value}")
                }
                Column {
                    Button(onClick = { saveDataToFile(ctx, "acceleration_data.txt", accelerationDataList) }, content = { Text(text = "Save")})
                    Button(onClick = { showDataDialog(ctx, accelerationDataList) }, content = { Text(text = "show")})
                }
            }
        }
    }



}// End of ACC Composable




@Composable
fun Gyroscope(){

    val ctx = LocalContext.current
    val sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var gX = remember { mutableStateOf(-1f) }
    var gY = remember { mutableStateOf(-1f) }
    var gZ = remember { mutableStateOf(-1f) }

    var gyroscopeDataList = remember { mutableListOf<String>() }

    val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {

                gX.value = event.values[0]; gY.value = event.values[1]; gZ.value = event.values[2]

                val timestamp = System.currentTimeMillis()
                val gyrX = event.values[0]
                val gyrY = event.values[1]
                val gyrZ = event.values[2]

                // Format the data as a string
                val data = "Timestamp: $timestamp\n" +
                        "gyrX: $gyrX\n" +
                        "gyrY: $gyrY\n" +
                        "gyrZ: $gyrZ\n\n"

                // Add the data to the list
                gyroscopeDataList.add(data)
            }
        }
    }
    DisposableEffect(sensorManager) {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val sensorDelay = 10000000

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, sensorDelay)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }


        Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
        //.background(color = Color.Gray)
    ){
        Column() {
            Row (
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Gyroscope", color = Color.Black )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween,
            ) {
                Column() {
                    Text("X: ${gX.value}\nY: ${gY.value}\nZ: ${gZ.value}")
                }
                Column {
                    Button(onClick = { saveDataToFile(ctx, "gyroscope_data.txt", gyroscopeDataList) }, content = { Text(text = "Save")})
                    Button(onClick = { showDataDialog(ctx, gyroscopeDataList) }, content = { Text(text = "show")})
                }
            }

        }
    }

}// End of GYR Composable

@Composable
fun Magnetometer(){


    val ctx = LocalContext.current
    val sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    var mX = remember { mutableStateOf(-1f) }
    var mY = remember { mutableStateOf(-1f) }
    var mZ = remember { mutableStateOf(-1f) }

    var magnetometerDataList = remember { mutableListOf<String>() }

    val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mX.value = event.values[0]; mY.value = event.values[1]; mZ.value = event.values[2]

                val timestamp = System.currentTimeMillis()
                val magX = event.values[0]
                val magY = event.values[1]
                val magZ = event.values[2]

                // Format the data as a string
                val data = "Timestamp: $timestamp\n" +
                        "accX: $magX\n" +
                        "accY: $magY\n" +
                        "accZ: $magZ\n\n"

                // Add the data to the list
                magnetometerDataList.add(data)
            }
        }
    }
    DisposableEffect(sensorManager) {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val sensorDelay = 10000000

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, sensorDelay)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
        //.background(color = Color.Gray)
    ){
        Column() {
            Row (
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Magnetometer", color = Color.Black )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement =  Arrangement.SpaceBetween,
            ) {
                Column() {
                    Text("X: ${mX.value}\nY: ${mY.value}\nZ: ${mZ.value}")
                }
                Column {
                    Button(onClick = { saveDataToFile(ctx, "magnetometer_data.txt", magnetometerDataList) }, content = { Text(text = "Save")})
                    Button(onClick = { showDataDialog(ctx, magnetometerDataList) }, content = { Text(text = "show")})
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MySensorsTheme {
        Gps(lat = "55", lon = "66")
        Accelerometer()
    }
}
