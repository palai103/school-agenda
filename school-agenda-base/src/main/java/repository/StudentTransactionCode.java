package repository;

import java.util.function.Function;

@FunctionalInterface
public interface StudentTransactionCode<T> extends Function<StudentRepository, T> {

}
