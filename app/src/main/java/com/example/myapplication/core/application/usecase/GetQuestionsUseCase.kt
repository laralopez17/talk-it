package com.example.myapplication.core.application.usecase

import com.example.myapplication.core.domain.model.Question
import com.example.myapplication.core.domain.ports.QuestionRepository

class GetQuestionsUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(): List<Question> = questionRepository.getQuestions()
}
