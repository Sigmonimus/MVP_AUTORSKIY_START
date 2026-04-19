package com.example.mvp_autorskiy_start.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

// Интерфейс объявлен здесь, чтобы не было конфликта с отдельным файлом
// Если у вас есть отдельный файл WikipediaApiService.kt, удалите его или закомментируйте этот интерфейс.
interface WikipediaApiService {
    @GET("page/summary/{title}")
    suspend fun getSummary(@Path("title", encoded = true) title: String): WikipediaSummary
}

data class WikipediaSummary(
    val title: String,
    val extract: String?,
    val extract_html: String?
)

object DictionaryRepository {
    private const val BASE_URL = "https://ru.wikipedia.org/api/rest_v1/"
    private const val TAG = "DictionaryRepo"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WikipediaApiService::class.java)

    // Локальный словарь (офлайн, мгновенно)
    private val localDefinitions = mapOf(
        "гоголь" to "Николай Васильевич Гоголь (1809–1852) – русский писатель, драматург, критик. Автор «Мёртвых душ», «Ревизора», «Шинели».",
        "пушкин" to "Александр Сергеевич Пушкин (1799–1837) – великий русский поэт, основоположник современного русского литературного языка. Автор «Евгения Онегина», «Капитанской дочки».",
        "толстой" to "Лев Николаевич Толстой (1828–1910) – русский писатель, мыслитель. Автор «Войны и мира», «Анны Карениной», «Воскресения».",
        "достоевский" to "Фёдор Михайлович Достоевский (1821–1881) – русский писатель, мыслитель. Автор «Преступления и наказания», «Идиота», «Братьев Карамазовых».",
        "тургенев" to "Иван Сергеевич Тургенев (1818–1883) – русский писатель-реалист. Автор «Отцов и детей», «Записок охотника».",
        "лермонтов" to "Михаил Юрьевич Лермонтов (1814–1841) – русский поэт, прозаик. Автор «Героя нашего времени», «Мцыри».",
        "чехов" to "Антон Павлович Чехов (1860–1904) – русский писатель, драматург. Автор «Вишнёвого сада», «Чайки», рассказов.",
        "булгаков" to "Михаил Афанасьевич Булгаков (1891–1940) – русский писатель. Автор «Мастера и Маргариты», «Собачьего сердца».",
        "солженицын" to "Александр Исаевич Солженицын (1918–2008) – русский писатель, лауреат Нобелевской премии. Автор «Одного дня Ивана Денисовича», «Матрёниного двора».",
        "море" to "Море – часть Мирового океана, обособленная сушей или возвышениями подводного рельефа.",
        "любовь" to "Любовь – чувство глубокой привязанности и устремлённости к другому человеку или объекту.",
        "честь" to "Честь – комплекс моральных качеств, вызывающих уважение и гордость; достоинство, доброе имя.",
        "дружба" to "Дружба – личные бескорыстные взаимоотношения между людьми, основанные на доверии, искренности, общих интересах.",
        "война" to "Война – организованная вооружённая борьба между государствами, народами или социальными группами.",
        "семья" to "Семья – основанная на браке или кровном родстве малая группа, члены которой связаны общностью быта, взаимной помощью и моральной ответственностью."
    )

    suspend fun getDefinition(word: String): String {
        val lowerWord = word.trim().lowercase()
        // 1. Локальный словарь
        localDefinitions[lowerWord]?.let {
            Log.d(TAG, "Локальное определение для $word")
            return it
        }

        // 2. Fallback на Википедию
        val candidates = generateCandidates(word)
        for (candidate in candidates) {
            try {
                val encoded = URLEncoder.encode(candidate, "UTF-8")
                Log.d(TAG, "Запрос к Wikipedia: $encoded")
                val response = api.getSummary(encoded)
                if (!response.extract.isNullOrBlank()) {
                    val definition = response.extract.trim().replace(Regex("\\s+"), " ")
                    Log.d(TAG, "Найдено в Wikipedia: $candidate")
                    return definition
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() != 404) {
                    Log.e(TAG, "HTTP ${e.code()}: ${e.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка запроса", e)
            }
        }
        return "Определение не найдено"
    }

    private fun generateCandidates(word: String): List<String> {
        val trimmed = word.trim()
        val candidates = mutableListOf<String>()
        candidates.add(trimmed.replaceFirstChar { it.uppercase() })

        val specialMapping = mapOf(
            "гоголь" to "Николай Васильевич Гоголь",
            "пушкин" to "Александр Сергеевич Пушкин",
            "толстой" to "Лев Николаевич Толстой",
            "достоевский" to "Фёдор Михайлович Достоевский",
            "тургенев" to "Иван Сергеевич Тургенев",
            "лермонтов" to "Михаил Юрьевич Лермонтов",
            "чехов" to "Антон Павлович Чехов",
            "булгаков" to "Михаил Афанасьевич Булгаков",
            "солженицын" to "Александр Исаевич Солженицын"
        )
        specialMapping[trimmed.lowercase()]?.let {
            candidates.add(it)
            candidates.add(it.replace(" ", "_"))
        }
        return candidates.distinct()
    }
}