package repository;

public interface TransactionManager {

	<T> T studentTransaction(StudentTransactionCode<T> code);

}
