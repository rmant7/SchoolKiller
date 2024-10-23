package com.schoolkiller.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType.CV_8UC1
import org.opencv.android.Utils

fun preprocessImageForOCR(bitmap: Bitmap): Bitmap {
    // Convert Bitmap to OpenCV Mat object
    val srcMat = Mat()
    Utils.bitmapToMat(bitmap, srcMat)

    // Convert to grayscale
    val grayMat = Mat()
    Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY)

    // Apply Gaussian Blur to reduce noise
    Imgproc.GaussianBlur(grayMat, grayMat, Size(5.0, 5.0), 0.0)

    // Apply Otsu's Binarization
    val binarizedMat = Mat()
    Imgproc.threshold(grayMat, binarizedMat, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

    // Convert back to Bitmap
    val binarizedBitmap = Bitmap.createBitmap(binarizedMat.cols(), binarizedMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(binarizedMat, binarizedBitmap)

    return binarizedBitmap
}

fun resizeBitmap(bitmap: Bitmap, scaleFactor: Float): Bitmap {
    val width = (bitmap.width * scaleFactor).toInt()
    val height = (bitmap.height * scaleFactor).toInt()
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

fun applyMedianBlur(bitmap: Bitmap): Bitmap {
    // Convert the Bitmap to Mat
    val srcMat = Mat()
    Utils.bitmapToMat(bitmap, srcMat)

    // Apply Median Blur
    val blurredMat = Mat()
    Imgproc.medianBlur(srcMat, blurredMat, 5)  // Use a kernel size of 5

    // Convert Mat back to Bitmap
    val blurredBitmap = Bitmap.createBitmap(blurredMat.cols(), blurredMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(blurredMat, blurredBitmap)

    return blurredBitmap
}

fun applyBilateralFilter(bitmap: Bitmap): Bitmap {
    // Convert Bitmap to Mat
    val srcMat = Mat()
    Utils.bitmapToMat(bitmap, srcMat)

    // Check if the image is already in grayscale, if not, convert it
    var processedMat = Mat()
    if (srcMat.channels() == 1) {
        // Already grayscale, no need to convert
        processedMat = srcMat
    } else {
        // Convert to grayscale (1-channel image)
        Imgproc.cvtColor(srcMat, processedMat, Imgproc.COLOR_BGR2GRAY)
    }

    // Apply Bilateral Filter
    val filteredMat = Mat()
    Imgproc.bilateralFilter(processedMat, filteredMat, 9, 75.0, 75.0)  // Adjust parameters as needed

    // Convert Mat back to Bitmap
    val filteredBitmap = Bitmap.createBitmap(filteredMat.cols(), filteredMat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(filteredMat, filteredBitmap)

    return filteredBitmap
}

/* given an (image) file, extracts text out of it */
fun tessaractImage(context: Context, bitmap: Bitmap): Result<String> {

    val tessDataPath = context.filesDir.absolutePath  // The parent directory where tessdata is located

    //val processedBitmap = preprocessImageForOCR(bitmap)

    // Resize the image (for example, 2x enlargement)
    //val resizedBitmap = applyBilateralFilter(bitmap)

    // Initialize Tesseract instance
    val tesseract = TessBaseAPI()

    Timber.d("Init Tesseract")
    // Set the tessdata path and languages (e.g., "eng" for English, "heb" for Hebrew)
    // eng+heb+rus
    // changed to heb for hebrew ocr tests
    tesseract.init(tessDataPath, "eng+heb") // Modify languages as per your requirements

    // Set Page Segmentation Mode (PSM) here
    //tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO)

    // Set a character whitelist, if applicable
    //tesseract.setVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzםףןךץאבגדהוזחטיכלמנסעפצקרשת-=><.∑∏")

    return try {
        // Set the bitmap to Tesseract for OCR
        Timber.d("Set image to Tesseract")
        tesseract.setImage(bitmap)

        // Extract the text
        val extractedText = tesseract.utF8Text
        Timber.d("Text is extracted by Tesseract.")
        Result.success(extractedText)

    } catch (e: Exception) {
        // Handle any exceptions during OCR
        Timber.e(e, "Unexpected exception during ocr.")
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
    val tessDataFiles = arrayOf("eng.traineddata", "heb.traineddata", "rus.traineddata")

    val tessDataDir = File(destinationPath)
    // Create the tessdata directory if it doesn't exist
    if (!tessDataDir.exists()) {
        tessDataDir.mkdirs()  // Create the directory and any missing parent directories
    }

    for (file in tessDataFiles) {
        val destinationFile = File("$destinationPath/$file")
        if (!destinationFile.exists()) {
            assetManager.open("tessdata/$file").use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
}