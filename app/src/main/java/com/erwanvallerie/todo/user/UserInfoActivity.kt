package com.erwanvallerie.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.erwanvallerie.todo.R
import com.erwanvallerie.todo.network.UserInfo
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import java.util.*

class UserInfoActivity : AppCompatActivity() {
    val mediaStore by lazy { MediaStoreRepository(this) }
    private val userView = UserInfoViewModel();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        lifecycleScope.launch {
            val info = userView.getInfo()
            findViewById<EditText>(R.id.editTextNom).setText(info?.lastName);
            findViewById<EditText>(R.id.editTextPrenom).setText(info?.firstName);
            findViewById<EditText>(R.id.editTextEmail).setText(info?.email);
        }

        findViewById<Button>(R.id.take_picture_button).setOnClickListener{
            launchCameraWithPermission()
        }

        findViewById<Button>(R.id.upload_image_button).setOnClickListener{
            galleryLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.buttonSauvegarder).setOnClickListener{
            val nom = findViewById<EditText>(R.id.editTextNom).text.toString();
            val prenom = findViewById<EditText>(R.id.editTextPrenom).text.toString();
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString();
            userView.updateData(UserInfo(email,nom,prenom))
            Toast.makeText(applicationContext,"Vos informations ont bien été modifiées", Toast.LENGTH_LONG).show()
            this.finish();
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { image ->
        if (image != null) {
            handleImage(image)
        }
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted)
                launchCamera()
            else
                launchCameraWithPermission()
        }

    private val permissionAndCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        // pour simplifier on ne fait rien ici, il faudra que le user re-clique sur le bouton
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        when {
            mediaStore.canWriteSharedEntries() && isAlreadyAccepted ->  launchCamera()
            isExplanationNeeded -> showExplanation()// afficher une explication
            else -> permissionAndCameraLauncher.launch(arrayOf(camPermission, storagePermission))
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings()}
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    // register
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            val view = // n'importe quelle vue (ex: un bouton, binding.root, window.decorView, ...)
                if (accepted) handleImage(photoUri)
                else Snackbar.make(window.decorView, "Échec!", Snackbar.LENGTH_LONG).show()
        }

    private fun handleImage(imageUri: Uri) {
        userView.handleImage(contentResolver.openInputStream(imageUri)!!.readBytes())
    }

    private lateinit var photoUri: Uri
    private fun launchCamera() {
        lifecycleScope.launch {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
            cameraLauncher.launch(photoUri)
        }
    }
}