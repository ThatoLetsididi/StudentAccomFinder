package com.example.studentaccomfinder.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object GlideHelper {

    /**
     * Loads an accommodation image into an ImageView.
     * PRIMARY USE: Local drawable resources (R.drawable.house_1, etc.)
     * FALLBACK: Can also load from URL string or file URI.
     */
    fun loadAccommodationImage(
        context: Context,
        imageView: ImageView,
        imageSource: String?
    ) {
        val requestOptions = RequestOptions()
            .placeholder(Constants.PLACEHOLDER_IMAGE_RES)
            .error(Constants.ERROR_IMAGE_RES)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()

        // Determine if this is a local drawable reference
        val resourceId = if (!imageSource.isNullOrEmpty() && 
            !imageSource.startsWith("http") && 
            !imageSource.startsWith("content") &&
            !imageSource.startsWith("/")) {
            context.resources.getIdentifier(
                imageSource,
                "drawable",
                context.packageName
            ).takeIf { it != 0 }
        } else null

        val loadTarget: Any? = when {
            resourceId != null -> resourceId
            !imageSource.isNullOrEmpty() && (
                imageSource.startsWith("http") || 
                imageSource.startsWith("content") || 
                imageSource.startsWith("/")
            ) -> imageSource
            else -> null
        }

        Glide.with(context)
            .load(loadTarget)
            .apply(requestOptions)
            .into(imageView)
    }

    /**
     * Loads image from a local URI (for provider-uploaded gallery images)
     */
    fun loadLocalImage(
        context: Context,
        imageView: ImageView,
        localUri: Uri?
    ) {
        val requestOptions = RequestOptions()
            .placeholder(Constants.PLACEHOLDER_IMAGE_RES)
            .error(Constants.ERROR_IMAGE_RES)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()

        Glide.with(context)
            .load(localUri)
            .apply(requestOptions)
            .into(imageView)
    }

    /**
     * Preloads image for smoother scrolling in RecyclerView
     */
    fun preloadImage(context: Context, imageSource: String?) {
        if (imageSource.isNullOrEmpty()) return

        val resourceId = if (!imageSource.startsWith("http") && 
            !imageSource.startsWith("content") &&
            !imageSource.startsWith("/")) {
            context.resources.getIdentifier(imageSource, "drawable", context.packageName)
                .takeIf { it != 0 }
        } else null

        val loadTarget: Any? = when {
            resourceId != null -> resourceId
            imageSource.startsWith("http") || 
            imageSource.startsWith("content") || 
            imageSource.startsWith("/") -> imageSource
            else -> null
        }

        if (loadTarget != null) {
            Glide.with(context)
                .load(loadTarget)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload()
        }
    }

    /**
     * Generates a drawable resource name for sample listings
     */
    fun getSampleImageName(index: Int): String {
        return "${Constants.DRAWABLE_PREFIX}$index"
    }
}
