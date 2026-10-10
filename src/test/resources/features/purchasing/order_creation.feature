Feature: Purchase Order Creation
  As a restaurant manager
  I want to create purchase orders for registered suppliers
  So that I can restock supplies needed for restaurant operations

  Scenario: Successfully create a pending purchase order for an active supplier
    Given an active supplier with id 10 and name "Asian Food Distribution"
    And a restaurant with id 101 and name "Dragon Wok"
    When the restaurant submits a purchase order with code "PO-2026-001" for supplier 10 with items:
      | productName | quantity | unitPrice | unitType |
      | Soy Sauce   | 20.0     | 5.50      | L        |
      | Jasmine Rice| 50.0     | 2.20      | KG       |
    Then the purchase order is created successfully with status "Pending"
    And the purchase order belongs to restaurant 101 and supplier 10

  Scenario: Fail to create a purchase order when the supplier does not exist
    Given no supplier exists with id 999
    And a restaurant with id 101 and name "Dragon Wok"
    When the restaurant submits a purchase order with code "PO-2026-002" for supplier 999 with items:
      | productName | quantity | unitPrice | unitType |
      | Sesame Oil  | 5.0      | 8.00      | L        |
    Then the purchase order creation is rejected because supplier is not found

  Scenario: Reject creating a purchase order with an initial status other than Pending
    Given an active supplier with id 10 and name "Asian Food Distribution"
    And a restaurant with id 101 and name "Dragon Wok"
    When the restaurant attempts to create a purchase order with initial status "Confirmed"
    Then the purchase order creation is rejected with an error "New purchase orders must start as Pending."
