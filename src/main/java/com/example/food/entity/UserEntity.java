package com.example.food.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String role;
    private String imageUrl;
    private String customerId;
}