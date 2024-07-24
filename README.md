
# GRAPHGENIE

In this project, we created a server that the user can connect through the browser and visualize a computational graph created from a selected configuration, and see the flow of inforamation in the graph.

## Authors

 - [Boaz Gurevich](https://github.com/boazgur)
 - [Erel Dekel](https://github.com/Dekel23)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/Dekel23/Advanced-Programming-Java
cd Advanced-Programming-Java
cd Course_Project
```

2. Ensure Java JDK version 22 is installed:
```bash
java --version
```
If needed install JDK 22 using the following link:\
https://www.oracle.com/il-en/java/technologies/downloads/

Make sure you added java JDK 22 to PATH
## Running
The reposetory consisnt of 3 folders:
- Course_Project: Backend java code for server side
- html_files: Static and generated HTML files for client side
- config_files: Example configuration files to use

### To compile and run follow this steps:

1. Go to the java directory:
```bash
cd <Full_Path_To_Course_Project>
```
2. Compile the code:
```bash
& javac --enable-preview --release 22 -d bin -cp src $(Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
```
3. Run the code:
```bash
& java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp 'C:\Users\ereld\OneDrive\Documents\Erel_Studies\BIU - BEng Computer Engineering & Physics\year4\second\Advanced Programming - 83677\Project_Code\Course_Project\bin' Main
```
Now as the client, go to your browser and search for: http://localhost:8080/app/index.html \
Upload any configuration file that follows the rules specified in the file config_files/README.md\
Change content in any topic desired to visualize the effects

## API Reference

#### Get all items

```http
  GET /api/items
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |

#### Get item

```http
  GET /api/items/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

#### add(num1, num2)

Takes two numbers and returns the sum.

