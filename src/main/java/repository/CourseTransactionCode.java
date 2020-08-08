package repository;

import java.util.function.BiFunction;

import com.mongodb.client.ClientSession;

@FunctionalInterface
public interface CourseTransactionCode<T>  extends BiFunction<CourseRepository, ClientSession, T> {

}
