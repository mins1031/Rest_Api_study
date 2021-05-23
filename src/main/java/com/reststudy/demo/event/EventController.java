package com.reststudy.demo.event;

import com.reststudy.demo.common.ErrorsResource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.hibernate.EntityMode;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    //private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));


        ModelMapper modelMapper = new ModelMapper();
        Event event = modelMapper.map(eventDto, Event.class);
        //ModelMapper는 dto -> 엔티티, 엔티티 -> dto 과정을 편리하게 진행할수 있게 해주는 라이브러리에 포함되 객체임.
        //위와 같이 modelMapper.map(바뀜 당할객체,바꿀객체)이렇게 정의하면 손쉽게 객체 형 변환 가능
        event.update();

        Event newEvent = eventRepository.save(event);
        Integer eventId = newEvent.getId();

        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class).slash(eventId);

        URI createdUri = linkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(newEvent);
        eventResource.add(linkTo(EventController.class).slash(eventId).withSelfRel());
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs.index.html#resources-event-create").withRel("profile"));
/**
 * 강의에서는 EntityResource를 spring hateos에 있는 ResourceSupport,Resource<T>를 이용했지만 버전이 바뀌며 둘다 사용x
 * EntityModel을 이용해 동일한 방식으로 클래스를 따로 생성하지 않아도 구현가능함.
 * */
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> resourcesAssembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pageResources = resourcesAssembler.toModel(page, e -> new EventResource(e));
        pageResources.add(new Link("/docs.index.html#resources-event-create").withRel("profile"));
        return ResponseEntity.ok(pageResources);
    }
    /*페이지를 Resource로 바꿔서 링크를 보내줘야하는데 그럴때 유용한게 스프링 Data jpa가 제공하는
    PagedResourcesAssembler임 PagedResourcesAssembler로 랩핑해 응답바디에 담아주면
    페이지와 관련된 링크들도 담아줌 현재,다음,이전,마지막,처음페이지등의 링크 그러나 그냥 Page만
    PagedResourcesAssembler로 변환하면 각각의 페이지 정보는 모르게됨. 그런경우
    PagedResourcesAssembler에 파라미터를 2개 줌.*/

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event findEvent = optionalEvent.get();
        EventResource eventResource = new EventResource(findEvent);
        eventResource.add(
                new Link("/docs.index.html#resources-event-create").withRel("profile"));
        return ResponseEntity.ok(eventResource);
        //왜바로 findEvent를 ok바디에 담지 않고 EventResource로 랩핑해 보내는가?
        //=> self에 대한 링크를 만드는 리소스가 EventResource여서 매번 linkTo()...하는 코드
        //작성보다 EventResource를 만들어놓고 사용하는게 코드중복 줄인 좋은코드.
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors){
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));

        eventValidator.validate(eventDto,errors);

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));

        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty())
            return ResponseEntity.notFound().build();

        Event event = optionalEvent.get();
        ModelMapper modelMapper = new ModelMapper();
        event = modelMapper.map(eventDto,Event.class);

        eventRepository.save(event);
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs.index.html#resources-event-create").withRel("profile"));
        eventResource.add(linkTo(methodOn(EventController.class).getEvent(id)).withRel("get-an-event"));
        return ResponseEntity.ok().body(event);
    }
}
