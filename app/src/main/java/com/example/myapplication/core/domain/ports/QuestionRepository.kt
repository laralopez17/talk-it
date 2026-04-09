package com.example.myapplication.core.domain.ports

import com.example.myapplication.core.domain.model.Question

interface QuestionRepository {
    fun getQuestions(): List<Question>
    fun saveUsedQuestionIds(usedIds: Set<Int>)
    fun clearUsedQuestionIds()
}
