package com.example.lab12mapas

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.CameraUpdateFactory
/*import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE*/
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch

@Composable
fun MapScreen() {
    val arequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(arequipaLocation, 12f)
    }
    val context = LocalContext.current

    //PlacesClient
    val placesClient = remember { Places.createClient(context) }

    //ubicaciones
    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994) // Zamacola
    )

    //poligonos
    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )

    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    //ubicacion del usuario
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLocation!!, 12f))
                }
            }
        } else {
            //permisos de ubicación
            ActivityCompat.requestPermissions(
                context as android.app.Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    Column {
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = mapType)
            ) {
                val icon = remember {
                    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.taza_de_cafe)
                    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false) //tamaño de icono
                    BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                }

                //marcadores
                locations.forEach { location ->
                    Marker(
                        state = rememberMarkerState(position = location),
                        icon = icon,
                        title = "Ubicación",
                        snippet = "Punto de interés"
                    )
                }

                // Dibujar polígonos
                Polygon(
                    points = plazaDeArmasPolygon,
                    strokeColor = Color.Red,
                    fillColor = Color.Blue,
                    strokeWidth = 5f
                )
                Polygon(
                    points = parqueLambramaniPolygon,
                    strokeColor = Color.Red,
                    fillColor = Color.Blue,
                    strokeWidth = 5f
                )
                Polygon(
                    points = mallAventuraPolygon,
                    strokeColor = Color.Red,
                    fillColor = Color.Blue,
                    strokeWidth = 5f
                )

                //ubicacion actual del usuario marcador
                userLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Mi Ubicación",
                        snippet = "Aquí estoy"
                    )
                }
            }
        }

        //tipo de mapa
        Column {
            Button(onClick = { mapType = MapType.NORMAL }) {
                Text("Normal")
            }
            Button(onClick = { mapType = MapType.HYBRID }) {
                Text("Híbrido")
            }
            Button(onClick = { mapType = MapType.TERRAIN }) {
                Text("Terreno")
            }
            Button(onClick = { mapType = MapType.SATELLITE }) {
                Text("Satélite")
            }
        }
    }

    //cámara programáticamente
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f), //yura
            durationMs = 3000
        )
    }
}