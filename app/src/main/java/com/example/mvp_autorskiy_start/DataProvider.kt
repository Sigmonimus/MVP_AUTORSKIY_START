package com.example.mvp_autorskiy_start.data

import com.example.mvp_autorskiy_start.R

object DataProvider {
    fun getAuthors(): List<Author> {
        val pushkinWorks = listOf(
            Work(
                id = 101,
                title = "Евгений Онегин",
                summary = "Роман в стихах о молодом дворянине Евгении Онегине...",
                fullText = "Полный текст романа в стихах...",
                arguments = listOf(
                    Argument(
                        id = 1001,
                        title = "Любовь Татьяны",
                        workTitle = "Евгений Онегин",
                        author = "Александр Пушкин",
                        description = "Татьяна, воспитанная на французских романах, искренне полюбила Онегина и первая призналась ему в письме.",
                        fullText = "Подробное описание аргумента...",
                        categoryIds = listOf(1)  // ID категории "Любовь"
                    ),
                    Argument(
                        id = 1002,
                        title = "Дружба и предательство",
                        workTitle = "Евгений Онегин",
                        author = "Александр Пушкин",
                        description = "Онегин и Ленский были друзьями, но пустяковая ссора привела к дуэли и гибели Ленского.",
                        fullText = "Подробное описание...",
                        categoryIds = listOf(4)  // ID категории "Дружба"
                    )
                )
            ),
            Work(
                id = 102,
                title = "Капитанская дочка",
                summary = "Пётр Гринёв отправляется на службу...",
                fullText = "Полный текст повести...",
                arguments = listOf(
                    Argument(
                        id = 1003,
                        title = "Честь и достоинство",
                        workTitle = "Капитанская дочка",
                        author = "Александр Пушкин",
                        description = "Гринёв отказывается присягать Пугачёву, следуя наказу отца «береги честь смолоду».",
                        fullText = "Подробное описание...",
                        categoryIds = listOf(2)  // ID категории "Война/долг"
                    )
                )
            )
        )

        val pushkin = Author(
            id = 1,
            name = "Александр Пушкин",
            years = "1799–1837",
            bio = "Великий русский поэт, драматург и прозаик...",
            imageRes = R.drawable.pushkin_portrait,
            works = pushkinWorks
        )

        val tolstoyWorks = listOf(
            Work(
                id = 201,
                title = "Война и мир",
                summary = "Роман-эпопея о жизни русского общества...",
                fullText = "Полный текст романа...",
                arguments = listOf(
                    Argument(
                        id = 2001,
                        title = "Любовь Наташи и Андрея",
                        workTitle = "Война и мир",
                        author = "Лев Толстой",
                        description = "Наташа Ростова и князь Андрей – пример чистой любви, прошедшей через испытания.",
                        fullText = "Подробное описание...",
                        categoryIds = listOf(1)
                    )
                )
            )
        )

        val tolstoy = Author(
            id = 2,
            name = "Лев Толстой",
            years = "1828–1910",
            bio = "Великий русский писатель, мыслитель...",
            imageRes = R.drawable.tolstoy_portrait,
            works = tolstoyWorks
        )

        return listOf(pushkin, tolstoy)
    }
}