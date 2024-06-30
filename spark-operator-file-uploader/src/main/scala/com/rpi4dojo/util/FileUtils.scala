package com.rpi4dojo.util

import java.io.{File, FileOutputStream, IOException}
import java.io._
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}

object FileUtils {

  def extractTar(tarPath: String, destPath: String): Unit = {
    val tais = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarPath)))
    var entry: TarArchiveEntry = tais.getNextTarEntry.asInstanceOf[TarArchiveEntry]

    val bufferSize = 4096 // Adjust buffer size as needed
    val buffer = new Array[Byte](bufferSize)

    while (entry != null) {
      val entryFile = new File(destPath, entry.getName)

      if (entry.isDirectory) {
        entryFile.mkdirs()
      } else {
        val outputFile = new FileOutputStream(entryFile)

        var len = tais.read(buffer)
        while (len != -1) {
          outputFile.write(buffer, 0, len)
          len = tais.read(buffer)
        }

        outputFile.close()
      }

      entry = tais.getNextTarEntry.asInstanceOf[TarArchiveEntry]
    }

    tais.close()
  }

  private def createDirectory(directory: File): Unit = {
    if (!directory.exists && !directory.mkdirs) {
      throw new IOException(s"Failed to create directory: ${directory.getAbsolutePath}")
    }
  }

  def deleteDirectory(directory: File): Unit = {
    if (directory.isDirectory) {
      directory.listFiles().foreach(deleteDirectory)
    }
    if (!directory.delete()) {
      throw new IOException(s"Failed to delete directory: ${directory.getAbsolutePath}")
    }
  }

  def getFolderSize(directory: File): Long = {
    if (directory.isDirectory) {
      directory.listFiles().map(_.length()).sum
    } else {
      0
    }
  }
}