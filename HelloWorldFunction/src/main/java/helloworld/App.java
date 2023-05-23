package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.groupdocs.metadata.Metadata;
import com.groupdocs.metadata.core.FileFormat;
import com.groupdocs.metadata.core.IReadOnlyList;
import com.groupdocs.metadata.core.MetadataProperty;
import com.groupdocs.metadata.search.FallsIntoCategorySpecification;
import com.groupdocs.metadata.tagging.Tags;

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
            this.getMetadata();
            // String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);

            // return response
            //         .withStatusCode(200)
            //         .withBody(output);
            return response
                    .withBody("{}")
                    .withStatusCode(200);
        } catch (IOException e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private void getMetadata() throws IOException{
        System.out.println("CWD=" + new File(".").getAbsolutePath());
        try (Metadata metadata = new Metadata("/var/task/input.mkv")) {
            if (metadata.getFileFormat() != FileFormat.Unknown && !metadata.getDocumentInfo().isEncrypted()) {
                System.out.println("The metadata properties describing some characteristics of the file content: title, keywords, language, etc.");
                Object properties = metadata.findProperties(new FallsIntoCategorySpecification(Tags.getContent()));
                System.out.println("The metadata properties describing some characteristics of the file content: title, keywords, language, etc.");
                // for (MetadataProperty property : properties) {
                //     System.out.println(String.format("Property name: %s, Property value: %s", property.getName(), property.getValue()));
                // }
            }
        }
    }
}
