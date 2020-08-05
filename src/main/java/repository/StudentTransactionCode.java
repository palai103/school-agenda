package repository;

import java.util.function.BiFunction;

import com.mongodb.client.ClientSession;

@FunctionalInterface
public interface StudentTransactionCode<T> extends BiFunction<StudentRepository, ClientSession, T> {

}
