package com.television.myprojecttv

import java.io.Serializable

data class Movie(var id: Long = 0,
                 var title: String? = null,
                 var description: String? = null,
                 var backgroundImageUrl: String? = null,
                 var cardImageUrl: String? = null,
                 var videoUrl: String? = null,
                 var category: String? = null,
                 var studio: String? = null ):Serializable
