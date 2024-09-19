package com.schoolkiller.domain.usecases.database

import com.schoolkiller.data_Layer.entities.Picture
import com.schoolkiller.data_Layer.repositories.PictureRepository
import javax.inject.Inject

class AddPictureUseCase @Inject constructor(
    private val pictureRepository: PictureRepository
) {
    suspend operator fun invoke(picture: Picture) {
        pictureRepository.insertPicture(picture)
    }
}