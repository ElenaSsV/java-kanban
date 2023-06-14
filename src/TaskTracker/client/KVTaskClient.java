package TaskTracker.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private String url;
    private HttpRequest request;
    private  final String apiToken;

    public KVTaskClient(String url) throws IOException, InterruptedException{
        this.url = url;
        String path = url + "/register";

        URI uriToRegister = URI.create(path);
        request = HttpRequest.newBuilder().GET().uri(uriToRegister).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        apiToken = response.body();
    }
    public void put(String key, String json) throws IOException, InterruptedException {
        String path = url + "/save" + "/" + key + "?API_TOKEN=" + apiToken;
        URI uriToPut = URI.create(path);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(uriToPut).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Код ответа: " + response.statusCode());
    }
    public String load(String key) throws IOException, InterruptedException {
        URI uriToLoad = URI.create(url + "/load" + "/" + key + "?" +"API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder().GET().uri(uriToLoad).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        return response.body();
    }

}
