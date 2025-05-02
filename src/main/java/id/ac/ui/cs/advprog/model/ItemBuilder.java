package id.ac.ui.cs.advprog.model;

public class ItemBuilder {
    private String itemName;
    private String itemCondition;
    private String issueDescription;
    
    public ItemBuilder itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }
    
    public ItemBuilder itemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
        return this;
    }
    
    public ItemBuilder issueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
        return this;
    }
    
    public Item build() {
        Item item = new Item();
        item.setItemName(this.itemName);
        item.setItemCondition(this.itemCondition);
        item.setIssueDescription(this.issueDescription);
        return item;
    }
}
