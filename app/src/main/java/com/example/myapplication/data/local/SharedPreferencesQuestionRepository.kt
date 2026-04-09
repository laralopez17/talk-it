package com.example.myapplication.data.local

import android.content.Context
import com.example.myapplication.core.domain.model.Question
import com.example.myapplication.core.domain.ports.QuestionRepository

class SharedPreferencesQuestionRepository(
    context: Context
) : QuestionRepository {

    private val prefs = context.getSharedPreferences("talkit_prefs", Context.MODE_PRIVATE)

    override fun getQuestions(): List<Question> {
        val usedIds = prefs.getStringSet("used_questions", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

        return QuestionCatalog.questions.mapIndexed { index, question ->
            Question(
                id = index,
                text = question,
                used = usedIds.contains(index)
            )
        }
    }

    override fun saveUsedQuestionIds(usedIds: Set<Int>) {
        prefs.edit().putStringSet("used_questions", usedIds.map { it.toString() }.toSet()).apply()
    }

    override fun clearUsedQuestionIds() {
        prefs.edit().putStringSet("used_questions", emptySet()).apply()
    }
}
