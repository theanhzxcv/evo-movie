package com.evo.identity.infrastructure.persistence.entities;

import com.evo.configuration.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CollectionId;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_DETAIL")
public class UserDetailEntity extends AuditableEntity {
    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "AVATAR_ID")
    private UUID avatarId;

    @Column(name = "BIO")
    private String bio;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL_VERIFIED")
    private String emailVerified;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @Column(name = "ADDRESS_LINE")
    private String addressLine;

    @Column(name = "PROVINCE")
    private String province;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "LINK_VERIFY")
    private String linkVerify;

    @Column(name = "EXPIRE_AT")
    private Date expireAt;

    @Column(name = "IS_VERIFIED")
    private Long isVerified;

    @Column(name = "VERIFY_AT")
    private Date verifyAt;
}
