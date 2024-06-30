#!/bin/bash

# Configuration
WEB_SERVICE_URL="http://localhost:8080"
UPLOAD_ENDPOINT="${WEB_SERVICE_URL}/api/files/upload"
LIST_ENDPOINT="${WEB_SERVICE_URL}/api/files/list"
DELETE_ENDPOINT="${WEB_SERVICE_URL}/api/files/delete"

# Function to verify folder structure
verify_folder_structure() {
  local folder=$1
  if [[ ! -d "$folder/input" || ! -d "$folder/jar" ]]; then
    echo "Error: Both 'input' and 'jar' folders must exist"
    return 1
  fi

  local input_files=$(ls "$folder/input")
  if [[ -z "$input_files" || ! $(echo "$input_files" | grep -E '\.csv$|\.parquet$') ]]; then
    echo "Error: 'input' folder should contain at least one CSV or Parquet file"
    return 1
  fi

  local jar_files=$(ls "$folder/jar")
  if [[ $(echo "$jar_files" | wc -l) -ne 1 || ! $(echo "$jar_files" | grep -E '\.jar$') ]]; then
    echo "Error: 'jar' folder should contain exactly one JAR file"
    return 1
  fi

  echo "Folder structure is correct"
  return 0
}

# Function to tar a folder for uploading
tar_folder() {
  local folder=$1

  verify_folder_structure "$folder"
  if [[ $? -ne 0 ]]; then
    return 1
  fi

  local tar_file="${folder}.tar"
  tar -cvf "$tar_file" -C "$folder" .
  if [[ $? -eq 0 ]]; then
    echo "Folder $folder successfully tared as $tar_file"
  else
    echo "Error: Failed to tar $folder"
    return 1
  fi
}

# Function to upload tar file to the web service
upload_tar_file() {
  local tar_file=$1

  response=$(curl -s -F "file=@${tar_file}" "${UPLOAD_ENDPOINT}")
  echo "Response from server: $response"
}

get_list_of_jars() {
  local response=$(curl -s http://localhost:8080/api/files/list)
  echo $response

}

# Function to delete a jar from the web service
delete_jar() {
  local jar_file_name=$1

  response=$(curl -s -X DELETE "${DELETE_ENDPOINT}/${jar_file_name}")
  echo "Response from server: $response"
}

# Function to show usage
show_usage() {
  echo "Usage: source spark-operator-file-upload.bashrc"
  echo "Functions:"
  echo "  verify_folder_structure <folder>       - Verify if the folder contains 'jar' and proper 'input' structure"
  echo "  tar_folder <folder>                    - Verify structure and tar the folder for uploading"
  echo "  upload_tar_file <tar_file>             - Upload the tar file to the web service"
  echo "  get_list_of_jars                       - Get a list of jars from the web service"
  echo "  delete_jar <jar_file_name>             - Delete a jar from the web service"
}

# Show usage if sourced without arguments
show_usage
