# Description

A small script for from text to hex of bytes series conversion, which is compatible with Postgresql `bytea` column datatype and could be used for quicker inserts during filling of the tables by data.
In a nutshell script does:
- Receives provided relative path of the file to read a content from
- Reads the content and transforms it to the hex representation of series of bytes compatible with `bytea` Postgresql datatype
- Creates a new file in the same parent folder as input file with a name format `{{fileName}}.bytes.txt`
- Writes hex of series of bytes to the new file

## Prerequisites

- [Java 25](https://www.oracle.com/pl/java/technologies/downloads/#java25)

## How to use

> Assuming run and compiled from the project root folder

### Compile
```bash
javac src/TextToByteaConvertor.java
```

### Run
```bash
java -cp src TextToByteaConvertor "{{relativePathToFile}}"
```

**Note**: relative path of the file will be considered from where the script is fired, e.g. for above example `"./test.svg"` will expect `test.svg` file be located in the root project directory, not `./src`. Hence the output `./test.bytes.txt` file will be created in the same root directory as well.

### Usage for Postgresql
The written in the new file hex could be copied and inserted directly into Postgresql bytea column

```sql
INSERT INTO test_table (bytes_data, mime_type)
VALUES (
  '{{receivedHex}}',
  'image/svg+xml'
);
```
