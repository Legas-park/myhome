package com.legascooder.myhome.repository;

import com.legascooder.myhome.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>{

    List<Board> findByTitle(String title); // 타이틀로 검색하기위해서 인터페이스를 정의를 하면 구현은 알아서 됨

    List<Board> findByTitleOrContent(String title, String content); // 타이틀과 컨텐트 둘다 검색하기 위해서 검색하기 위해서 인터페이스 정의

    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
