package repository;

import com.mongodb.client.ClientSession;

@FunctionalInterface
public interface TransactionCode<T> extends TriFunction<ClientSession, StudentRepository, CourseRepository, T>{

}
