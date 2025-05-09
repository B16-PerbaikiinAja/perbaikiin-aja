package id.ac.ui.cs.advprog.perbaikiinaja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemBuilderTest {

    ItemBuilder builder;

    @BeforeEach
    void setUp() {
        builder = Item.builder();
    }

    @Test
    void testBuildItemWithChainedCalls() {
        Item item = builder
                .name("Phone")
                .condition("Poor")
                .issueDescription("Cracked screen")
                .build();

        assertEquals("Phone", item.getName());
        assertEquals("Poor", item.getCondition());
        assertEquals("Cracked screen", item.getIssueDescription());
    }
}
