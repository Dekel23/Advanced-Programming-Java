
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
javac --enable-preview --release 22 -d bin -cp src "src\*.java"
```
3. Run the code:
```bash
java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp "bin" Main
```
Now as the client, go to your browser and search for: http://localhost:8080/app/index.html \
Upload any configuration file that follows the rules specified in the file [config_files/README.md](https://html-preview.github.io/?url=https://github.com/Dekel23/Advanced-Programming-Java/blob/main/config_files/README.md)\
Change content in any topic desired to visualize the effects

## API Reference

See API documentation in [Course_Project/docs/index.html](https://html-preview.github.io/?url=https://github.com/Dekel23/Advanced-Programming-Java/blob/main/Course_Project/docs/index.html)

