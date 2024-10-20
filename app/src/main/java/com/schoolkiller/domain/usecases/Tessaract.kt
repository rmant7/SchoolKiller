package com.schoolkiller.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

/* given an (image) file, extracts text out of it */
fun tessaractImage(context: Context, bitmap: Bitmap): Result<String> {

    val tessDataPath = context.filesDir.absolutePath  // The parent directory where tessdata is located

    // Initialize Tesseract instance
    val tesseract = TessBaseAPI()

    // Set the tessdata path and languages (e.g., "eng" for English, "heb" for Hebrew)
    tesseract.init(tessDataPath, "eng+heb") // Modify languages as per your requirements

    return try {
        // Set the bitmap to Tesseract for OCR
        tesseract.setImage(bitmap)

        // Extract the text
        val extractedText = tesseract.utF8Text
        Result.success(extractedText)

    } catch (e: Exception) {
        // Handle any exceptions during OCR
        Result.failure(e)
    } finally {
        // End the Tesseract session to free up resources
        tesseract.end()
    }
}

/* given a (pdf) file, extracts text out of it */
/*fun tessaractPDF(pdfPath: String): Result<String> {

    val pdfFile = File(pdfPath)
    // Get the tessdata path from environment or system-specific defaults
    val tessDataPath = System.getenv("TESSDATA_PREFIX") ?: getDefaultTessdataPath()

    // Initialize Tesseract instance
    val tesseract = TessBaseAPI()

    // Set the datapath for Tesseract (path to tessdata folder)
    tesseract.init(tessDataPath, "eng+heb") // Use the languages you need

    // Collect OCR results from all pages
    val extractedText = StringBuilder()

    try {
        // Load the PDF document using Android's PdfRenderer
        val parcelFileDescriptor: ParcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        // Loop through all pages and perform OCR
        for (pageIndex in 0 until pdfRenderer.pageCount) {
            println("Processing page: ${pageIndex + 1}")

            // Open the current page
            val page = pdfRenderer.openPage(pageIndex)

            // Create a Bitmap to render the PDF page
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Render the page onto the Bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            // Perform OCR on the rendered Bitmap
            tesseract.setImage(bitmap)
            val result = tesseract.utF8Text

            // Append the result from the current page, separating pages with a line separator
            extractedText.append(result).append(System.lineSeparator())

            // Only add a line separator between pages, not after the last page
            if (pageIndex < pdfRenderer.pageCount - 1) {
                extractedText.append(System.lineSeparator())
            }

            // Print the extracted text from the current page
            println("OCR Result for page ${pageIndex + 1}: $result")
        }

        // Close the PDF document
        pdfRenderer.close()
        parcelFileDescriptor.close()

        return Result.success(extractedText.toString())

    } catch (e: IOException) {
        println("Error reading PDF file: ${e.message}")
        return Result.failure(e)

    } finally {
        tesseract.end()
    }
}

// Get the default library path based on the operating system
fun getDefaultLibraryPath(): String {
    return when {
        isMacOS() -> "/opt/homebrew/lib" // Default for Homebrew on macOS
        isLinux() -> "/usr/local/lib" // Common for Linux systems
        isWindows() -> "C:\\Program Files\\Tesseract-OCR\\" // Common Windows location for Tesseract
        else -> throw UnsupportedOperationException("Unsupported operating system")
    }
}*/

fun copyTessDataFiles(context: Context, destinationPath: String) {
    val assetManager = context.assets
    val tessDataFiles = arrayOf("eng.traineddata", "heb.traineddata")

    val tessDataDir = File(destinationPath)
    // Create the tessdata directory if it doesn't exist
    if (!tessDataDir.exists()) {
        tessDataDir.mkdirs()  // Create the directory and any missing parent directories
    }

    for (file in tessDataFiles) {
        val destinationFile = File(destinationPath + file)
        if (!destinationFile.exists()) {
            assetManager.open("tessdata/$file").use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
}