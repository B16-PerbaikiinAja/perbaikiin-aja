package id.ac.ui.cs.advprog.perbaikiinaja.repository.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}