package com.evo.identity.domain;

import com.evo.configuration.AuditableDomain;
import com.evo.identity.application.enums.EProfileStatus;
import com.evo.identity.application.enums.EResponseStatus;
import com.evo.identity.application.enums.EVerify;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.util.EvoIdUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail extends AuditableDomain {
    private UUID id;
    private UUID userId;
    private UUID avatarId;
    private String bio;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private Date dateOfBirth;
    private String addressLine;
    private String province;
    private String country;
    private String emailVerified;
    private String email;
    private String linkVerify;
    private Date expireAt;
    private Long isVerified;
    private Date verifiedAt;

    public UserDetail(UserDetailCmd cmd) {
        this.id = EvoIdUtils.nextId();
        this.userId = cmd.getUserId();
        this.email = cmd.getEmail();
        this.bio = cmd.getBio();
        this.firstName = cmd.getFirstName();
        this.lastName = cmd.getLastName();
        this.gender = cmd.getGender();
        this.phoneNumber = cmd.getPhoneNumber();
        this.dateOfBirth = cmd.getDateOfBirth();
        this.addressLine = cmd.getAddressLine();
        this.province = cmd.getProvince();
        this.country = cmd.getCountry();
        this.isVerified = EVerify.UNVERIFIED.value;
    }

    public void update(UserDetailCmd cmd) {
        this.bio = cmd.getBio();
        this.firstName = cmd.getFirstName();
        this.lastName = cmd.getLastName();
        this.gender = cmd.getGender();
        this.phoneNumber = cmd.getPhoneNumber();
        this.dateOfBirth = cmd.getDateOfBirth();
        this.addressLine = cmd.getAddressLine();
        this.province = cmd.getProvince();
        this.country = cmd.getCountry();
        this.emailVerified = cmd.getEmailVerified();
        this.email = cmd.getEmail();
        this.isVerified = cmd.getIsVerified();
        this.linkVerify = cmd.getLinkVerify();
        this.expireAt = cmd.getExpireAt();
    }

    public void verify() {
        this.emailVerified = this.email;
        this.email = null;
        this.linkVerify = null;
        this.expireAt = null;
    }
}
