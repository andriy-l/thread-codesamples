package ua.te.jug.concurrency.processhandle;

import java.lang.ProcessHandle.Info;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessHandleDemo {

	public static void main(String[] args) {
		ProcessHandle.allProcesses().limit(20).map(p -> p.info()).forEach(System.out::println);

		System.out.println("ALL descendants for this program parent's proces: ");

		ProcessHandle.current()
		.parent()  
		.get()
		.descendants() // 
		.forEach(System.out::println);

		System.out.println("INFO about ALL descendants for process with PID 1 (systemd or init in Linux) parent's proces: ");
		
		Optional<ProcessHandle> process = ProcessHandle.of(1);	
		Set<Info> descendants = process.get().descendants().map(ProcessHandle::info).collect(Collectors.toSet());
		descendants.forEach(System.out::println); 
	}
}
