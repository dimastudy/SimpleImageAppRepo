package com.example.simplephotoapp.additional

//    fun downloadImage(imageUrl: String, activity: Activity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (!verifyPermission(activity)) {
//                return@launch
//            }
//            withContext(Dispatchers.Main) {
//                _downloadPhoto.value = true
//            }
//
//            Glide.with(activity)
//                .asBitmap()
//                .load(imageUrl)
//                .into(object : CustomTarget<Bitmap>(1920, 1080) {
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        val bitmap: Bitmap = resource
//                        saveMediaToStorage(bitmap, activity)
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        TODO("Not yet implemented")
//                    }
//                })
//
//        }
//    }



//    fun saveMediaToStorage(bitmap: Bitmap, activity: Activity) {
//        //Generating a file name
//        val filename = "${System.currentTimeMillis()}.jpg"
//
//        //Output stream
//        var fos: OutputStream? = null
//
//        //For devices running android >= Q
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            //getting the contentResolver
//            activity.contentResolver?.also { resolver ->
//
//                //Content resolver will process the contentvalues
//                val contentValues = ContentValues().apply {
//
//                    //putting file information in content values
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//
//                }
//
//                //Inserting the contentValues to contentResolver and getting the Uri
//                val imageUri: Uri? =
//                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//                //Opening an outputstream with the Uri that we got
//                fos = imageUri?.let { resolver.openOutputStream(it) }
//            }
//        } else {
//            //These for devices running on android < Q
//            //So I don't think an explanation is needed here
//            val imagesDir =
////                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//            val image = File(imagesDir, filename)
//            fos = FileOutputStream(image)
//        }
//
//        fos?.use {
//            //Finally writing the bitmap to the output stream that we opened
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//            viewModelScope.launch {
//                withContext(Dispatchers.Main) {
//                    _downloadPhoto.value = false
//                }
//            }
//        }
//    }
