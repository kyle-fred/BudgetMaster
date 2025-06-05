package com.budgetmaster.testsupport.assertions.controller.error;

import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.testsupport.constants.PathConstants;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ErrorControllerAssertions {

    private final ResultActions resultActions;

    private ErrorControllerAssertions(ResultActions resultActions) {
        this.resultActions = resultActions;
    }

    public static ErrorControllerAssertions assertThat(ResultActions resultActions) {
        return new ErrorControllerAssertions(resultActions);
    }

    public ErrorControllerAssertions isNotFound() throws Exception {
        resultActions.andExpect(status().isNotFound());
        return this;
    }

    public ErrorControllerAssertions isInternalServerError() throws Exception {
        resultActions.andExpect(status().isInternalServerError());
        return this;
    }

    public ErrorControllerAssertions hasTimestamp() throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.TIMESTAMP).exists());
        return this;
    }

    public ErrorControllerAssertions hasStatus(int expectedStatus) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.STATUS).value(expectedStatus));
        return this;
    }

    public ErrorControllerAssertions hasErrorCode(String expectedErrorCode) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERROR_CODE).value(expectedErrorCode));
        return this;
    }

    public ErrorControllerAssertions hasMessage(String expectedMessage) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.MESSAGE).value(expectedMessage));
        return this;
    }

    public ErrorControllerAssertions hasPath(String expectedPath) throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.PATH).value(expectedPath));
        return this;
    }

    public ErrorControllerAssertions hasNoValidationErrors() throws Exception {
        resultActions.andExpect(jsonPath(PathConstants.JsonProperties.Error.ERRORS).isEmpty());
        return this;
    }

    public ErrorControllerAssertions isNotFoundResponse(String message, String path) throws Exception {
        return isNotFound()
            .hasTimestamp()
            .hasStatus(HttpStatus.NOT_FOUND.value())
            .hasErrorCode(ErrorCode.RESOURCE_NOT_FOUND.name())
            .hasMessage(message)
            .hasPath(path)
            .hasNoValidationErrors();
    }

    public ErrorControllerAssertions isInternalServerErrorResponse(String path) throws Exception {
        return isInternalServerError()
            .hasTimestamp()
            .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .hasErrorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
            .hasMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
            .hasPath(path)
            .hasNoValidationErrors();
    }
}
