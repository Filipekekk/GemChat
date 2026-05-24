/**
 * Plik Entities.kt definiuje modele danych (encje), które są mapowane na tabele w lokalnej bazie danych Room.
 * Zawiera klasy reprezentujące Konwersacje oraz Wiadomości.
 */
package com.gemchat.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Klasa reprezentująca tabelę 'conversations' (konwersacje).
 * Przechowuje podstawowe informacje o pojedynczym czacie.
 */
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unikalny identyfikator konwersacji, generowany automatycznie.
    val title: String, // Tytuł konwersacji wyświetlany na liście.
    val lastMessage: String = "", // Skrót ostatniej wiadomości do podglądu na liście.
    val timestamp: Long = System.currentTimeMillis() // Czas ostatniej aktywności (używany do sortowania).
)

/**
 * Klasa reprezentująca tabelę 'messages' (wiadomości).
 * Wykorzystuje klucz obcy (ForeignKey) do powiązania wiadomości z konkretną konwersacją.
 * onDelete = ForeignKey.CASCADE oznacza, że usunięcie konwersacji usunie wszystkie jej wiadomości.
 */
@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Conversation::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unikalny identyfikator wiadomości.
    val conversationId: Long, // ID konwersacji, do której należy ta wiadomość.
    val text: String, // Treść tekstowa wiadomości.
    val isFromUser: Boolean, // Flaga określająca, czy wiadomość pochodzi od użytkownika (true) czy od AI (false).
    val localImagePath: String? = null, // Opcjonalna ścieżka do lokalnej kopii zdjęcia zapisanego w pamięci aplikacji.
    val timestamp: Long = System.currentTimeMillis() // Czas wysłania wiadomości.
)