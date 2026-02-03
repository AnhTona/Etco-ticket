package com.esco.etco.entity.response;

import com.esco.etco.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private int age;
    private String address;
    private Instant createdAt;
}
