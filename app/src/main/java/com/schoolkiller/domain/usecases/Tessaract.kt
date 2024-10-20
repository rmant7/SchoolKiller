package com.schoolkiller.domain.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.IOException
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor

/* In a Gradle project, you can communicate the required dependencies
(like Tesseract, Java, and other external tools) to users in several ways.
While Gradle can automatically manage Java/Kotlin libraries (like Tess4J)
as part of your build configuration, external dependencies (like Tesseract)
must be installed manually by the user
a requirement for this code to run is installing 'Tessaract'
I think a better place to mention it, is in our README.md file.
In addition, we can add custom Gradle tasks to check for required system dependencies
(like Tesseract) */

/* given an (image) file, extracts text out of it */
fun tessaractImage(imagePath: String): Result<String> {

    val imageFile = File(imagePath)
    // Get the tessdata path from environment or system-specific defaults
    val tessDataPath = System.getenv("TESSDATA_PREFIX") ?: getDefaultTessdataPath()

    // Initialize Tesseract instance
    val tesseract = TessBaseAPI()

    // Set the tessdata path and languages (e.g., "eng" for English, "heb" for Hebrew)
    tesseract.init(tessDataPath, "eng+heb") // Modify languages as per your requirements

    return try {
        // Load the image file as a Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        if (bitmap == null) {
            return Result.failure(IOException("Unable to decode image file"))
        }

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
fun tessaractPDF(pdfPath: String): Result<String> {

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
}

// Get the default tessdata path based on the operating system
fun getDefaultTessdataPath(): String {
    return when {
        isMacOS() -> "/opt/homebrew/share/tessdata" // Default for Homebrew on macOS
        isLinux() -> "/usr/local/share/tessdata" // Common Linux location
        isWindows() -> "C:\\Program Files\\Tesseract-OCR\\tessdata" // Common Windows location for tessdata
        else -> throw UnsupportedOperationException("Unsupported operating system")
    }
}

// Helper functions to identify the operating system
fun isMacOS(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("mac")
}

fun isLinux(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("nux")
}

fun isWindows(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("win")
}



/*import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.IOException

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.FileDescriptor

/* In a Gradle project, you can communicate the required dependencies
(like Tesseract, Java, and other external tools) to users in several ways.
While Gradle can automatically manage Java/Kotlin libraries (like Tess4J)
as part of your build configuration, external dependencies (like Tesseract)
must be installed manually by the user
a requirement for this code to run is installing 'Tessaract'
I think a better place to mention it, is in our README.md file.
In addition, we can add custom Gradle tasks to check for required system dependencies
(like Tesseract) */

/* Given an (image) file, extracts text out of it */
fun tessaractImage(imageFile: File): Result<String> {
    // Get the tessdata path from environment or system-specific defaults
    val tessDataPath = System.getenv("TESSDATA_PREFIX") ?: getDefaultTessdataPath()

    // Initialize Tesseract instance
    val tesseract = TessBaseAPI()

    // Set the tessdata path and languages (e.g., "eng" for English, "heb" for Hebrew)
    tesseract.init(tessDataPath, "eng+heb") // Modify languages as per your requirements

    return try {
        // Load the image file as a Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        if (bitmap == null) {
            return Result.failure(IOException("Unable to decode image file"))
        }

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

fun tessaractPDF(pdfFile: File): Result<String> {
    // Get system-specific or environment-configured paths
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
}

// Get the default tessdata path based on the operating system
fun getDefaultTessdataPath(): String {
    return when {
        isMacOS() -> "/opt/homebrew/share/tessdata" // Default for Homebrew on macOS
        isLinux() -> "/usr/local/share/tessdata" // Common Linux location
        isWindows() -> "C:\\Program Files\\Tesseract-OCR\\tessdata" // Common Windows location for tessdata
        else -> throw UnsupportedOperationException("Unsupported operating system")
    }
}

// Helper functions to identify the operating system
fun isMacOS(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("mac")
}

fun isLinux(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("nux")
}

fun isWindows(): Boolean {
    return System.getProperty("os.name").toLowerCase().contains("win")
}*/