package id.ac.ui.cs.advprog.perbaikiinaja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {
    
    private Item item;
    
    @BeforeEach
    void setUp() {
        item = new Item();
    }
    
    @Test
    void testSetAndGetItemName() {
        String itemName = "Headphone";
        item.setItemName(itemName);
        assertEquals(itemName, item.getItemName());
    }
    
    @Test
    void testSetAndGetItemCondition() {
        String condition = "Good";
        item.setItemCondition(condition);
        assertEquals(condition, item.getItemCondition());
    }
    
    @Test
    void testSetAndGetIssueDescription() {
        String issue = "Right ear doesn't work";
        item.setIssueDescription(issue);
        assertEquals(issue, item.getIssueDescription());
    }
    
    @Test
    void testIdIsGenerated() {
        assertNull(item.getId());
    }
}
