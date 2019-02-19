package ua.te.jug.concurrency.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WordCountDemo {
	
	public static void main(String[] args) {
		
		processedFiles(Paths.get("texts")) 
		.parallelStream()
		.flatMap(path -> getAllLines(path).stream()) //looks stupid, but we should close stream in the method and collect elements 
		.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
		.forEach((k, v) -> System.out.println(k + " => " + v));

	}

	private static List<Path> processedFiles(Path path) {
		try(Stream<Path> stream = Files.walk(path)){
			return stream.parallel() // faster in parallel mode
					.filter(p -> p.getFileName().toString().endsWith(".txt"))
					.collect(Collectors.toList());
		} catch(IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
    
    private static List<String> getAllLines(Path path) {    	
        try(Stream<String> lines = Files.lines(path)) {
        	return lines
//        			.parallel()  //will be faster in sequential mode
        			.unordered()
        			.flatMap(s -> Arrays.stream(s.split(",| "))) // tokenizing by comma or by space
        			.filter(s -> Pattern.matches("\\D+", s)) // matches any character that's not a digit
        			.map(String::toLowerCase)
        			.map(s -> s.replaceAll("\\.|\\?|\\!|\\;|»|:|\\)|\\(|…|«|\"|'|(--)|\\*|\\[|\\]|/|_|`",""))
        			.map(String::trim)
        			.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
