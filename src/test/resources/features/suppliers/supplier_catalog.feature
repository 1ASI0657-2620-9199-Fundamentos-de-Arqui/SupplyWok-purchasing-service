Feature: Supplier Catalog Management
  As a registered supplier
  I want to manage my product catalog items with strict ownership checks
  So that only valid suppliers can publish and update their offerings

  Scenario: Add a catalog item for a registered supplier
    Given a registered supplier with id 10 in purchasing
    When supplier 10 adds catalog item "Hoisin Sauce" priced at 18.50 per "LTR"
    Then the catalog item is created with an assigned id

  Scenario: Reject adding a catalog item when supplier does not exist
    Given no registered supplier exists with id 999 in purchasing
    When supplier 999 attempts to add catalog item "Sesame Oil" priced at 22.00 per "LTR"
    Then the catalog item operation returns an empty result

  Scenario: Update a catalog item belonging to the supplier
    Given a registered supplier with id 10 in purchasing
    And supplier 10 owns catalog item 42 named "Hoisin Sauce" priced at 18.50
    When supplier 10 updates catalog item 42 to name "Hoisin Premium" and price 25.00
    Then catalog item 42 is updated with name "Hoisin Premium" and price 25.00

  Scenario: Reject updating a catalog item that does not belong to the supplier
    Given a registered supplier with id 20 in purchasing
    And catalog item 42 does not belong to supplier 20
    When supplier 20 attempts to update catalog item 42 to name "Unauthorized" and price 10.00
    Then the catalog item operation returns an empty result
