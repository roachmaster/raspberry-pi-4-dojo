package com.rpi4dojo.service

import com.rpi4dojo.config.ConfigLoader
import com.rpi4dojo.util.FileUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.io.File
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}

@Service
class FileUploadService {

  private val jarDir = ConfigLoader.getConfig("app.paths.jarDir")
  private val inputDir = ConfigLoader.getConfig("app.paths.inputDir")

  def uploadFile(file: MultipartFile): Unit = {
    val tempDir = Files.createTempDirectory("upload")
    val tempFile = new File(tempDir.toFile, file.getOriginalFilename)
    file.transferTo(tempFile)

    Try {
      FileUtils.extractTar(tempFile.getAbsolutePath, tempDir.toString)
      val inputFolder = new File(tempDir.toFile, "input")
      val jarFolder = new File(tempDir.toFile, "jar")

      if (!inputFolder.exists() || !jarFolder.exists() || !inputFolder.isDirectory || !jarFolder.isDirectory) {
        throw new IllegalArgumentException("Invalid tar structure: Missing input or jar folder")
      }

      val inputFiles = inputFolder.listFiles().filter(file => file.getName.endsWith(".csv") || file.getName.endsWith(".parquet"))
      if (inputFiles.isEmpty) {
        throw new IllegalArgumentException("Invalid input folder: Must contain at least one CSV or Parquet file")
      }

      val jarFiles = jarFolder.listFiles().filter(_.getName.endsWith(".jar"))
      if (jarFiles.length != 1) {
        throw new IllegalArgumentException("Invalid jar folder: Must contain exactly one jar file")
      }

      val jarFileName = jarFiles.head.getName
      val newInputFolder = new File(inputDir, jarFileName.stripSuffix(".jar"))
      if (newInputFolder.exists()) {
        FileUtils.deleteDirectory(newInputFolder)
      }

      inputFolder.renameTo(newInputFolder)
      println(Paths.get(jarDir, jarFileName).toAbsolutePath)

      Files.move(Paths.get(jarFiles.head.getAbsolutePath), Paths.get(jarDir, jarFileName))
    } match {
      case Success(_) => Files.delete(tempFile.toPath)
      case Failure(exception) =>
        FileUtils.deleteDirectory(tempDir.toFile)
        throw exception
    }
  }

  def listFiles(): String = {
    val jarDirectory = new File(jarDir)
    val response = new StringBuilder("Jars available:\n")

    if (jarDirectory.exists && jarDirectory.isDirectory) {
      val jarFiles = Option(jarDirectory.listFiles())
        .getOrElse(Array.empty[File])
        .filter(_.getName.endsWith(".jar"))

      if (jarFiles.isEmpty) {
        response.append("No jar files found.\n")
      } else {
        jarFiles.foreach { jarFile =>
          val inputFolder = new File(inputDir, jarFile.getName.stripSuffix(".jar"))
          val inputSize = FileUtils.getFolderSize(inputFolder)
          response.append(s"Jar: ${jarFile.getName}, Input Data Size: ${inputSize} bytes\n")
        }
      }
    } else {
      response.append(s"The directory $jarDir does not exist or is not a directory.\n")
    }

    response.toString()
  }

  def deleteFile(jarName: String): Unit = {
    val jarFile = new File(jarDir, jarName)
    if (!jarFile.exists() || !jarFile.getName.endsWith(".jar")) {
      throw new IllegalArgumentException(s"Jar file $jarName not found")
    }
    val inputFolder = new File(inputDir, jarName.stripSuffix(".jar"))
    if (inputFolder.exists()) {
      FileUtils.deleteDirectory(inputFolder)
    }

    if (jarFile.exists()) {
      jarFile.delete()
    }
  }
}