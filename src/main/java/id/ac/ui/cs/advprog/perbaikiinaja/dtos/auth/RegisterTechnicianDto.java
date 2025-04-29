package id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterTechnicianDto extends RegisterUserDto {
    private String address;
}