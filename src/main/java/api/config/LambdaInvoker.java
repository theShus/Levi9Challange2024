package api.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.*;
import java.nio.charset.StandardCharsets;

public class LambdaInvoker {

    private static final String FUNCTION_ARN = "arn:aws:lambda:eu-central-1:145023105668:function:SendEmailFunction";

    public String invokeSendEmailFunction(String jsonString) {
        // Create AWS Lambda client
        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withRegion("eu-central-1")
                .build();

        // Prepare the invocation request with the JSON string as payload
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(FUNCTION_ARN)
                .withInvocationType(InvocationType.RequestResponse)
                .withPayload(jsonString);

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