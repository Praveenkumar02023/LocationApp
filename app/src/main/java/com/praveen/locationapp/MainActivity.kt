package com.praveen.locationapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.praveen.locationapp.ui.theme.LocationAppTheme
import android.Manifest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                MyApp(viewModel)
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils , viewModel = viewModel, context = context)
}

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
){
    val location = viewModel.location.value

    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
                    permissions ->
            if(permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true){
                //have permission

                locationUtils.RequestLocationUpdates(viewModel)

            }else{
                //no permission so we have to request.
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity
                    ,Manifest.permission.ACCESS_FINE_LOCATION
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                if(rationaleRequired){
                    Toast.makeText(context,"Location permission required",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Location permission required.Please enable location in settings.",Toast.LENGTH_LONG).show()
                }

            }
        })

    Column(
        modifier =  Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

       if(location != null){
           Text(text = "Latitude : ${location.latitude}")
           Text(text = "Longitude : ${location.longitude}")
           Text(text = "Address : $address",modifier = Modifier.padding(16.dp))
       }else{
           Text(
               text = "Location not found"
           )
       }

        Button(onClick = {
            if(locationUtils.haveLocationAccess(context)){
                //TODO: Get Location
                locationUtils.RequestLocationUpdates(viewModel)

            }else{
                //TODO: Request Permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }

    }

}
