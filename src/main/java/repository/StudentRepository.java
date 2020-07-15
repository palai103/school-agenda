package repository;

import java.util.List;

import model.Student;

public interface StudentRepository {

	List<Student> findAll();

	Student findById(String id);

}
