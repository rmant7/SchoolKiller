package com.schoolkiller.ui.reusable_components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.schoolkiller.R
import com.schoolkiller.view_model.SchoolKillerViewModel


@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchoolKillerViewModel
) {

//    var images by remember { mutableStateOf(listOf<Uri>()) }
    var showImagePicker by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            val clipData = data?.clipData

            val uriList = mutableListOf<Uri>()

            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uriList.add(uri)
                }
            } else {
                data?.data?.let { uriList.add(it) }
            }

            viewModel.onImagesSelected(uriList)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showImagePicker = true
        } else {
            // TODO { Handle permission denied }
        }
    }

    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            // Check & request permission when the button is clicked
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ) -> {
                        showImagePicker = true
                    }

                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
        } else {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES,
                    ) -> {
                        showImagePicker = true
                    }

                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }
    }) {
        Text(stringResource(R.string.pick_images))
    }


    if (showImagePicker) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,
            true)
        }
        launcher.launch(intent)
        showImagePicker = false // Reset the flag after launching
    }


}



