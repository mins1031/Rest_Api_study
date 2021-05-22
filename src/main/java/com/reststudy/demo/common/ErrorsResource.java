package com.reststudy.demo.common;

import com.reststudy.demo.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {

    public ErrorsResource(Errors errors, Link... links) {
        super(errors,links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
    /**
     * 기존 EventController의 EntityModel과 강의의 EventResource나 똑같이 응답객체를
     * 만들어 응답하는데;. hateos를 지키기 위해 Resource에 응답 객체와, 링크정보를 넣은거고
     * 해당 클래스인 ErrorsResource역시 에러를 응답하기 위해 에러와 링크정보를 담아서 출력해줌.
     * 여기서 에러의 정보와 에러상황시 필요한 링크는 메인페이지 = 인덱스페이지이기 때문에
     * 인덱스 정보역시 같이 담아주기에 "index"라는 이름으로 IndexController.class의 index
     * 메서드의 uri를 보내줌.
     * */
}
