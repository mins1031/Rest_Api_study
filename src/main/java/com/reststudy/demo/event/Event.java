package com.reststudy.demo.event;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional)이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        //UPdate free
        if (this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        } else
            this.free = false;

        if (this.location == null || this.location.isBlank()){
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
    /**
     * 이거 꿀팁 if (this.location.isBlank()){ 이 if 조건문의 경우 java 11 이전엔 trim으로 공백을 모두 지우고 .isEmpty를
     * 확인했다면 java11부터는 isBlank를 통해 문자열을 공백을 제외하고 검사해줌.
     */
}
