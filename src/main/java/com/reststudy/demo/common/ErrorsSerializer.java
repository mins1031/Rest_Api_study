package com.reststudy.demo.common;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;


import java.io.IOException;

/**
 * JsonSerializer<T> 를 상속받는건 기본적으로 직렬화(객 -> json)를 해주는경우 사용되는데 기본 스프링에서 제공되는
 * JsonSerializer(jackson)는 자바빈의 규약을 따르는 객체만 직렬화가 가능하고 다른 객체는 불가했음
 * 지금의 경우인 Errors는 자바빈 규약을 따르지 않기에 직렬화 불가해 직접 Errors용 JsonSerializer를 구현하신듯
 * 구글링에서 본 다른 예시로는 LocalDateTime을 변환시 <T-> LocalDateTime>으로 설정후 LocalDateTime내용을 string으로
 * 파싱하는 작업을 보았음.
 * 이제 만든 ErrorsSerializer를 objectMapper에 등록을 해줘야하는데 @JsonComponent 어노테이션으로 쉽게 가능하다고 함
 * 그래서 이제 Errors를 직렬화할때 해당 클래스를 사용해줌.
 * 지역에러와 글로벌에러는 솔직히 당장은 뭔지 감만 조금 잡힘. 지역에러는 단순히 값에서 나는에러들 널포인터라던지,인덱스 범위 넘는거라던지
 * 전역에러는...몰러 그런가보다 함일단.
 * */
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    /**
     * Errors에는 배열이 여러개 있기 때문에 StartArray,EndArray 로 구분
     * */
    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        errors.getFieldErrors().stream().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("field",e.getField());
                gen.writeStringField("objectName",e.getObjectName());
                gen.writeStringField("code",e.getCode());
                gen.writeStringField("defaultMessage",e.getDefaultMessage());
                Object rejectValue = e.getRejectedValue();
                if (rejectValue != null){
                    gen.writeStringField("rejectedValue", rejectValue.toString());
                }

                gen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(e ->{
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName",e.getObjectName());
                gen.writeStringField("code",e.getCode());
                gen.writeStringField("defaultMessage",e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}
