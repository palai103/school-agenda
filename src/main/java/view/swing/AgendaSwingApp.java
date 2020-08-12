package view.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import controller.AgendaController;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManager;
import repository.TransactionManagerMongo;
import service.AgendaService;

@Command(mixinStandardHelpOptions = true)
public class AgendaSwingApp implements Callable<Void> {

	private static final int MONGO_PORT = 27017;
	private static final String DB_COURSES_COLLECTION = "courses";
	private static final String DB_STUDENTS_COLLECTION = "students";
	private static final String MONGO_ADDRESS = "locahost";
	private static final String DB_NAME = "schoolagenda";

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = MONGO_ADDRESS;

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = MONGO_PORT;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = DB_NAME;

	@Option(names = { "--db-students-collection" }, description = "Student collection name")
	private String studentCollectionName = DB_STUDENTS_COLLECTION;

	@Option(names = { "--db-courses-collection" }, description = "Courses collection name")
	private String courseCollectionName = DB_COURSES_COLLECTION;

	public static void main(String[] args) {
		new CommandLine(new AgendaSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				StudentMongoRepository studentMongoRepository = new StudentMongoRepository(mongoClient, DB_NAME,
						DB_STUDENTS_COLLECTION);
				AgendaSwingView agendaSwingView = new AgendaSwingView();
				CourseMongoRepository courseMongoRepository = new CourseMongoRepository(mongoClient, DB_NAME,
						DB_COURSES_COLLECTION);
				TransactionManager transactionManager = new TransactionManagerMongo(mongoClient, studentMongoRepository,
						courseMongoRepository);
				AgendaService agendaService = new AgendaService(transactionManager);
				AgendaController agendaController = new AgendaController(agendaSwingView, agendaService);
				agendaSwingView.setAgendaController(agendaController);
				agendaSwingView.setVisible(true);
				agendaController.getAllStudents();
				agendaController.getAllCourses();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}

}
