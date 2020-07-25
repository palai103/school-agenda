package repository;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;

public class TransactionManagerMongo implements TransactionManager {

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
		ClientSession clientSession = client.startSession();

		try {
			clientSession.startTransaction();
			valueToReturn = code.apply(studentMongoRepository);
			clientSession.commitTransaction();
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
		ClientSession clientSession = client.startSession();

		try {
			clientSession.startTransaction();
			valueToReturn = code.apply(courseMongoRepository);
			clientSession.commitTransaction();
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
		ClientSession clientSession = client.startSession();
		
		try {
			clientSession.startTransaction();
			valueToReturn = code.apply(studentMongoRepository, courseMongoRepository);
			clientSession.commitTransaction();
		} catch (RuntimeException rte) {
			clientSession.abortTransaction();
		} finally {
			clientSession.close();
		}
		return valueToReturn;
	}
}
