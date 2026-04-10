import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class TextToByteaConvertor {
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String DEFAULT_OUTPUT_SUFFIX = "bytes";
    public static final int UNSIGNED_BYTE = 0xff;

    public TextToByteaConvertor() {}

    public static void main(String[] args) {
        try {
            TextToByteaConvertor convertor = new TextToByteaConvertor();
            convertor.run(args);
        } catch (Exception e) {
            System.err.println("Error:\n" + e.getMessage());
            System.exit(1);
        }
    }

    public void run(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Lack of arguments. Usage to run: java -cp src TextToByteaConvertor \"{{pathToInputFile}}\"");
        }

        String path = args[0];
        Path originalPath = Path.of(path).toAbsolutePath().normalize();
        if (!Files.exists(originalPath)) {
            throw new IOException("Provided path " + originalPath + " does not exist");
        }
        boolean isDirectory = Files.isDirectory(originalPath);

        if (isDirectory) {
            try (Stream<Path> originalFiles = Files.list(originalPath)) {
                for (Path originalFile : (Iterable<Path>) originalFiles.filter(Files::isRegularFile)::iterator) {
                    processConvertor(originalFile, isDirectory);
                }
            }
        } else if (Files.isRegularFile(originalPath)) {
            processConvertor(originalPath, isDirectory);
        } else {
            throw new IOException("The provided type of path " + originalPath + " seem to be neither directory nor regular file");
        }
    }

    private void processConvertor(Path filePath, boolean isDirectory) throws Exception {
        String fileText = getFileText(filePath);
        String fileName = getFileNameOnly(filePath);
        boolean bytesFile = isBytesFile(fileName);
        if (bytesFile) {
            if (!isDirectory) {
                throw new IOException("Provided file " + filePath + " is already a hex file");
            } else {
                System.out.println("Provided file " + filePath + " is already a hex file");
                return;
            }
        }

        Path parentPath = getParentPath(filePath);
        String byteaText = toByteaHex(fileText);
        Path byteaFile = saveByteaData(parentPath, fileName, byteaText);

        System.out.println("Inserted bytea hex text into " + byteaFile);
    }

    private String getFileText(Path file) throws IOException {
        if (!Files.exists(file)) {
            throw new IOException("File not found " + file);
        }

        return Files.readString(file, DEFAULT_ENCODING);
    }

    private String getFileNameOnly(Path file) {
        String fileName = "";
        String fullFileName = file.getFileName().toString();
        int lastDotPos = fullFileName.lastIndexOf('.');
        if (lastDotPos <= 0) {
            fileName = fullFileName.replace(".", "");
        } else {
            fileName = fullFileName.substring(0, lastDotPos);
        }

        return fileName;
    }

    private boolean isBytesFile(String fileName) {
        return fileName.contains(".bytes");
    }

    private Path getParentPath(Path file) throws IllegalArgumentException {
        Path parent = file.getParent();
        if (parent == null) {
            throw new IllegalArgumentException("File " + file + " has no parent path");
        }

        return parent;
    }

    private Path saveByteaData(Path parentPath, String fileName, String content) throws IOException {
        StringBuilder byteFileName = new StringBuilder();
        byteFileName.append(fileName);
        byteFileName.append(".");
        byteFileName.append(DEFAULT_OUTPUT_SUFFIX);
        byteFileName.append(".txt");

        Path byteFile = parentPath.resolve(byteFileName.toString());

        Files.writeString(
                byteFile,
                content,
                DEFAULT_ENCODING,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return byteFile;
    }

    private String toByteaHex(String fileText) {
        byte[] fileBytes = fileText.getBytes(DEFAULT_ENCODING);
        StringBuilder sb = new StringBuilder("\\x");

        for (byte b : fileBytes) {
            sb.append(String.format("%02x", b & UNSIGNED_BYTE));
        }

        return sb.toString();
    }
}
