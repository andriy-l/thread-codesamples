package ua.te.jug.concurrency.streams;

import java.util.List;
import java.util.stream.Stream;

public class StreamDemo {

	public static void main(String[] args) {
		
		// @see 
		List.of("a1", "a2", "b1", "c2", "c1")
			.stream()
			.parallel()
		    .filter(s -> s.startsWith("c"))
		    .sequential()
		    .map(String::toUpperCase)
		    .parallel()
		    .sorted()
		    .forEach(System.out::println);
	}

}
