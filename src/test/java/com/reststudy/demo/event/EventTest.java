package com.reststudy.demo.event;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest
public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("spring rest API study")
                .description("REST API development")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){

        //Given
        String name = "Event";
        String description = "spring";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }


    //->>
    private Object[] parametersForTestFree(){
        return new Object[] {
                new Object[] {0,0,true},
                new Object[] {100,0,false},
                new Object[] {0,100,false},
                new Object[] {100,200,false}
        };
    }
    @Test
    /*@Parameters({
            "0, 0, true",
            "100, 0 ,false",
            "0, 100, true"
    }) -> 이렇게 작성하면 타입적으로 안전하지 않음*/
    @Parameters(method = "parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestOffice(){
        return new Object[] {
                new Object[] {"강남",true},
                new Object[] {null,false},
                new Object[] {" ",false}
        };
    }

    @org.junit.Test
    @Parameters(method = "parametersForTestOffice")
    public void testOffice(String localtion,boolean isOffline){
        //Given
        Event event = Event.builder()
                .location(localtion)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isEqualTo(isOffline);


    }

}