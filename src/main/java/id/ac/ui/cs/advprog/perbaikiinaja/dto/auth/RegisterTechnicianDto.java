package id.ac.ui.cs.advprog.perbaikiinaja.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterTechnicianDto extends RegisterUserDto {
    private String address;
    private int completedJobsCount;
    private double totalEarnings;
}
