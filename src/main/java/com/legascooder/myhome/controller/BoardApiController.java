package com.legascooder.myhome.controller;


import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
class BoardApiController { //RESTful api 만들기

    @Autowired
    private BoardRepository repository;

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/boards")
    List<Board> all(@RequestParam(required = false, defaultValue = "") String title, // 만약 title만 검색했을때 2번째 매개값인 content가 없으면 에러가 나기때문에 디폴트값으로 아무것도 없어도 에러가 안나게 지정해줌
                    @RequestParam(required = false, defaultValue = "") String content) { // id말고 title로도 호출가능하게 설정 , 추가로 content도 검색가능하게 테스트
        if(StringUtils.isEmpty(title) && StringUtils.isEmpty(content)){ // 2개를 같이 유효한지 검사해야하기 위해서 작성
            return repository.findAll(); //  /api/boards url을 요청하면 모든 데이터를 보여주게 만듬
        }else{
            return repository.findByTitleOrContent(title, content); // api/board?title=헬로 처럼 url을 요청하면 title랑 content 해당 데이터를 보여주게 만듬
        }

    }

    @GetMapping("/title")
    List<Board> all(){
        return repository.findAll(); //
    }
    // end::get-aggregate-root[]

    @PostMapping("/boards") // Post요청을 하면 새로운 데이터가 생기도록 설정
    Board newBoard(@RequestBody Board newBoard) { //클라이언트가 요청하는 json 형태의 HTTP Body내용을 JavaObject로 변환 newBoard에 내용을 변환해줌
        return repository.save(newBoard);
    }

    // Single item

    @GetMapping("/boards/{id}") // /api/boards/id = id에 해당하는 숫자를 요청하면 해당 데이터를 호출
    Board one(@PathVariable Long id) { // PathVariable 클라이언트가 요청하는 url에서 각 구분자에 들어오는 값을 처리하기 위함 // 이경우는 id에 해당되는 값을 처리하기위해 사용
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/boards/{id}")
    Board replaceBoard(@RequestBody Board newBoard, @PathVariable Long id) { // 수정할 내용을 newBoard에 담고 id를 찾아서 정보를 수정함

        return repository.findById(id) // 우선 수정해야되는 id를 찾고, title에 수정된 내용이랑 content에 수정된 내용을 newBoard오브젝트로 변환된 내용을 board에 저장
                .map(board -> {
                    board.setTitle(newBoard.getTitle());
                    board.setContent(newBoard.getContent());
                    return repository.save(board);
                })
                .orElseGet(() -> {
                    newBoard.setId(id);
                    return repository.save(newBoard);
                });
    }
    @Secured("ROLE_ADMIN") // 권한에 따른 삭제처리권한 대상 부여
    @DeleteMapping("/boards/{id}")
    void deleteBoard(@PathVariable Long id) {
        repository.deleteById(id);
    }
}