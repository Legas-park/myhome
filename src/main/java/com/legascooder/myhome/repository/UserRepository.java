package com.legascooder.myhome.repository;

import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
