package com.lern1.mysensors

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.lern1.mysensors.ui.theme.MySensorsTheme

class MainActivity : ComponentActivity() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback

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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Gps(lat, lon)
                }
            }
        }
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

@Composable
fun Gps(lat:String, lon:String){

    //var loc by remember { mutableStateOf<Location?>(null) }

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
                Icon(imageVector = Icons.Default.LocationOn,
                    contentDescription = "GPS Icon",
                )
                Text(text = "Sensorname", color = Color.Black )
                Switch(checked = false, onCheckedChange = {
                })
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
                Row {
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
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MySensorsTheme {
    }
}