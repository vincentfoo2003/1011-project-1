package com.example.a1011project

// Android & System Imports
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import java.time.OffsetDateTime
// ViewBinding and Gson
import com.example.a1011project.databinding.ActivityMainBinding
import com.google.gson.Gson

// Correct Java 8+ Time Imports
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Networking
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.updatebutton.setOnClickListener {
            val busStopCode = binding.busStopCode.text.toString()
            if (busStopCode.isNotEmpty()) {
                fetchData(busStopCode).start()
            } else {
                binding.time.text = "Please enter a bus stop code"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData(busStopCode: String): Thread {
        return Thread {
            try {
                val url = URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=$busStopCode")
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("AccountKey", "P9wU3SjMR9e5JzOdxSaaAQ==")
                connection.setRequestProperty("accept", "application/json")

                val responseCode = connection.responseCode
                println("Response Code: $responseCode for Bus Stop: $busStopCode")

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputStream)
                    val request = Gson().fromJson(inputStreamReader, BusArrivalResponse::class.java)
                    inputStreamReader.close()
                    inputStream.close()
                    updateUI(request)
                } else {
                    runOnUiThread {
                        binding.time.text = "Failed to fetch data. Response Code: $responseCode"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    binding.time.text = "An error occurred: ${e.message}"
                }
            }
        }
    }

    // You will need to import this new class at the top of your file


// ... inside your MainActivity class

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(response: BusArrivalResponse) {
        runOnUiThread {
            if (response.Services.isNotEmpty()) {
                val firstBusService = response.Services[0]
                val serviceNumber = firstBusService.ServiceNo

                // --- Time Calculation Logic ---

                // 1. Get the current moment in time, aware of time zone.
                // ZonedDateTime.now() would also work here.
                val now = OffsetDateTime.now()

                // --- Handle the first bus ---
                val arrivalTimeString = firstBusService.NextBus.EstimatedArrival
                if (arrivalTimeString.isNotEmpty()) {
                    // 2. Parse the API's full date-time string into a time zone-aware object.
                    val arrivalTime = OffsetDateTime.parse(arrivalTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

                    // 3. Calculate the difference. This comparison is now accurate across time zones.
                    val minutesUntilArrival = ChronoUnit.MINUTES.between(now, arrivalTime)

                    // 4. Create the display text
                    val arrivalDisplayText = when {
                        minutesUntilArrival < 1 -> "Arriving"
                        else -> "$minutesUntilArrival min"
                    }
                    binding.time.text = "Next Bus: $arrivalDisplayText"
                } else {
                    binding.time.text = "Next Bus: Not Available"
                }

                // --- Handle the second and third buses in the same way ---
                val arrivalTime1String = firstBusService.NextBus2.EstimatedArrival
                if (arrivalTime1String.isNotEmpty()) {
                    val arrivalTime1 = OffsetDateTime.parse(arrivalTime1String, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val minutesUntilArrival1 = ChronoUnit.MINUTES.between(now, arrivalTime1)
                    binding.time1.text = "Subsequent: $minutesUntilArrival1 min"
                } else {
                    binding.time1.text = "Subsequent: -"
                }

                val arrivalTime2String = firstBusService.NextBus3.EstimatedArrival
                if (arrivalTime2String.isNotEmpty()) {
                    val arrivalTime2 = OffsetDateTime.parse(arrivalTime2String, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    val minutesUntilArrival2 = ChronoUnit.MINUTES.between(now, arrivalTime2)
                    binding.time2.text = "Following: $minutesUntilArrival2 min"
                } else {
                    binding.time2.text = "Following: -"
                }

                binding.busServiceNumber.setText(serviceNumber)

            } else {
                binding.time.text = "No bus services found for this stop."
                binding.time1.text = ""
                binding.time2.text = ""
                binding.busServiceNumber.setText("")
            }
        }
    }}

