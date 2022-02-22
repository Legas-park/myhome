package com.legascooder.myhome.repository;

import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = { "boards" })
    List<User> findAll();
    // 원래 model.User 에서 LAZY만 사용했을경우 정보를 호출할경우 getFiedl를 호출할때 사용되는데 @EntityGraph를 사용하면 그런 Fetch타입을 무시하고 한번에 조인이 되어서 정보를 불러 올 수 있음
    // 여러개의 쿼리를 생성하지않고 한번에 확인

    User findByUsername(String username);

}
