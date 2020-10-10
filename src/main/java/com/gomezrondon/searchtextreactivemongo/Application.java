package com.gomezrondon.searchtextreactivemongo;


import com.gomezrondon.searchtextreactivemongo.entity.DocFile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.BaseStream;


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
		String mainPath = "C:\\temp";

		List<String> whitelist = List.of("txt", "sql", "java", "py", "bat", "csv", "kt", "kts");

		Flux<DocFile> filesWithContent = getFilesWithContent(whitelist, mainPath);
		Flux<DocFile> filesSinContent = getFilesSinContent(whitelist, mainPath);

		//here we drop the collection and save all records
		repository.deleteAll()
				.thenMany(repository.saveAll(filesWithContent))
				.thenMany(repository.saveAll(filesSinContent))
				.doOnComplete(()->System.out.println("Finished>>>>>"))
				.subscribe();

	}

	private Flux<DocFile> getFilesSinContent(List<String> whitelist, String mainPath) throws IOException {
		return getFilesFronDirectoryRecursive(mainPath)
				.map(p -> new File(String.valueOf(p)))
				.filter(File::isFile)
				.filter(file -> !whitelist.contains(getExtensionOfFile(file)))
				.map(file -> {
					String extension = getExtensionOfFile(file);
					return new DocFile(file.getName(), extension, file.getPath(), List.of());

				});
	}

	private Flux<DocFile> getFilesWithContent(List<String> whitelist, String mainPath) throws IOException {
		return getFilesFronDirectoryRecursive(mainPath)
				.map(p -> new File(String.valueOf(p)))
				.filter(File::isFile)
				.filter(file -> whitelist.contains(getExtensionOfFile(file)))
				.map(file -> {
					String extension = getExtensionOfFile(file);
					//		System.out.println(file.getAbsolutePath());
					List<String> lines = fromPath(file.toPath())
							.filter(word -> word.length() > 2)
							.collectList().block();
					return new DocFile(file.getName(), extension, file.getPath(), lines);

				});
	}

	private Flux<Path> getFilesFronDirectoryRecursive(String mainPath) {
		return Flux.using(() -> Files.walk(Path.of(mainPath)),
				Flux::fromStream,
				BaseStream::close
		);
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
		return Flux.using(() -> Files.lines(path, StandardCharsets.ISO_8859_1),
				Flux::fromStream,
				BaseStream::close
		);
	}

}




interface DocFileRepository extends ReactiveCrudRepository<DocFile, String> {

	//Flux<DocFile> findByNameContains(String s);
}
