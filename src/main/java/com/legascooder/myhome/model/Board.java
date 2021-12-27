package com.legascooder.myhome.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size; // 스프링부트 업데이트되면서 Vaildation파일이 분리뒤어서 따로 의존성 추가해줌
//지금 사용하는것은 사용자가 잘못된 정보를 서버에 요청하면 그것을 안전하게 막아주려는 보안장치 ex) 해킹
//https://stackoverflow.com/questions/13649015/error-in-java-import-statement-the-import-javax-validation-constraints-notnull 참고함

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min=2, max=30, message = "제목은 2자 이상 30자 이하여야 합니다.")
    private String title;
    private String content;

}
