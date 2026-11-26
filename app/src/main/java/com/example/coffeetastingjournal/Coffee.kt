package com.example.coffeetastingjournal

import com.google.firebase.firestore.DocumentId

data class Coffee(
    @DocumentId val id: String = "",
    val name: String = "",
    val cafe: String = "",
    val origin: String = "",
    val roast: String = "",
    val rating: Int = 0,
    val notes: String = "",
    val date: String = ""
)