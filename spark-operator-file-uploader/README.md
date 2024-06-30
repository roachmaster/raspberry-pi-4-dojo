# SparkOperatorFileUploader
SparkOperatorFileUploader is a web service designed to handle the uploading of tar files containing a single JAR file and associated input data files (CSV or Parquet). The web service extracts the files, validates their structure, and organizes them for use with Spark. This service is designed to work with the Spark Operator deployed on-premises without using S3 or any other cloud buckets.

## Table of Contents
 - [Requirements](#requirements)
 - [Running via JAR](#running-via-jar)
 - [Running via Docker](#running-via-docker)
 - [Running on k3s](#running-on-k3s)
 - [cURL Examples](#curl-examples)
 - [Using the spark-operator-file-upload.bashrc Script](#using-the-spark-operator-file-uploadbashrc-script)
 - [Using Swagger to View the API](#using-swagger-to-view-the-api)
 - [Integration with Spark](#integration-with-spark)
 - [Spark Scala Example](#spark-scala-example)
 
## Requirements
 - Java 11 or higher
 - Maven
 - Docker
 - k3s cluster
 - cURL

## Running via JAR
Build the Project:
mvn clean package

Run the JAR:
java -jar target/SparkOperatorFileUploader-1.0-SNAPSHOT.jar

## Running via Docker
Build the Docker Image:
docker build -t spark-operator-file-uploader .
Run the Docker Container:
docker run -p 8080:8080 spark-operator-file-uploader

## Running on k3s
### Create a Namespace:
```sh
kubectl create namespace spark-operator
```
### Deploy the Web Service:
```sh
kubectl apply -f k3s/deployment.yaml
```
### Apply the Service:
```sh
kubectl apply -f k3s/service.yaml
```
### Create an Ingress:
```sh
kubectl apply -f k3s/ingress.yaml
```
## cURL Examples
### Upload a Tar File:
```sh
curl -F "file=@/path/to/your/tarfile.tar" http://spark-operator.example.com/upload
```

### Get List of Jars and Input Data:
```sh
curl http://spark-operator.example.com/list
```

### Delete a Jar:
```sh
curl -X DELETE http://spark-operator.example.com/delete/jar-file-name.jar
```

## Using the spark-operator-file-upload.bashrc Script
###Source the Script:
```sh
source spark-operator-file-upload.bashrc
```
### Verify Folder Structure:
```sh
verify_folder_structure /path/to/folder
```
### Tar a Folder for Uploading:
```sh
tar_folder /path/to/folder
```
### Upload Tar File to the Web Service:
```sh
upload_tar_file /path/to/folder.tar
```

### Get a List of Jars from the Web Service:
```sh
get_list_of_jars
```
### Delete a Jar from the Web Service:
```sh
delete_jar jar-file-name.jar
```
## Using Swagger to View the API
### Open Swagger UI:

Navigate to http://spark-operator.example.com/swagger in your web browser.

### Explore the API:
Use the interactive documentation to test the endpoints and understand their usage.


### Integration with Spark

#### Spark Scala Example
```scala
import org.apache.spark.sql.SparkSession
object SparkJob {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder
            .appName("Spark Job")
            .getOrCreate()

        // Define the paths based on the JAR name
        val jarName = "my-jar"
        val inputPath = s"/mnt/data/input/$jarName"
        val jarPath = s"/mnt/data/jar/$jarName.jar"

        // Load the input data
        val inputData = spark.read
            .parquet(inputPath)

        // Perform some operations
        inputData.show()

        // Stop the Spark session
        spark.stop()
    }
}
```
This example demonstrates how to load the input data associated with a JAR in a Spark job. The paths are constructed based on the JAR name, and the input data is read using the Spark session.

