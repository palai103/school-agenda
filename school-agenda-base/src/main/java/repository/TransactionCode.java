package repository;

import java.util.function.BiFunction;

@FunctionalInterface
public interface TransactionCode<T> extends BiFunction<StudentRepository, CourseRepository, T>{

}
