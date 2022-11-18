package com.deepakbarad.weatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.deepakbarad.weatherapp.databinding.FragmentWeatherBinding
import com.deepakbarad.weatherapp.framework.base.BaseFragment
import com.deepakbarad.weatherapp.framework.model.CurrentWeather
import com.deepakbarad.weatherapp.framework.services.LocationService
import com.deepakbarad.weatherapp.framework.utils.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : BaseFragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val weatherViewModel: WeatherViewModel by viewModels()

    @Inject
    lateinit var locationService: LocationService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.viewModel = weatherViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        getForecast()
    }

    private fun getForecast() {
        locationService.locationListener.currentLocation?.longitude?.let { longitude ->
            locationService.locationListener.currentLocation?.latitude?.let { latitude ->
                weatherViewModel.getForecast5(
                    longitude, latitude
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setListeners() {
        super.setListeners()
        binding.fabRefresh.setOnClickListener {
            binding.tvWeather.text = ""
            getForecast()
        }
    }

    override fun setObservers() {
        super.setObservers()
        weatherViewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            Timber.i("CurrentWeather ->", currentWeather)
            displayWeatherInfo(currentWeather)
        }

        weatherViewModel.errorInfo.observe(viewLifecycleOwner) { errorMessage ->
            binding.root.showSnackbar(binding.root, errorMessage, Snackbar.LENGTH_SHORT, null) {}
        }
    }

    private fun displayWeatherInfo(currentWeather: CurrentWeather) {
        val weatherData = StringBuilder()
        with(currentWeather) {
            binding.tvCity.text = this.city?.name
            this.list?.forEach { listItem ->
                weatherData.appendLine("${listItem.weather?.get(0)?.main}(${listItem.weather?.get(0)?.description}) On ${listItem.dtTxt}")
            }
            binding.tvWeather.text = weatherData.toString()
            locationService.locationListener.currentLocation?.let { location ->
                binding.tvCoordinates.text = buildString {
                    append(location.longitude)
                    append(" ")
                    append(location.latitude)
                }
            }
        }
    }
}