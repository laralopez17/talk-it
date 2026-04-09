package com.example.myapplication.core.application.usecase

import com.example.myapplication.core.domain.ports.QuestionRepository

class ClearUsedQuestionIdsUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke() {
        questionRepository.clearUsedQuestionIds()
    }
}
