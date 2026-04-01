import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextToByteaConvertor {
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String DEFAULT_OUTPUT_SUFFIX = "bytes";
    public static final int UNSIGNED_BYTE = 0xff;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            System.out.println("Error:\n" + e.getMessage());
            System.exit(1);
        }
    }

    public static void run(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Lack of arguments. Usage to run: java -cp src TextToByteaConvertor \"{{pathToInputFile}}\"");
        }

        String path = args[0];
        Path originalFile = Path.of(path).toAbsolutePath().normalize();
        String fileText = getFileText(originalFile);
        String fileName = getFileNameOnly(originalFile);
        Path parentPath = getParentPath(originalFile);

        String byteaText = toByteaHex(fileText);
        Path byteaFile = saveByteaData(parentPath, fileName, byteaText);

        System.out.println("Inserted bytea hex text into " + byteaFile);
    }

    public static String getFileText(Path file) throws IOException {
        if (!Files.exists(file)) {
            throw new IOException("File not found " + file);
        }

        return Files.readString(file, DEFAULT_ENCODING);
    }

    public static String getFileNameOnly(Path file) {
        String fileName = "";
        String fullFileName = file.getFileName().toString();
        int lastDotPos = fullFileName.lastIndexOf('.');
        if (lastDotPos <= 0) {
            fileName = fullFileName;
        } else {
            fileName = fullFileName.substring(0, lastDotPos);
        }

        return fileName;
    }

    public static Path getParentPath(Path file) throws IllegalArgumentException {
        Path parent = file.getParent();
        if (parent == null) {
            throw new IllegalArgumentException("File " + file + " has no parent path");
        }

        return parent;
    }

    public static Path saveByteaData(Path parentPath, String fileName, String content) throws IOException {
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

    public static String toByteaHex(String fileText) {
        byte[] fileBytes = fileText.getBytes(DEFAULT_ENCODING);
        StringBuilder sb = new StringBuilder("\\x");

        for (byte b : fileBytes) {
            sb.append(String.format("%02x", b & UNSIGNED_BYTE));
        }

        return sb.toString();
    }
}
