package id.ac.ui.cs.advprog.perbaikiinaja.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import jakarta.persistence.*;

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String itemName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String itemCondition;

    @Getter
    @Setter
    @Column(nullable = false)
    private String issueDescription;

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }
}
