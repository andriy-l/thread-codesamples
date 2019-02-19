package ua.te.jug.concurrency.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WordCoundDemo {

	public static void main(String[] args) {
		Path path = Paths.get("texts");
        try {
			Files.walk(path)
			.filter(p -> p.getFileName().toString().endsWith(".txt"))
			.flatMap(WordCoundDemo::getAllLines)
			.flatMap(s -> Arrays.stream(s.split(",| ")))
			.filter(s -> Pattern.matches("\\D+", s))
			.map(String::toLowerCase)
			.map(s -> s.replaceAll("\\.|\\?|\\!|\\;|»|:|\\)|\\(|…|«|\"|'|(--)|\\*|\\[|\\]|/|_|`",""))
			.map(String::trim)
			.collect(Collectors.groupingBy(k -> k, TreeMap::new, Collectors.counting()))
			.forEach((k, v) -> System.out.println(k + " => " + v));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
    private static Stream<String> getAllLines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

}
