package com.moin.snackhunt

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YelpApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YelpApp() {
    val navController = rememberNavController()
    var searchTerm by rememberSaveable { mutableStateOf("") }
    var businesses by remember { mutableStateOf<List<YelpBusiness>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                searchTerm = searchTerm,
                businesses = businesses,
                onSearchTermChange = { searchTerm = it },
                onBusinessesChange = { businesses = it },
                coroutineScope = coroutineScope,
                context = context
            )
        }
        composable("pizza") { PizzaScreen(businesses = businesses) }
        composable("juice") { JuiceScreen(businesses = businesses) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    searchTerm: String,
    businesses: List<YelpBusiness>,
    onSearchTermChange: (String) -> Unit,
    onBusinessesChange: (List<YelpBusiness>) -> Unit,
    coroutineScope: CoroutineScope,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Snack Hunt",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE91E63)
        )

        OutlinedTextField(
            value = searchTerm,
            onValueChange = onSearchTermChange,
            label = { Text("Search for pizza or juice") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(

            onClick = {
                coroutineScope.launch {
                    if (searchTerm.contains("pizza", ignoreCase = true)) {
                        fetchBusinesses(searchTerm, context, "pizza") { result ->
                            onBusinessesChange(result)
                            navController.navigate("pizza")
                            onSearchTermChange("")
                        }
                    } else {
                        fetchBusinesses(searchTerm, context, "juice") { result ->
                            onBusinessesChange(result)
                            navController.navigate("juice")
                            onSearchTermChange("")
                        }
                    }
                }
            },

            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text("Search", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.pizza),
                contentDescription = "Pizza",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(18f / 12f)
                    .clickable {
                        coroutineScope.launch {
                            fetchBusinesses(searchTerm, context, "pizza") { result ->
                                onBusinessesChange(result)
                                navController.navigate("pizza")
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.juice),
                contentDescription = "Juice",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(18f / 12f)
                    .clickable {
                        coroutineScope.launch {
                            fetchBusinesses(searchTerm, context, "juice") { result ->
                                onBusinessesChange(result)
                                navController.navigate("juice")
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )
        }


    }
}

@Composable
fun PizzaScreen(businesses: List<YelpBusiness>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
    ) {

        item {
            Image(
                painter = painterResource(id = R.drawable.pizzascreen),
                contentDescription = "Pizza Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "NYC is famous for its pizza, offering classic thin crusts and gourmet options alike. " +
                        "Whether it's a quick slice or a sit-down experience, the city's pizzerias are a must-try for pizza lovers.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(14.dp)
            )
        }


        items(businesses) { business ->
            BusinessCard(business)
            Divider(color = Color.LightGray)
        }
    }
}

@Composable
fun JuiceScreen(businesses: List<YelpBusiness>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
    ) {

        item {
            Image(
                painter = painterResource(id = R.drawable.juicescreen),
                contentDescription = "Juice Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "NYC's juice bars serve fresh, cold-pressed juices and smoothies perfect for a healthy boost." +
                        " With a range of flavors and options, it's easy to find something refreshing and nutritious.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = Color(0xFFE91E63),
                modifier = Modifier.padding(14.dp)
            )
        }


        items(businesses) { business ->
            BusinessCard(business)
            Divider(color = Color.LightGray)
        }
    }
}

@Composable
fun BusinessCard(business: YelpBusiness) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)) {
            Text(text = business.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = business.location.display_address.joinToString(", "))
            Text(text = "Rating: ${business.rating}")
            Text(text = "Phone: ${business.display_phone}")

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = business.image_url)
                        .build()
                ),
                contentDescription = business.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}




suspend fun fetchBusinesses(
    searchTerm: String,
    context: android.content.Context,
    category: String,
    onResult: (List<YelpBusiness>) -> Unit
) {
    try {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yelp.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val yelpApiService = retrofit.create(YelpApiService::class.java)

        val term = if (searchTerm.isBlank()) category else searchTerm

        val response: Response<YelpSearchResponse> =
            yelpApiService.searchBusinesses(term = term, location = "NYC") // Replace with desired location


        if (response.isSuccessful) {
            val businesses = response.body()?.businesses ?: emptyList()
            onResult(businesses)
        } else {
            // Handle error
            Log.e("YelpApp", "Error fetching businesses: ${response.code()} - ${response.message()}")
            Toast.makeText(context, "Failed to fetch businesses", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        // Handle error
        Log.e("YelpApp", "Error fetching businesses: ${e.message}")
        Toast.makeText(context, "Failed to fetch businesses", Toast.LENGTH_SHORT).show()
    }
}