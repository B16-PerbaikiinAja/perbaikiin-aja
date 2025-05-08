package id.ac.ui.cs.advprog.perbaikiinaja.model;

public class ItemBuilder {
    private String name;
    private String condition;
    private String issueDescription;
    
    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public ItemBuilder condition(String condition) {
        this.condition = condition;
        return this;
    }
    
    public ItemBuilder issueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
        return this;
    }
    
    public Item build() {
        Item item = new Item();
        item.setName(this.name);
        item.setCondition(this.condition);
        item.setIssueDescription(this.issueDescription);
        return item;
    }
}
