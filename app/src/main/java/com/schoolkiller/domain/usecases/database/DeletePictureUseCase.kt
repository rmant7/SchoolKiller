package com.schoolkiller.domain.usecases.database

import com.schoolkiller.data.entities.Picture
import com.schoolkiller.data.repositories.PictureRepository
import javax.inject.Inject

class DeletePictureUseCase @Inject constructor(
    private val pictureRepository: PictureRepository
) {
    suspend operator fun invoke(picture: Picture) {
        pictureRepository.deletePicture(picture)
    }
}