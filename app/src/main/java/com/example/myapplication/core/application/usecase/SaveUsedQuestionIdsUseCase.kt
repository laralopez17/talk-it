package com.example.myapplication.core.application.usecase

import com.example.myapplication.core.domain.ports.QuestionRepository

class SaveUsedQuestionIdsUseCase(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(usedIds: Set<Int>) {
        questionRepository.saveUsedQuestionIds(usedIds)
    }
}
