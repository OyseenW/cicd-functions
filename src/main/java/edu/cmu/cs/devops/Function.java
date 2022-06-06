package edu.cmu.cs.devops;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.apache.commons.codec.binary.Base64;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            org.apache.commons.codec.binary.Base64 base64 = new Base64();
            byte[] decodedContextBytes = base64.decodeBase64(name);
            String decodedContext = new String(decodedContextBytes);
            // System.out.println(decodedContext);
            String[] words = decodedContext.split(" ");
            for (String w: words) {
                // System.out.print(w + " ");
            }
            // System.out.println(" ");
            LinkedList<String> sortedWords = new LinkedList<>();
            sortedWords.add(words[0]);
            for (int j = 1; j < words.length; j++) {
                boolean flag = false;
                for (int i = 0; i < sortedWords.size(); i++) {
                    if (words[j].compareToIgnoreCase(sortedWords.get(i)) < 0) {
                        sortedWords.add(i, words[j]);
                        break;
                    }
                    if (i == sortedWords.size() - 1) {
                        flag = true;
                    }
                }
                if (flag) {
                    sortedWords.addLast(words[j]);
                }
            }
            String result = "";
            for (String w: sortedWords) {
                result += w;
                result += " ";
            }
            result = result.trim();
            // System.out.println(result);

            String encodeResult = base64.encodeBase64String(result.getBytes());

             System.out.println(encodeResult);


            return request.createResponseBuilder(HttpStatus.OK).body(encodeResult).build();
        }
    }
}
