package id.ac.ui.cs.advprog.perbaikiinaja.model;

import id.ac.ui.cs.advprog.model.Item;
import id.ac.ui.cs.advprog.model.ItemBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemBuilderTest {
    
    private ItemBuilder builder;
    
    @BeforeEach
    void setUp() {
        builder = new ItemBuilder();
    }
    
    @Test
    void testBuildItemWithChainedCalls() {
        Item item = builder
                .itemName("Phone")
                .itemCondition("Poor")
                .issueDescription("Cracked screen")
                .build();
        
        assertEquals("Phone", item.getItemName());
        assertEquals("Poor", item.getItemCondition());
        assertEquals("Cracked screen", item.getIssueDescription());
    }
}
