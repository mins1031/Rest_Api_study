package com.reststudy.demo.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
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
    public void createEvent() throws Exception {
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
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                ;
    }

    @Test
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

    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }
}
