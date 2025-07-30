package com.example.adminwaveoffood

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityAddItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject


class AddItemActivity : AppCompatActivity() {
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredients: String
    private var foodImageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.AddItemButton.setOnClickListener {
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredients = binding.ingredint.text.toString().trim()

            if (foodName.isNotBlank() && foodPrice.isNotBlank() && foodDescription.isNotBlank() && foodIngredients.isNotBlank()) {
                if (foodImageUri != null) {
                    uploadData()
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadData() {
        val menuRef = database.child("menu")
        val newItemKey = menuRef.push().key ?: return

        val file = foodImageUri?.let { uri ->
            val compressedBitmap = compressImage(uri)
            val tempFile = File.createTempFile("img_", ".jpg", cacheDir)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, tempFile.outputStream())
            tempFile
        }

        if (file != null) {
            uploadToImgBB(file) { url ->
                if (url != null) {
                    val newItem = AllMenu(
                        newItemKey,
                        foodName =foodName,
                        foodPrice = foodPrice,
                        foodDescription =foodDescription,
                        foodImage = url,
                        foodIngredient = foodIngredients
                    )

                    menuRef.child(newItemKey).setValue(newItem).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save item: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.e("Database", "Save failed", it.exception)
                        }
                    }
                } else {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
                file.delete()
            }
        } else {
            Toast.makeText(this, "Invalid image file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressImage(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val maxSize = 1024
        var width = bitmap.width
        var height = bitmap.height
        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        if (ratio < 1) {
            width = (width * ratio).toInt()
            height = (height * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun uploadToImgBB(imageFile: File, callback: (String?) -> Unit) {
        val apiKey = "69d7e8693af157b61e40bc5c55decea7" // Thay bằng API key từ ImgBB
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                imageFile.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
            )
            .build()
        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload?key=$apiKey")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ImgBB", "Upload failed: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@AddItemActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                Log.d("ImgBB", "Full response: $json")
                try {
                    val jsonObject = JSONObject(json)
                    if (jsonObject.getBoolean("success")) {
                        val url = jsonObject.getJSONObject("data").getString("url")
                        Log.d("ImgBB", "Extracted URL: $url")
                        runOnUiThread { callback(url) }
                    } else {
                        val error = jsonObject.optString("error", "Unknown error")
                        val status = jsonObject.optInt("status", 0)
                        Log.e("ImgBB", "API error: $error, Status: $status")
                        runOnUiThread {
                            Toast.makeText(this@AddItemActivity, "ImgBB error: $error", Toast.LENGTH_SHORT).show()
                            callback(null)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ImgBB", "JSON parse failed: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@AddItemActivity, "Failed to parse ImgBB response", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                }
            }
        })
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            foodImageUri = uri
            binding.selectedImage.setImageURI(uri)
        }
    }
}