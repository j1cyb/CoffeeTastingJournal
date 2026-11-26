package com.example.coffeetastingjournal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class CoffeeRepository {

    private val collection = FirebaseFirestore.getInstance().collection("coffees")

    fun add(coffee: Coffee, onSuccess: () -> Unit, onError: (String) -> Unit) {
        collection.add(coffee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to save coffee") }
    }

    fun update(coffee: Coffee, onSuccess: () -> Unit, onError: (String) -> Unit) {
        collection.document(coffee.id).set(coffee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update coffee") }
    }

    fun delete(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        collection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to delete coffee") }
    }

    fun listen(
        onChange: (List<Coffee>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return collection.orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Failed to load coffees")
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Coffee::class.java) ?: emptyList()
                onChange(list)
            }
    }
}