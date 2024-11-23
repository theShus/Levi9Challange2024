package api.config;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import api.models.Match;
import java.nio.charset.StandardCharsets;

public class BucketLambdaInvoker {

    private static final String FUNCTION_ARN = "arn:aws:lambda:eu-central-1:145023105668:function:FunctionForEmailSending"; // Update with your Lambda function ARN


    public String invokeStoreStringDataFunction(String content) {
        // Create AWS Lambda client
        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withRegion("eu-central-1")
                .build();

        // Prepare the payload by encoding the string as a JSON string
        String payload = "\"" + content.replace("\"", "\\\"") + "\""; // Escape any double quotes in the content

        // Prepare the invocation request
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(FUNCTION_ARN)
                .withInvocationType(InvocationType.RequestResponse)
                .withPayload(payload);

        try {
            // Invoke the Lambda function
            InvokeResult invokeResult = awsLambda.invoke(invokeRequest);

            // Process the response
            String responseJson = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            System.out.println("Lambda function invoked successfully. Response: " + responseJson);

            return responseJson;
        } catch (ServiceException e) {
            System.err.println("ServiceException: " + e.getErrorMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return null;
        }
    }
}
