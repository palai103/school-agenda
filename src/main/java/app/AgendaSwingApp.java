package app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import controller.AgendaController;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManager;
import repository.TransactionManagerMongo;
import service.AgendaService;
import view.cli.AgendaViewCli;
import view.swing.AgendaSwingView;

@Command(mixinStandardHelpOptions = true)
public class AgendaSwingApp implements Callable<Void> {

	private static final int MONGO_PORT = 27017;
	private static final String DB_COURSES_COLLECTION = "courses";
	private static final String DB_STUDENTS_COLLECTION = "students";
	private static final String MONGO_ADDRESS = "localhost";
	private static final String DB_NAME = "schoolagenda";

	@Option(names = { "--interface" }, description = "Interface type")
	private String interfaceType = "gui";

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
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				if ((!interfaceType.equals("gui") && !interfaceType.equals("cli"))) {
					System.err.println("Invalid value for optin --interface. Allowed values: are \"gui\" and \"cli\"");
					System.exit(1);
				}
				MongoClient mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				StudentMongoRepository studentMongoRepository = new StudentMongoRepository(mongoClient, DB_NAME,
						DB_STUDENTS_COLLECTION);
				CourseMongoRepository courseMongoRepository = new CourseMongoRepository(mongoClient, DB_NAME,
						DB_COURSES_COLLECTION);
				TransactionManager transactionManager = new TransactionManagerMongo(mongoClient, studentMongoRepository,
						courseMongoRepository);
				AgendaService agendaService = new AgendaService(transactionManager);
				if (interfaceType.equals("gui")) {
					AgendaSwingView agendaSwingView = new AgendaSwingView();
					AgendaController agendaController = new AgendaController(agendaSwingView, agendaService);
					agendaSwingView.setAgendaController(agendaController);
					agendaSwingView.setVisible(true);
					agendaController.getAllStudents();
					agendaController.getAllCourses();
				}
				if (interfaceType.equals("cli")) {
					LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
					Logger logger = loggerContext.getLogger("org.mongodb.driver");
					logger.setLevel(Level.OFF);
					AgendaViewCli agendaViewCli = new AgendaViewCli(System.in, System.out);
					AgendaController agendaController = new AgendaController(agendaViewCli, agendaService);
					agendaViewCli.inject(agendaController);
					agendaController.getAllStudents();
					agendaController.getAllCourses();
					int returnStatus;
					do {
						returnStatus = agendaViewCli.menuChoice();
					} while (returnStatus != -1);
				}
			}
		});
		return null;
	}
}
