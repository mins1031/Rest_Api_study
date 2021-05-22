package com.reststudy.demo.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reststudy.demo.common.RestDocsConfiguration;
import com.reststudy.demo.common.TestDescription;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
/**
 * @WebMvcTest는 말 그대로 Mock == 가짜 객체를 사용하기 때문에 디비에 적용을 할수 없어 createEvent에서 만든
 * event값을 활용해서 뭔가 할수가 없음. 지금 주석을 작성하는 경우 event의 id값과 free,offline값의 적용을 안하는 과정을
 * 보여주기 위함이기에 @SpringBootTest,@AutoConfigureMockMvc 두개의 어노테이션을 사용한다고함. 뭔말이지;
 * 아마 기존 스프링 테스트는 MockMvc를 사용하려면 따로 어노테이션을 줘야하는데 @SpringBootTest는 기본값으로 Mock을 가지고 있어
 * 따로 설정안해도된다? 이런말인거 같은데 일단 감이 안옴. 암튼 더이상 테스트가 mocking되지 않기에 실제 리포지토리를 사용해 테스트가
 * 진행됨. 그래서 밑에서 작성한 Event객체의 id값,free,offline값은 실제 컨트롤러의 파라미터인 dto에 값이 없기에
 * 무시가 되고 리포지토리를 통해 테스트 실행후 성공하게됨.
 * */
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    //@MockBean
    //EventRepository eventRepository;
    //해당 컨트롤러는 웹용의 객체만 가지고 만들어주기때문에 실제 컨트롤러에 리포지 토리가 있다고 해도
    //컨트롤러에는 적용이 않됨. 그렇기에 가짜 빈을 가져오는 @MockBean어노테이션을 통해 가져와줌.
    /**
     * mockMvc.perform(요청을 줘야함.)
     * .contentType(MediaType.APPLICATION_JSON_UTF8) 요청본문에 json을 넘기고 있다
     * .accept(MediaTypes.HAL_JSON) HAL_JSON 응답을 원한다.라는걸 알려주는 메서드
     * .andDo(print()) : 응답을 로그에 찍어주는 메서드
     * .andExpect(jsonPath("id").exists() DB에 id가 있는지 확인해주는 구문
     *  요청은 Event객체를 줘야함. 요청본문에 어떻게 주느냐
     *  .content()를 통해 요청본문에 원하는 값을 넣어서 확인해줌. 그럼 또 본문에
     *  어떻게 json으로 변환해주냐? -> 예전에 jwt활용시 json데이터로 변환해주는
     *  ObjectMapper라는 클래스를 사용한적이 있는데 이번에 여기서도 활용함.
     *  springboot에서 기본 제공되는 클래스인듯
     * */
    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API STUDY")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        //Mockito.when(eventRepository.save(event)).thenReturn(event);
        //eventRepository.save가 호출되면 event를 리턴하라

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                //.andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
        .andDo(document("create-event",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("query-events").description("link to query event"),
                        linkWithRel("update-event").description("link to update an existing event"),
                        linkWithRel("profile").description("link to profile an existing event")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                        fieldWithPath("beginEventDateTime").description("beginEvenDateTime of new event"),
                        fieldWithPath("endEventDateTime").description("endEvenDateTime of new event"),
                        fieldWithPath("location").description("location of new event"),
                        fieldWithPath("basePrice").description("basePrice of new event"),
                        fieldWithPath("maxPrice").description("maxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("location header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                responseFields(
                        fieldWithPath("id").description("id of new event"),
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                        fieldWithPath("beginEventDateTime").description("beginEvenDateTime of new event"),
                        fieldWithPath("endEventDateTime").description("endEvenDateTime of new event"),
                        fieldWithPath("location").description("location of new event"),
                        fieldWithPath("basePrice").description("basePrice of new event"),
                        fieldWithPath("maxPrice").description("maxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                        fieldWithPath("free").description("free of new event"),
                        fieldWithPath("offline").description("offline of new event"),
                        fieldWithPath("eventStatus").description("eventStatus of new event"),
                        fieldWithPath("_links.self.href").description("_links.self.href of new event"),
                        fieldWithPath("_links.query-events.href").description("_links.query-event.href of new event"),
                        fieldWithPath("_links.update-event.href").description("_links.update-event.href of new event"),
                        fieldWithPath("_links.profile.href").description("_links.profile.href of new event")
                        )
        ))
        ;
    }

    @Test
    public void testFree(){
        //Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isTrue();

        //Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse();

        //Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse();

    }

    @Test
    public void testOffice(){
        //Given
        Event event = Event.builder()
                .location("강남역 네이버 D2 스타업 팩토리")
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue();
        //Given
        event = Event.builder()
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse();

    }

    @Test
    @TestDescription("입력 받을수 없는 값을 사용한 경우 에러발생하는 테스트")
    public void createEvent_badRequest() throws Exception{
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API STUDY")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    /** 요거는 값이 비어있는, 즉 @Valid에 걸리는 필드값이 비어있을때의 테스트*/
    @Test
    @TestDescription("입력값이 비어있는경우 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }

    /**
     * 요거는 값 자체가 이상한 값이 들어왔을때 ex) 시작하는 날이 끝나는 날보다 뒤일떄, 기본급이 최대급보다 클때 등등..
     * */
    @Test
    @TestDescription("입력값이 잘못된 경우 에러를 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API STUDY")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,26,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,24,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,23,14,21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기.")
    public void queryEvents() throws Exception{
        //Given
        IntStream.range(0,30).forEach(i -> {
            this.generateEvent(i);
        });
        //When
        this.mockMvc.perform(get("/api/events")
            .param("page","1")
            .param("size","10")
            .param("sort","name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                ;
    }

    private void generateEvent(int i) {
        Event event = Event.builder()
                .name("event "+ i)
                .description("test event")
                .build();

        this.eventRepository.save(event);
    }


}
