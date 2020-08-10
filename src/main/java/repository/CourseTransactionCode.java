package repository;

import java.util.function.Function;

@FunctionalInterface
public interface CourseTransactionCode<T>  extends Function<CourseRepository, T> {

}
