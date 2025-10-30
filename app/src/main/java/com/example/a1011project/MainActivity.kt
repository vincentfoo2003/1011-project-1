package com.example.a1011project

import android.os.Binder
import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a1011project.databinding.ActivityMainBinding
import com.example.a1011project.databinding.ActivityMainBinding.inflate
import com.example.a1011project.ui.theme._1011ProjectTheme
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData().start()
    }

    private fun fetchData(): Thread {
        return Thread {
            try { // It's good practice to wrap network code in a try-catch block
                val url = URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=84391")
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"

                // Add the required headers here
                connection.setRequestProperty("AccountKey", "P9wU3SjMR9e5JzOdxSaaAQ==")
                connection.setRequestProperty("accept", "application/json")

                val responseCode = connection.responseCode
                println("Response Code: $responseCode") // More descriptive print

                if (responseCode == HttpsURLConnection.HTTP_OK) { // HTTP_OK is 200
                    // To actually read the data, you need to process the InputStream
                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    // You will get a JSON string here. You need a JSON parsing library
                    // like Gson or Moshi to convert this into usable objects.
                    println("Response Data: $response")
                    // Example of updating UI with the raw response (for demonstration)
                    runOnUiThread {
                        binding.time.text = response // Update a TextView with the fetched data
                    }
                } else {
                    // Update UI on the main thread for failure
                    runOnUiThread {
                        binding.time.text = "Failed to fetch data. Response Code: $responseCode"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exceptions like no internet connection
                runOnUiThread {
                    binding.time.text = "An error occurred: ${e.message}"
                }
            }
        }
    }

}
