package repository;

@FunctionalInterface
public interface TriFunction<T, U, W, R> {
	public R apply(T t, U u, W w);
}
