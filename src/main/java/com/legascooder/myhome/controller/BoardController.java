package com.legascooder.myhome.controller;

import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.repository.BoardRepository;
import com.legascooder.myhome.service.BoardService;
import com.legascooder.myhome.validator.BoardValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller // 사용자의 요청을 받아서 백엔드의 초기 진입단계 설정
@RequestMapping("/board") // board라는 url에 매칭되는 클래스나 메소드를 수행하도록 하는 것 /board로 접근하는 url을 처리한다는것
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired //스프링의 디펜던시 인젝션을 사용하기위해서 기동될때 아래의 boardValidator의 인스턴스에 값이 저장됨
    private BoardValidator boardValidator;//PostMapping 에서 사용하기위해서 선언

    @GetMapping("/list") // HTTP Get 요청을 처리하고 FrontEnd에 정보를 호출할 때 사용 (리소스 요청) 데이터를 요청할때 사용
    // <브라우저 히스토리에 남기때문에 보안을 위해서는 중요한 정보를 다루면 안됨>
    // Get는 보여줘
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable,
                       @RequestParam(required = false, defaultValue = "") String searchText) { // Model에 데이터를 담을때 addAttribute메소드를 사용.
        //String search만 있을경우 해당 key값이 없으면 에러가 나기때문에 RequestParam을 주어서 key값이 없어도 에러가 안나게 변경
        //한 페이지에서 데이터 갯수를 얼마나 표현할 수 있는지 PageableDefault 어노테이션으로 사이즈 수정(데이터가 많다면 화면에 보일 수 있게끔 데이터갯수 조정 가능)
//        Page<Board> boards = boardRepository.findAll(pageable); // 기존에 list에 있는 내용을 다 보여줬다면 JPA기능의 Page코드를 사용해서 정보가 많을경우 보여주는 내용을 분할하기 위해서 Page코드 사용
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
        // BoardRepo에서 검색기능의 매개값을 받아올 수 있도록 정의 후 컨트롤러에서 구현
        // board/list?page=0 첫번째 페이지부터 확인할 수 있음 /board/list?page=0&size=2 라면 한 페이지에 나타낼 수 있는 데이터 수량을 의미

        int startPage = Math.max(1,boards.getPageable().getPageNumber() -4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() +4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards",boards); //"boards를 지정한 이름을 통해서 boards객체를 사용
        return "board/list";
    }

    @GetMapping("/form") // 위랑 똑같은 내용으로 데이터요청
    public String form(Model model, @RequestParam(required = false) Long id) {
        if(id == null){
            model.addAttribute("board", new Board());
        }else{
            Board board = boardRepository.findById(id).orElse(null);
            model.addAttribute("board", board);
        }
        return "board/form";
    }

    @PostMapping("/form") // Post는 추가작업을 수행하기 위해서 HTTPBody에 정보를 담에 보낼때 사용 (새로운 정보를 등록할때 주로 사용) 서버에 내용 전송
    //서버로 리로스를 생성하거나 업데이트하기위해 데이터를 보낼 때 사용함, 전송할 데이터를 body에 담아서 서버로 보냄
    //Post는 만들어줘/변경해줘
    public String postForm(@Valid Board board, BindingResult bindingResult, Authentication authentication) {
        // Valid 를 사용하기위해서 ModelAttribute에서 Valid 로 변경 사용이유는 Board클래스에 적음
        boardValidator.validate(board, bindingResult);
        if (bindingResult.hasErrors()) {//Board 클래스에서 선언한 Vaild annotation의 값이 2자리보다 크고 30자리보다 짧아야한다라는것을 적용
            return "board/form";
        }
//        Authentication a = SecurityContextHolder.getContext().getAuthentication(); 전역변수 사용해서도 getName를 가져올 수 있다.
        String username = authentication.getName();
        boardService.save(username, board);
//        boardRepository.save(board);  //save 메소드가 bord의 키값을 가리킴
        return "redirect:/board/list";
    }

}
