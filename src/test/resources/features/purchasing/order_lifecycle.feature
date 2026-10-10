Feature: Purchase Order Lifecycle and Modifications
  As a restaurant manager or supplier
  I want to manage purchase order details and status transitions according to business rules
  So that order integrity is preserved throughout fulfillment

  Scenario: Successfully modify a purchase order while it is in Pending status
    Given an existing purchase order with code "PO-MOD-01" in "Pending" status for restaurant 101 and supplier 10
    When the restaurant modifies the purchase order details with updated restaurant name "Dragon Wok Central"
    Then the purchase order is updated successfully with restaurant name "Dragon Wok Central"

  Scenario: Reject modifying a purchase order that is not in Pending status
    Given an existing purchase order with code "PO-MOD-02" in "InTransit" status for restaurant 101 and supplier 10
    When the restaurant attempts to modify the purchase order details
    Then the modification is rejected with an error "Only pending purchase orders can be modified."

  Scenario: Transition purchase order status through valid fulfillment stages
    Given an existing purchase order with code "PO-TRANS-01" in "Pending" status
    When the status is updated to "Confirmed"
    Then the purchase order status is updated to "Confirmed"
    When the status is updated to "InTransit"
    Then the purchase order status is updated to "InTransit"
    When the status is updated to "Delivered"
    Then the purchase order status is updated to "Delivered"

  Scenario: Reject invalid status transitions
    Given an existing purchase order with code "PO-TRANS-02" in "Pending" status
    When an invalid status transition to "Delivered" is attempted
    Then the status update is rejected with an invalid transition error
