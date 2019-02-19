package ua.te.jug.concurrency.firstjava;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.transform.stream.StreamSource;

/**
 * Java 1.0 - 1.2 approach: - for every task create thread ("thread-oriented") -
 * use lock (synchronization) around results - should use join()
 * 
 * Conclusions: - could be a lot of threads - thread creation and context
 * switching are not free - a bit complicated to handle it all
 *
 */
public class ThreadDemo {
	private final static String SEARCH_WORD = "java";
	private static long threadCount;
	private static long filesCount;
	private static long wordCount;

	// TODO: You should use Tests
	public static void main(String[] args) {
		Set<Path> results = new HashSet<>();

		List<Path> paths;
		try {
			paths = Files.list(Paths.get("/etc"))
					.peek(p -> filesCount++)
					.filter(p -> !Files.isDirectory(p))
					.filter(ThreadDemo::checkReadPermission)
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			paths = Collections.emptyList();
		}
		for (Path p : paths) {
			Thread t = new Thread(() -> {
				var found =  wordContains(p);
				if (found > 0) {
					synchronized (results) {
						results.add(p);
						wordCount += found;
					}
				}
				threadCount++;
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// TODO: we should use Loggers
		System.out.printf("Thread Nums: %d\n", threadCount);
		System.out.printf("All Files Nums: %d\n", filesCount);
		System.out.printf("Files contains: %d\n", results.size());
		System.out.printf("WordCount: %d\n", wordCount);
	}

	private static boolean checkReadPermission(Path p) {
		var readPermissions = List.of(PosixFilePermission.GROUP_READ,
				PosixFilePermission.OTHERS_READ, PosixFilePermission.OWNER_READ);
		try {
			return Files.getPosixFilePermissions(p).containsAll(readPermissions);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static long wordContains(Path p) {
		long found = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(p.toFile())))) {
			found = reader.lines()
					.flatMap(str -> Arrays.asList(str.split("\\s")).stream())
					.filter(str -> str.contains(SEARCH_WORD))
					.count();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return found;
	}

}
