package com.example.test2.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.example.test2.model.entities.CitiesList
import com.example.test2.model.entities.CityItem
import com.example.test2.model.entities.FileData
import com.example.test2.model.entities.SettingsData
import com.example.test2.services.extensions.startAnimation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("StaticFieldLeak")
object FirebaseService {
    private val db = FirebaseDatabase.getInstance("https://androidfirebase-910e8-default-rtdb.europe-west1.firebasedatabase.app")
    private val storage = FirebaseStorage.getInstance("gs://androidfirebase-910e8.appspot.com")
    var context: Context? = null

    fun updateRemoteAsset(asset: CityItem, completion: (CityItem?, Exception?) -> Unit) {
        updateRemoteAssetRec(asset) { updatedAsset, error ->
            completion(updatedAsset, error)
        }
    }

    private fun updateRemoteAssetRec(asset: CityItem, completion: (CityItem?, Exception?) -> Unit) {
        if (asset.image?.downloadUrl != null && asset.image?.downloadUrl != "null") {
            if (asset.image?.downloadUrl != null) {
                uploadImage(asset.image) { error ->
                    if (error != null) {
                        completion(null, error)
                    } else {
                        uploadAsset(asset) { error ->
                            if (error != null) {
                                completion(null, error)
                            } else {
                                completion(asset, null)
                            }
                        }
                    }
                }
            }
        }
        else {
            uploadAsset(asset) { error ->
                if (error != null) {
                    completion(null, error)
                } else {
                    completion(asset, null)
                }
            }
        }
    }

    private fun uploadAsset(asset: CityItem, completion: (Exception?) -> Unit) {
        try {
            val ref = db.getReference("cities")
            val cityId = ref.push().key

            if (cityId != null) {
                asset.id = cityId
                ref.child(cityId).setValue(asset)
                    .addOnSuccessListener {
                        Toast.makeText(context, "City saved successfully!", Toast.LENGTH_LONG).show()
                        completion(null)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "City saving failure!", Toast.LENGTH_LONG).show()
                        completion(e)
                    }
            }

        } catch (_: Throwable) {
            completion(Exception("Unable to encode song asset"))
        }
    }

    private fun uploadImage(image: FileData?, completion: (Exception?) -> Unit) {
        try {
            val uniqueId = UUID.randomUUID().toString()
            val fileName = uniqueId
            val storageReference = storage.getReference("images/$fileName")

            storageReference.putFile(image?.downloadUrl!!.toUri())
                .addOnSuccessListener {
                    image.name = fileName
                    completion(null)
                }
                .addOnFailureListener {
                    completion(Exception("Fail uploading image!"))
                }
        } catch (error: Throwable) {
            completion(Exception("Unable to process image Uri"))
        }
    }

    fun getCities(citiesList: MutableList<CityItem> = ArrayList(), completion: (isLast: Boolean) -> Unit) {
        citiesList.clear()
        val ref = db.getReference("cities")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var count: Long = 0;
                    if (citiesList.count() != 0)
                        return
                    for (citySnapshot in snapshot.children) {
                        try {
                            count++
                            val city = citySnapshot.getValue(CityItem::class.java)
                            getCityImage(city) { uri, error ->
                                city?.image!!.downloadUrl = uri.toString()
                                citiesList.add(city!!)
                                completion(citiesList.count() == snapshot.childrenCount.toInt())
                            }
                        }
                        catch (e: Exception) {
                            throw Exception(e)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setSettings(settings: SettingsData, completion: (isSuccess: Boolean) -> Unit) {
        try {
            val ref = db.getReference("settings")

            if (settings.fontColor != "") {
                ref.child("fontColor").setValue(settings.fontColor)
                    .addOnSuccessListener {
                        completion(true)
                    }
            }

            if (settings.fontSize != "") {
                ref.child("fontSize").setValue(settings.fontSize)
                    .addOnSuccessListener {
                        completion(true)
                    }
            }
        } catch (_: Throwable) {
            completion(false)
        }
    }

    fun getSettings(settings: SettingsData, completion: (isSuccess: Boolean) -> Unit) {
        val ref = db.getReference("settings")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        settings.fontColor = snapshot.child("fontColor").getValue(String::class.java)
                        settings.fontSize = snapshot.child("fontSize").getValue(String::class.java)
                        completion(true)
                    }
                    catch (e: Exception) {
                        throw Exception(e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getCityImage(city: CityItem?, completion: (uri: Uri?, Exception?) -> Unit) {
        val storageRef = storage.reference.child("images/${city?.image!!.name}")

        storageRef
            .downloadUrl
            .addOnSuccessListener { uri ->
                completion(uri, null)
            }
            .addOnFailureListener { e ->
                completion(null, e)
            }
    }
}