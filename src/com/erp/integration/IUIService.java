package com.erp.integration;

import com.erp.exception.IntegrationException;

import java.util.Map;

/**
 * Boundary interface between the UI and backend subsystems.
 * All deep-integration calls (Orders, HR, auth) flow through this single contract.
 *
 * Implementations may be mocks (in-memory), HTTP clients, or any other transport.
 */
public interface IUIService {

    // ========= Endpoints =========
    String AUTH_LOGIN = "auth/login";

    String ORDERS_LIST = "orders/list";
    String ORDERS_CREATE = "orders/create";
    String ORDERS_APPROVE = "orders/approve";
    String ORDERS_REJECT = "orders/reject";
    String ORDERS_REVISION = "orders/revision";
    String ORDERS_SHIP = "orders/ship";
    String ORDERS_PAY = "orders/pay";
    String ORDERS_CANCEL = "orders/cancel";
    String ORDERS_STATS = "orders/stats";

    String HR_EMPLOYEES = "hr/employees";
    String HR_EMPLOYEE_UPDATE = "hr/employee/update";
    String HR_RECRUITMENT = "hr/recruitment";
    String HR_RECRUITMENT_STAGE = "hr/recruitment/stage";
    String HR_ONBOARDING = "hr/onboarding";
    String HR_ONBOARDING_UPDATE = "hr/onboarding/update";
    String HR_PAYROLL = "hr/payroll";
    String HR_PAYROLL_TRANSFER = "hr/payroll/transfer";
    String HR_ATTENDANCE = "hr/attendance";
    String HR_ATTENDANCE_LOG = "hr/attendance/log";
    String HR_LEAVE = "hr/leave";
    String HR_LEAVE_ACTION = "hr/leave/action";
    String HR_PERFORMANCE = "hr/performance";
    String HR_STATS = "hr/stats";

    /**
     * Read data from the backend.
     * @throws IntegrationException (FETCH_DATA_FAILED) on error
     */
    <T> T fetchData(String endpoint, Map<String, Object> params, Class<T> resultType)
            throws IntegrationException;

    /**
     * Write data to the backend.
     * @throws IntegrationException (SEND_DATA_FAILED) on error
     */
    <R> R sendData(String endpoint, Object payload, Class<R> resultType)
            throws IntegrationException;
}
