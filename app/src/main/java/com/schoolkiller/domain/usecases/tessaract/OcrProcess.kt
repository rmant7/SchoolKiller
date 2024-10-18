import java.io.File
import java.util.*

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
fun processImage(ImageFile: File) {
    // Get system-specific or environment-configured paths
    val tessLibraryPath = System.getenv("TESSERACT_LIBRARY_PATH") ?: getDefaultLibraryPath()
    println("lib path: ${tessLibraryPath}")
    val tessDataPath = System.getenv("TESSDATA_PREFIX") ?: getDefaultTessdataPath()
    println("prefix path: ${tessDataPath}")

    // Set the paths only if necessary
    System.setProperty("jna.library.path", tessLibraryPath)

    // Initialize Tesseract instance
    val tesseract = Tesseract()

    // Set the tessdata path dynamically
    tesseract.setDatapath(tessDataPath)

    // Optionally, set the Tesseract language (e.g., "heb" for Hebrew)
    tesseract.setLanguage("heb+eng")

    try {

        // Perform OCR on the image
        val result = tesseract.doOCR(imageFile)

        // Print the extracted text
        println("OCR Result: $result")
    } catch (e: TesseractException) {
        println("Error during OCR: ${e.message}")
    }
}

/* given a (pdf) file, extracts text out of it */
fun processPDF(pdfFile: File) {

    // Get system-specific or environment-configured paths
    val tessLibraryPath = System.getenv("TESSERACT_LIBRARY_PATH") ?: getDefaultLibraryPath()
    println("lib path: ${tessLibraryPath}")
    val tessDataPath = System.getenv("TESSDATA_PREFIX") ?: getDefaultTessdataPath()
    println("prefix path: ${tessDataPath}")

    // Set the path to your Tesseract installation (if needed)
    System.setProperty("jna.library.path", "/opt/homebrew/lib")

    // Initialize Tesseract instance
    val tesseract = Tesseract()

    // Set the datapath for Tesseract (path to tessdata folder)
    tesseract.setDatapath("/opt/homebrew/share/tessdata/")

    // Optionally, set the Tesseract language (e.g., "heb" for Hebrew)
    tesseract.setLanguage("heb+eng")

    try {

        // Load PDF document using PDFBox
        val document: PDDocument = PDDocument.load(pdfFile)

        // Create PDF renderer to convert PDF pages to images
        val pdfRenderer = PDFRenderer(document)

        // Loop through all pages and perform OCR
        for (page in 0 until document.numberOfPages) {
            println("Processing page: ${page + 1}")

            // Convert each PDF page to an image
            val bufferedImage: BufferedImage = pdfRenderer.renderImageWithDPI(page, 300f) // 300 DPI for better quality

            // Optionally save the image for verification
            val outputFile = File("src/main/resources/page_${page + 1}.png")
            ImageIO.write(bufferedImage, "png", outputFile)

            // Perform OCR on the image
            val result = tesseract.doOCR(bufferedImage)

            // Print the extracted text from the current page
            println("OCR Result for page ${page + 1}: $result")
        }

        // Close the PDF document
        document.close()

    } catch (e: IOException) {
        println("Error reading PDF file: ${e.message}")
    } catch (e: TesseractException) {
        println("Error during OCR: ${e.message}")
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