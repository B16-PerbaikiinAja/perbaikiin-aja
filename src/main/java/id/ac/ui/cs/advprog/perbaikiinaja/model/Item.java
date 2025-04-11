package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.util.UUID;

/**
 * Represents an item to be repaired.
 */
public class Item {
    private UUID id;
    private String name;
    private String condition;
    private String issueDescription;

    public Item() {
        this.id = UUID.randomUUID();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
