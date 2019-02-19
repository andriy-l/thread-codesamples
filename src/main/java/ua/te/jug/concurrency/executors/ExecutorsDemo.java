package ua.te.jug.concurrency.executors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Since Java 5 approach: - "task-oriented" - use executors to run tasks - use
 * concurrent collections to store results
 * 
 *
 */
public class ExecutorsDemo {
	private final static String SEARCH_WORD = "java";
	private static long filesCount;

	// TODO: You should use Tests
	public static void main(String[] args) {
		Set<Path> results = new ConcurrentSkipListSet<>();
		long globalWordCount = 0;

		List<Path> paths;
		try {
			paths = Files.list(Paths.get("/etc")).peek(p -> filesCount++).filter(p -> !Files.isDirectory(p))
					.filter(ExecutorsDemo::checkReadPermission).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			paths = Collections.emptyList();
		}

		List<Callable<Long>> tasks = new ArrayList<>();
		int numProcs = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService executor = Executors.newFixedThreadPool(numProcs); // newCachedThreadPool();

		for (Path p : paths) {
			tasks.add(() -> {
				var wc = wordContains(p);
				if (wc > 0) {
					results.add(p);
				}
				return wc;
			});
		}

		List<Future<Long>> futureResults;
		try {
			futureResults = executor.invokeAll(tasks);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			futureResults = null;
		}
		
		for (Future<Long> f : futureResults) {
			try {
				globalWordCount += f.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();

		System.out.printf("Thread Nums: %d\n", numProcs);
		System.out.printf("All Files Nums: %d\n", filesCount);
		System.out.printf("Files contains: %d\n", results.size());
		System.out.printf("WordCount: %d\n", globalWordCount);
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
		// we also can use Files utility class here
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(p.toFile())))) {
			found = reader.lines().flatMap(str -> Arrays.asList(str.split("\\s")).stream())
					.filter(str -> str.contains(SEARCH_WORD)).count();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return found;
	}

}
