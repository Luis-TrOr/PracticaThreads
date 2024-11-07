import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class ContadorLineas extends SimpleFileVisitor<Path> {

    private static AtomicInteger totalLineas = new AtomicInteger(0);
    private static AtomicInteger totalCaracteres = new AtomicInteger(0);

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        String name = file.toAbsolutePath().toString();

        if (name.toLowerCase().endsWith(".txt")) {
            Thread contador = new Thread(new LineCounter(name, totalLineas, totalCaracteres));
            contador.start();
            try {
                contador.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.printf("No se puede procesar: %30s%n", file.toString());
        return super.visitFileFailed(file, exc);
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.exit(2);
        }

        Path startingDir = Paths.get(args[0]);

        ContadorLineas contadorLineas = new ContadorLineas();
        Files.walkFileTree(startingDir, contadorLineas);

        System.out.printf("Total de líneas: %,d%n", totalLineas.get());
        System.out.printf("Total de caracteres: %,d%n", totalCaracteres.get());
    }
}
