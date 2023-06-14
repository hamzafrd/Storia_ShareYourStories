package com.hamzafrd.storia.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hamzafrd.storia.data.Result
import com.hamzafrd.storia.databinding.ActivityAddStoryBinding
import com.hamzafrd.storia.helper.SessionPreferences
import com.hamzafrd.storia.helper.StoryViewModelFactory
import com.hamzafrd.storia.ui.viewModel.StoryViewModel
import com.hamzafrd.storia.utils.reduceFileImage
import com.hamzafrd.storia.utils.rotateFile
import com.hamzafrd.storia.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)

        val pref = SessionPreferences.getInstance(dataStore)
        val factory = StoryViewModelFactory.getInstance(this, pref)
        val viewModel: StoryViewModel by viewModels { factory }

        viewModel.getToastText().observe(this) { event ->
            event.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            cameraXButton.setOnClickListener { startCameraX() }
            galleryButton.setOnClickListener { startGallery() }
            uploadButton.setOnClickListener { uploadImage(viewModel, emptyList()) }

            includeLocSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation(object : LocationCallback() {
                        override fun onLocationResult(loc: LocationResult) {
                            super.onLocationResult(loc)
                            val locList =
                                listOf(loc.lastLocation?.latitude, loc.lastLocation?.longitude)
                            uploadButton.setOnClickListener { uploadImage(viewModel, locList) }
                        }
                    })
                } else uploadButton.setOnClickListener { uploadImage(viewModel, emptyList()) }
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage(
        viewModel: StoryViewModel,
        locList: List<Double?>,
    ) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding.edtDescStory.text.toString().toRequestBody("text/plain".toMediaType())

            val lat = isLocationActive(locList, true)
            val lon = isLocationActive(locList, false)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModel.uploadStory(imageMultipart, description, lat, lon).observe(this) {
                when (it) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        viewModel.setToastText(it.error)
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        viewModel.setToastText(it.data)
                        finish()
                    }
                }
            }
        } else viewModel.setToastText("Make sure take a picture")
    }

    private fun isLocationActive(locList: List<Double?>, isLat: Boolean): RequestBody? {
        return if (locList.isNotEmpty()) {
            if (isLat)
                locList.first().toString()
                    .toRequestBody("text/plain".toMediaType())
            else locList.last().toString()
                .toRequestBody("text/plain".toMediaType())
        } else null
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation(object : LocationCallback() {})
            }
        }

    private fun getMyLocation(callback: LocationCallback) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val result = LocationResult.create(listOf(location))
                    callback.onLocationResult(result)
                }
            binding.includeLocSwitch.isChecked = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            binding.includeLocSwitch.isChecked = false
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}