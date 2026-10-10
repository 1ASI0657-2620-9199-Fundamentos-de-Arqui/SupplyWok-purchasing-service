Feature: Purchase Order Visibility and Tenant Isolation
  As a system stakeholder
  I want purchase orders to be accessible only by authorized restaurants and suppliers
  So that tenant confidentiality and data isolation are strictly maintained

  Scenario: Supplier only sees purchase orders addressed to it
    Given a purchase order "PO-VIS-01" for supplier 10 and restaurant 101
    And a purchase order "PO-VIS-02" for supplier 20 and restaurant 101
    When supplier 10 queries all its purchase orders
    Then the query results contain only purchase order "PO-VIS-01"
    And the query results do not contain purchase order "PO-VIS-02"

  Scenario: Restaurant only sees its own purchase orders and cannot see another restaurant's orders
    Given a purchase order "PO-VIS-10" for restaurant 101 and supplier 10
    And a purchase order "PO-VIS-20" for restaurant 202 and supplier 10
    When restaurant 101 queries all its purchase orders
    Then the query results contain only purchase order "PO-VIS-10"
    And the query results do not contain purchase order "PO-VIS-20"

  Scenario: A restaurant cannot access another restaurant's purchase order by ID
    Given a purchase order "PO-ISO-01" with id 500 belonging to restaurant 101
    When restaurant 202 queries the purchase order with id 500
    Then no purchase order is found
