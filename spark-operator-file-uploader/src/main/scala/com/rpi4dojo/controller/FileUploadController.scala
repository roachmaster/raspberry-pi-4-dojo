package com.rpi4dojo.controller

import com.rpi4dojo.service.FileUploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

import java.io.IOException

@RestController
@RequestMapping(Array("/api/files"))
class FileUploadController @Autowired()(fileUploadService: FileUploadService) {

  @PostMapping(Array("/upload"))
  @throws[IOException]
  def handleFileUpload(@RequestParam("file") file: MultipartFile): ResponseEntity[String] = {
    try {
      fileUploadService.uploadFile(file)
      new ResponseEntity[String]("File uploaded and processed successfully", HttpStatus.OK)
    } catch {
      case e: IllegalArgumentException =>
        new ResponseEntity[String](e.getMessage, HttpStatus.BAD_REQUEST)
      case e: Exception =>
        new ResponseEntity[String](e.getMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  @GetMapping(Array("/list"))
  def listFiles: ResponseEntity[String] = {
    new ResponseEntity[String](fileUploadService.listFiles(), HttpStatus.OK)
  }

  @DeleteMapping(Array("/delete/{jarName}"))
  def deleteFile(@PathVariable jarName: String): ResponseEntity[String] = {
    try {
      fileUploadService.deleteFile(jarName)
      new ResponseEntity[String](s"Jar $jarName and its input data deleted successfully", HttpStatus.OK)
    } catch {
      case e: IllegalArgumentException =>
        new ResponseEntity[String](e.getMessage, HttpStatus.BAD_REQUEST)
      case e: Exception =>
        new ResponseEntity[String](e.getMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
}
