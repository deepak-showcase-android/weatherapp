package com.deepakbarad.weatherapp.framework.data.datasources.remote

import com.deepakbarad.weatherapp.BuildConfig.OPEN_WEATHER_API_KEY
import com.deepakbarad.weatherapp.framework.data.interfaces.IOpenWeatherDataSource
import com.deepakbarad.weatherapp.framework.di.NetworkModule
import com.deepakbarad.weatherapp.framework.model.CurrentWeather
import com.deepakbarad.weatherapp.framework.network.IOpenWeatherApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class OpenWeatherRemoteDataSource @Inject constructor(
    @NetworkModule.OpenWeatherApiQualifier private val openWeatherApi: IOpenWeatherApi
) : IOpenWeatherDataSource {

    override suspend fun getForecast5(longitude: Double, latitude: Double): Flow<CurrentWeather> =
        flow {
            try {
                val queryMap: MutableMap<String, String> = mutableMapOf()
                queryMap["lat"] = latitude.toString()
                queryMap["lon"] = longitude.toString()
                queryMap["appid"] = OPEN_WEATHER_API_KEY
                val currentWeatherResponse = openWeatherApi.getForecast5(queryMap)
                val currentWeather = currentWeatherResponse.body()
                println(currentWeather?.message)
                if (currentWeather != null) {
                    currentWeather.collectedTime = System.currentTimeMillis()
                    emit(currentWeather)
                } else {
                    Timber.d("exception!!")
                    throw(Exception("No current weather info available"))
                }
            } catch (exception: Exception) {
                println(exception)
            }
        }
}