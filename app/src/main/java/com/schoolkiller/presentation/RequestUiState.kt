package com.schoolkiller.presentation

sealed interface RequestUiState {

    data object Initial : RequestUiState
    data object Loading : RequestUiState
    data object Success : RequestUiState
    data object Error : RequestUiState

}