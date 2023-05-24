package helloworld;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import com.groupdocs.metadata.Metadata;
import com.groupdocs.metadata.core.FileFormat;
import com.groupdocs.metadata.core.IReadOnlyList;
import com.groupdocs.metadata.core.MetadataProperty;
import com.groupdocs.metadata.search.FallsIntoCategorySpecification;
import com.groupdocs.metadata.tagging.Tags;
import com.tests.*;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
            S3Object o = s3.getObject("shell-test-lambda", "input.mkv");
            S3ObjectInputStream s3is = o.getObjectContent();
            System.out.println(input.getBody() + "body");
            IReadOnlyList<MetadataProperty> properties = this.getMetadata(s3is);
            String output = String.format("{ \"message\": \"Metadata properties\":  }", properties);
            
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (IOException e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private IReadOnlyList<MetadataProperty> getMetadata(S3ObjectInputStream o) throws IOException{
        try (Metadata metadata = new Metadata(o)) {
            
            if (metadata.getFileFormat() != FileFormat.Unknown && !metadata.getDocumentInfo().isEncrypted()) {
                System.out.println("The metadata properties describing some characteristics of the file content: title, keywords, language, etc.");
                IReadOnlyList<MetadataProperty> properties = metadata.findProperties(new FallsIntoCategorySpecification(Tags.getContent()));
                System.out.println("Metadata properties: " + properties);
                return properties;
                // for (MetadataProperty property : properties) {
                //     System.out.println(String.format("Property name: %s, Property value: %s", property.getName(), property.getValue()));
                // }
            }
        }
        return null;
    }
}
