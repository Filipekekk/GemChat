# GemChat - Twój Prywatny Asystent AI 🤖

GemChat to zaawansowana aplikacja mobilna na system Android, działająca jako inteligentny asystent oparty na modelu **Google Gemini**. Projekt wyróżnia się pełnym wsparciem dla multimodalności (analiza tekstu i obrazu) oraz bezkompromisowym podejściem do prywatności użytkownika (architektura *Local-First*).

## 🌟 Kluczowe Funkcje

* **Multimodalny Czat (Tekst + Vision):** Prowadź naturalne konwersacje z AI. Aplikacja pozwala na przesyłanie zdjęć bezpośrednio z galerii urządzenia – model zinterpretuje obraz i odpowie na pytania z nim związane.
* **Prywatność (Privacy-by-Design):** Żadne dane konwersacji nie są wysyłane na zewnętrzne serwery bazodanowe. Cała historia czatów, załączniki oraz metadane są bezpiecznie przechowywane w izolowanej, lokalnej bazie na urządzeniu użytkownika.
* **Zarządzanie Sesjami:** * Automatyczne grupowanie wiadomości w wątki (Conversations).
    * Możliwość przeglądania historii, wznawiania starszych rozmów oraz ich usuwania.
* **Personalizacja i Ustawienia:**
    * Przełączanie między modelami sztucznej inteligencji (np. szybki *Gemini Flash* vs dokładny *Gemini Pro*).
    * Obsługa dynamicznego motywu (Dark/Light mode).
    * Opcja całkowitego "wyczyszczenia" pamięci aplikacji jednym kliknięciem.

## 🛠 Technologie i Architektura

Aplikacja została zbudowana w 100% z wykorzystaniem nowoczesnego stosu technologicznego rekomendowanego przez Google dla systemu Android.

* **Język:** Kotlin
* **Architektura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Interfejs Użytkownika:** Jetpack Compose (w pełni deklaratywne UI)
* **Asynchroniczność i Stan:** Kotlin Coroutines, `StateFlow`, `Flow` (Reaktywne aktualizacje UI)
* **Lokalna Baza Danych:** Room Database (nakładka na SQLite)
* **Komunikacja Sieciowa:** Gemini API SDK / REST
* **Zarządzanie Obrazami:** Coil (asynchroniczne ładowanie z pamięci)

## 🗄 Struktura Danych i Repozytoria

Wzorzec Repozytorium zarządza przepływem informacji między lokalną bazą a chmurą AI.

### Baza Danych (Room)
Dane podzielone są na relacyjne encje: `Conversation` (wątek), `Message` (pojedyncza wiadomość) oraz `Attachment` (załączniki).
Kluczowe operacje w `ChatDao`:
* `insertMessage(message)`: Zapisuje nową wiadomość użytkownika lub odpowiedź AI.
* `getMessagesForConversation(conversationId)`: Zwraca reaktywny strumień (`Flow`) wiadomości, automatycznie odświeżając ekran czatu po każdej zmianie.
* `getAllConversations()`: Pobiera listę wszystkich wątków do ekranu głównego.
* `deleteConversationById(id)`: Bezpiecznie usuwa cały wątek wraz z kaskadowym usunięciem wiadomości.

### GeminiRepository
Moduł odpowiedzialny za formatowanie *promptów*, konwersję obrazów (Bitmap/Base64) i obsługę zapytań sieciowych do API Google w tle, nie blokując głównego wątku aplikacji.

## 🚀 Uruchomienie Projektu

Aby aplikacja mogła łączyć się z modelem, wymaga własnego klucza API.
1. Wygeneruj klucz w panelu [Google AI Studio](https://aistudio.google.com/).
2. W głównym katalogu projektu zlokalizuj (lub utwórz) plik `local.properties`.
3. Dodaj w nim linijkę z Twoim kluczem:
   ```properties
   GEMINI_API_KEY=twoj_wygenerowany_klucz_tutaj
   ```
4. Zbuduj i uruchom projekt w Android Studio.

## 📱 Projekt Ekranów (Mockups)

![Mockups1](Mockups1.png)
![Mockups2](Mockups2.png)

## ✨ Efekt końcowy (Zrzuty ekranu)

<div style="display: flex; gap: 10px;">
  <img width="270" height="585" alt="Chat Screen" src="LINK_DO_SCREENA_CZATU" />
  <img width="270" height="585" alt="Conversations List" src="LINK_DO_SCREENA_LISTY" />
  <img width="270" height="585" alt="Settings Screen" src="LINK_DO_SCREENA_USTAWIEN" />