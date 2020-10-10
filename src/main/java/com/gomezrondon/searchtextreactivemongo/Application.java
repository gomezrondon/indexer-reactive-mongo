package com.gomezrondon.searchtextreactivemongo;


import com.gomezrondon.searchtextreactivemongo.entity.DocFile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Stream;


@SpringBootApplication(proxyBeanMethods = false)
@EnableReactiveMongoRepositories
public class Application  implements CommandLineRunner {

	private final DocFileRepository repository;

	public Application(DocFileRepository repository) {
		this.repository = repository;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	//	folder.listFiles()
/*		try (Stream<Path> paths = Files.walk(Paths.get("C:\\temp"))) {
			paths
					.filter(Files::isRegularFile)
					.forEach(System.out::println);
		}*/

		List<String> whitelist = List.of("txt", "sql", "java", "py", "bat", "csv", "kt", "kts");


		String mainPath = "C:\\temp";
		Flux.fromStream(Files.walk(Paths.get(mainPath)))
				.map(p -> new File(String.valueOf(p)))
				.filter(File::isFile)
				.filter(file -> whitelist.contains(getExtensionOfFile(file)))
				.map(file -> {
					fromPath(file.toPath()).subscribe(System.out::println);
					return Mono.empty();
				})
				.subscribe();
				//.subscribe(System.out::println);

/*		try (Stream<Path> paths = Files.walk(Paths.get("C:\\temp\\test.txt"))) {
			Flux.fromStream(paths)
					//.filter(Files::isRegularFile)
					.map(l -> {
						fromPath(l).subscribe(System.out::println);
						return Mono.empty();
					}).subscribe();

		}*/

	//	Thread.sleep(5000);

	//	fromPath(Path.of("C:\\temp")).subscribe(System.out::println);

	}

	public static String getExtensionOfFile(File file)
	{
		String fileExtension="";
		// Get file Name first
		String fileName=file.getName();

		// If fileName do not contain "." or starts with "." then it is not a valid file
		if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0)
		{
			fileExtension=fileName.substring(fileName.lastIndexOf(".")+1);
		}

		return fileExtension;
	}

	private static Flux<String> fromPath(Path path) {
		return Flux.using(() -> Files.lines(path),
				Flux::fromStream,
				BaseStream::close
		);
	}

}




interface DocFileRepository extends ReactiveCrudRepository<DocFile, String> {

	Flux<DocFile> findByNameContains(String s);
}
