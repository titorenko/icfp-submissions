package frontend;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import frontend.exceptions.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Frontend {

    private static final Logger LOG = LoggerFactory.getLogger(Frontend.class);

    private ObjectMapper mapper;
    private Client client;


    public Frontend() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        client = Client.create();
    }

    // get all our problems
    public Problem[] getProblems() {
        return doRequest(Commands.myproblems, Problem[].class, null);
    }

    // this thing starts problem evaluation timer (!!!!!) if you use id from problem list
    public EvalResponse evaluate(EvalRequest request) {
        return doRequest(Commands.eval, EvalResponse.class, request);
    }

    // this thing starts problem evaluation timer (!!!!!)
    public GuessResponse guess(Guess guess) {
        return doRequest(Commands.guess, GuessResponse.class, guess);
    }

    public TrainingProblem train(TrainRequest request) {
        return doRequest(Commands.train, TrainingProblem.class, request);
    }

    public Status status() {
        return doRequest(Commands.status, Status.class, null);
    }

    public <T> T doRequest(Commands command, Class<T> responseType, Object body) {

        LOG.debug("Running " + command + " for " + body);

        WebResource webResource = client
                .resource("http://icfpc2013.cloudapp.net/"+command)
                .queryParam("auth", "02610OhkKvBo2Ns5MAAzC8KEujp5Z2UxYEyfd9cNvpsH1H");

        ClientResponse response;
        if (body==null) {
            response = webResource
                    .accept("application/json")
                    .get(ClientResponse.class);
        } else {
            String json = putToJson(body);
            LOG.debug("Sending json: " + json);
            response = webResource
                    .type("application/json")
                    .accept("application/json")
                    .post(ClientResponse.class, json);
        }

        switch(response.getStatus()) {
            case 200:
                break;
            case 400:
                throw new MalformedRequestException("400: Request body is invalid. Technical failure");
            case 404:
                throw new NoSuchProblemException("404: Problem with such id doesn't exist ", body);
            case 410:
                throw new ProblemTimedOutException("410: Problem timed out (5 min passed) ", body);
            case 412:
                throw new ProblemAlreadySolvedException("412: Problem already solved ", body);
            case 413:
                throw new RequestToBigException("413: Request is too big");
            case 429:
                throw new RetryLaterException("429: Retry later response");
            default:
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        return getFromJson(responseType, response);
    }

    private String putToJson(Object body) {
        try {
            return mapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new FailedToCreateRequestException("Failed to serialize to json from " + body, e);
        }
    }

    private <T> T getFromJson(Class<T> responseType, ClientResponse response) {
        try {
            return mapper.readValue(response.getEntityInputStream(), responseType);
        } catch (IOException e) {
            throw new UnpaseableResponseException("Failed to deserialize json to " + responseType.getName(), e);
        }
    }

    public <T> T retryTask(FrontendTask<T> task) {
        for (; ; ) {
            try {
                return task.doTask(this);
            } catch (RetryLaterException e) {
                try {
                    System.out.println("Throttling... (1s)");
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    //
                }
            }
        }
    }

    public interface FrontendTask<T> {
        T doTask(Frontend frontend);
    }
}
