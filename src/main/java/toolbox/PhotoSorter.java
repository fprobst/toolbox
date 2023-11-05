package toolbox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * select x.id_local, y.id_local, y.captureTime, y.fileFormat, y.fileWidth,
 * y.fileHeight, y.rootFile, a.originalFilename, z.name from
 * AgLibraryCollectionImage x
 * join Adobe_images y on x.image = y.id_local
 * join AgLibraryCollection z on x.collection = z.id_local
 * join AgLibraryFile a on y.rootFile = a.id_local
 */
public class PhotoSorter {

    private static Logger logger = Logger.getLogger("PhotoSorter");

    private final Pattern pattern = Pattern
            .compile("(\\d{3})\\-((\\d*)\\-(.*)|(.*)\\-(\\d*))\\.(nef|jpg|jpeg|avi|mpg|mpeg|mp4|mov|dng|nrw)");

    private final Map<String, SortedSet<Path>> files = new HashMap<>();

    private static final List<String> excludedDirectories = Arrays.asList("2010-01", "2010-02", "2010-03", "2010-04",
            "2010-05");

    private Map<String, SortedSet<Path>> findPhotos(Path folder) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                logger.info(String.format("Foto gefunden: %s", entry.toString()));
                if (Files.isDirectory(entry) && !excludedDirectories.contains(entry.getFileName().toString())) {
                    findPhotos(entry);
                }

                String fileName = entry.getFileName().toString();
                Matcher matcher = pattern.matcher(fileName.toLowerCase());

                final String sammlung;
                if (matcher.matches()) {
                    sammlung = matcher.group(1);
                } else if (Files.isDirectory(entry)) {
                    continue;
                } else {
                    sammlung = "XXX";
                }

                SortedSet<Path> sammlungFiles = files.get(sammlung);
                if (sammlungFiles == null) {
                    sammlungFiles = new TreeSet<>();
                    files.put(sammlung, sammlungFiles);
                }
                sammlungFiles.add(entry);
            }
        }
        return files;

    }

    public static void main(String[] args) {
        PhotoSorter sorter = new PhotoSorter();

        final Path targetFolder = Paths.get("/Users/Florian/Pictures/Kamera/Sammlung");

        Map<String, SortedSet<Path>> photos;
        try {
            photos = sorter.findPhotos(Paths.get("/Users/Florian/Pictures/Kamera/2015"));
            for (Entry<String, SortedSet<Path>> sammlung : photos.entrySet()) {

                Path sammlungFolder = Paths.get(targetFolder.toString(), sammlung.getKey());
                Files.createDirectories(sammlungFolder);

                for (Path source : sammlung.getValue()) {
                    Path target = Paths.get(sammlungFolder.toString(), source.getFileName().toString());
                    logger.info(String.format("Kopiere %s => %s", source.toString(), target.toString()));
                    Files.copy(source, target,
                            StandardCopyOption.REPLACE_EXISTING);

                    Files.writeString(Paths.get(sammlungFolder.toString(), "files.txt"),
                            String.format("%s%n", source.toString()),
                            StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
