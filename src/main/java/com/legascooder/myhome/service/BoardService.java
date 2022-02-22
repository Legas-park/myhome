package com.legascooder.myhome.service;


import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.model.User;
import com.legascooder.myhome.repository.BoardRepository;
import com.legascooder.myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public Board save(String username, Board board){
        User user = userRepository.findByUsername(username);
        board.setUser(user);
        return  boardRepository.save(board);
    }
}
