package id.ac.ui.cs.advprog.perbaikiinaja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    
    private Item item;
    
    @BeforeEach
    void setUp() {
        item = new Item();
    }
    
    @Test
    void testSetAndGetName() {
        String name = "Headphone";
        item.setName(name);
        assertEquals(name, item.getName());
    }
    
    @Test
    void testSetAndGetCondition() {
        String condition = "Good";
        item.setCondition(condition);
        assertEquals(condition, item.getCondition());
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
