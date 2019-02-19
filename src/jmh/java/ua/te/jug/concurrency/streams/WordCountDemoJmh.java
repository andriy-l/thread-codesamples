package ua.te.jug.concurrency.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class WordCountDemoJmh {

	public static void main(String[] args) {

		Options options = new OptionsBuilder().include(WordCountDemo.class.getSimpleName()).forks(1).build();

		try {
			new Runner(options).run();
		} catch (RunnerException e) {
			e.printStackTrace();
		}
	}

	@State(Scope.Thread)
	public static class Dirs {		
		Path path = Paths.get("/docs/my_docs");
		List<PosixFilePermission> readPermissions = List.of(PosixFilePermission.GROUP_READ,
				PosixFilePermission.OTHERS_READ, PosixFilePermission.OWNER_READ);
	}
	
	@State(Scope.Thread)
	public static class FilePath {
		public Path path = Paths.get("/etc/passwd");
	}



	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public static List<Path> processedFilesParallel(Dirs dirs) {		
		try (Stream<Path> stream = Files.walk(dirs.path) ) {
			return stream.parallel()
					.filter(p -> {
						try {
							return Files.getPosixFilePermissions(p).containsAll(dirs.readPermissions);
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
					})
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}


	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public static List<Path> processedFilesSerial(Dirs dirs) {
		try (Stream<Path> stream = Files.walk(dirs.path)) {
			return stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}


	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public static List<String> getAllLinesParallel(FilePath fp) {
		try (Stream<String> lines = Files.lines(fp.path)) {
			return lines
					.parallel()
					.unordered()
					.flatMap(s -> Arrays.stream(s.split(",| "))) // tokenizing by comma or by space																							// space
					.filter(s -> Pattern.matches("\\D+", s)) // matches any character that's not a digit
					.map(String::toLowerCase)
					.map(s -> s.replaceAll("\\.|\\?|\\!|\\;|»|:|\\)|\\(|…|«|\"|'|(--)|\\*|\\[|\\]|/|_|`", ""))
					.map(String::trim).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public static List<String> getAllLinesSerial(FilePath fp) {
		try (Stream<String> lines = Files.lines(fp.path)) {
			return lines
					.unordered()
					.flatMap(s -> Arrays.stream(s.split(",| "))) // tokenizing by comma or by space
					.filter(s -> Pattern.matches("\\D+", s)) // matches any character that's not a digit
					.map(String::toLowerCase)
					.map(s -> s.replaceAll("\\.|\\?|\\!|\\;|»|:|\\)|\\(|…|«|\"|'|(--)|\\*|\\[|\\]|/|_|`", ""))
					.map(String::trim).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
