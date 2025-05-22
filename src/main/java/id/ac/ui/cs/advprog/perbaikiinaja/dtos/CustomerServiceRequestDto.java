package id.ac.ui.cs.advprog.perbaikiinaja.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CustomerServiceRequestDto {
    private String name;
    private String condition;
    private String issueDescription;
    private LocalDate serviceDate;
    private String couponCode;
    private UUID paymentMethodId;
}
