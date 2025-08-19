package com.evo.identity.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileReqModel {
    private String bio;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private Date dateOfBirth;
    private String addressLine;
    private String province;
    private String country;
}
