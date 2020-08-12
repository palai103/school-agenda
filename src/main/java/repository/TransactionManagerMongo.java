package repository;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;

public class TransactionManagerMongo implements TransactionManager {

	private ClientSession clientSession;

	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private CourseMongoRepository courseMongoRepository;

	public TransactionManagerMongo(MongoClient client, StudentMongoRepository studentMongoRepository,
			CourseMongoRepository courseMongoRepository) {
		this.client = client;
		this.studentMongoRepository = studentMongoRepository;
		this.courseMongoRepository = courseMongoRepository;
	}

	@Override
	public <T> T studentTransaction(StudentTransactionCode<T> code) {
		T valueToReturn = null;
		clientSession = client.startSession();

		TransactionBody<T> transactionBody = () -> code.apply(studentMongoRepository);

		try {
			valueToReturn = clientSession.withTransaction(transactionBody);
		} catch (RuntimeException rte) {
			clientSession.abortTransaction();
		} finally {
			clientSession.close();
		}

		return valueToReturn;
	}

	@Override
	public <T> T courseTransaction(CourseTransactionCode<T> code) {
		T valueToReturn = null;
		clientSession = client.startSession();

		TransactionBody<T> transactionBody = () -> code.apply(courseMongoRepository);

		try {
			valueToReturn = clientSession.withTransaction(transactionBody);
		} catch (RuntimeException rte) {
			clientSession.abortTransaction();
		} finally {
			clientSession.close();
		}

		return valueToReturn;
	}

	@Override
	public <T> T compositeTransaction(TransactionCode<T> code) {
		T valueToReturn = null;
		clientSession = client.startSession();

		TransactionBody<T> transactionBody = () -> code.apply(studentMongoRepository, courseMongoRepository);

		try {
			valueToReturn = clientSession.withTransaction(transactionBody);
		} catch (RuntimeException rte) {
			clientSession.abortTransaction();
		} finally {
			clientSession.close();
		}

		return valueToReturn;
	}
}
