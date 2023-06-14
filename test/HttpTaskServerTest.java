import TaskTracker.model.Epic;
import TaskTracker.model.Status;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.server.HttpTaskServer;
import TaskTracker.service.Managers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HttpTaskServerTest {
    private static HttpTaskServer server;
    private HttpClient client;

    private URI url;

    private final Gson gson = Managers.getGson();

    @BeforeEach
    public void beforeEach() throws IOException {
        client = HttpClient.newHttpClient();
        server = new HttpTaskServer();
        server.start();

    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void postNewTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0),90);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task actualTask = gson.fromJson(response.body(), Task.class);
        newTask.setId(1);
        assertEquals(newTask, actualTask);
    }

    @Test
    public void postNewEpic() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);
        newEpic.setId(1);
        assertEquals(newEpic, actualEpic);
    }

    @Test
    public void postNewSubtask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");

        Subtask newSubtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,15, 10,0),
                90);

        String subtaskJson = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(url2).header("Accept", "application/json")
                .POST(subtaskBody).build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask actualSubtask = gson.fromJson(subtaskResponse.body(), Subtask.class);
        newSubtask.setId(2);
        assertEquals(newSubtask, actualSubtask);
    }

    @Test
    public void postTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0),90);

        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task updatedTask = new Task("Test createTask2", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 7,30, 15,0),90);
        updatedTask.setId(1);
        String updatedJson = gson.toJson(updatedTask);
        final HttpRequest.BodyPublisher updatedTaskBody = HttpRequest.BodyPublishers.ofString(updatedJson);
        HttpRequest updatedTaskRequest = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(updatedTaskBody).build();
        HttpResponse<String> updatedTaskResponse = client.send(updatedTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updatedTaskResponse.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest actualTaskRequest = HttpRequest.newBuilder().uri(urlToGetById).header("Accept", "application/json")
                .GET().build();
        HttpResponse<String> actualTaskResponse = client.send(actualTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, actualTaskResponse.statusCode());

        assertEquals(updatedTask, gson.fromJson(actualTaskResponse.body(), Task.class));
    }

    @Test
    public void handleGetTasks() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0),90);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> taskResponse = client.send(tasksRequest, HttpResponse.BodyHandlers.ofString());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasksFromJson = gson.fromJson(taskResponse.body(), taskType);
        assertFalse(tasksFromJson.isEmpty());
        newTask.setId(1);
        assertEquals(newTask, tasksFromJson.get(0));
    }

    @Test
    public void handleGetEpics() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());

        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> epicFromJson = gson.fromJson(epicResponse.body(), epicType);
        assertFalse(epicFromJson.isEmpty());
        newEpic.setId(1);
        assertEquals(newEpic, epicFromJson.get(0));
    }

    @Test
    public void handleGetTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6, 30, 10, 0), 90);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(urlToGetById).header("Accept", "application/json").GET().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.body(), taskResponse.body());
        newTask.setId(1);
        Task receivedTask = gson.fromJson(taskResponse.body(),Task.class);
        assertEquals(newTask, receivedTask);
    }
    @Test
    public void handleGetEpic() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(urlToGetById).header("Accept", "application/json").GET().build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.body(), epicResponse.body());
        newEpic.setId(1);
        Epic receivedEpic = gson.fromJson(epicResponse.body(),Epic.class);
        assertEquals(newEpic, receivedEpic);


    }

    @Test
    public void handleGetSubtask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");

        Subtask newSubtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,15, 10,0),
                90);

        String subtaskJson = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(url2).header("Accept", "application/json")
                .POST(subtaskBody).build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        URI urlToGetById = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest getSubtaskRequest = HttpRequest.newBuilder().uri(urlToGetById).header("Accept", "application/json").GET().build();
        HttpResponse<String> getSubtaskResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(subtaskResponse.body(), getSubtaskResponse.body());
        newSubtask.setId(2);
        Subtask receivedSubtask = gson.fromJson(getSubtaskResponse.body(), Subtask.class);
        assertEquals(newSubtask, receivedSubtask);
    }

    @Test
    public void handleDeleteTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6, 30, 10, 0), 90);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(urlToGetById).DELETE().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleDeleteTasks() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6, 30, 10, 0), 90);
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(urlToGetById).DELETE().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleDeleteEpic() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToDelById = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(urlToDelById).DELETE().build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleDeleteEpics() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToDelById = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(urlToDelById).DELETE().build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleDeleteSubtask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic ("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");

        Subtask newSubtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6,15, 10,0),
                90);

        String subtaskJson = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(url2).header("Accept", "application/json")
                .POST(subtaskBody).build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        URI urlToDelById = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest getSubtaskRequest = HttpRequest.newBuilder().uri(urlToDelById).DELETE().build();
        HttpResponse<String> getSubtaskResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleDeleteSubtasks() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic("Test createEpic", "Test createEpic description");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");

        Subtask newSubtask = new Subtask("Test Subtask", "Test description",
                Status.NEW, 1, LocalDateTime.of(2023, 6, 15, 10, 0),
                90);

        String subtaskJson = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtaskJson);
        HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(url2).header("Accept", "application/json")
                .POST(subtaskBody).build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        URI urlToDelById = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest getSubtaskRequest = HttpRequest.newBuilder().uri(urlToDelById).DELETE().build();
        HttpResponse<String> getSubtaskResponse = client.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void handleGetHistory() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0),90);

        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI urlToGetById = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(urlToGetById).header("Accept",
                "application/json").GET().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(response.body(), taskResponse.body());
        newTask.setId(1);
        Task receivedTask = gson.fromJson(taskResponse.body(),Task.class);
        assertEquals(newTask, receivedTask);


        URI urlToGetHistory = URI.create("http://localhost:8080/tasks/history");
        HttpRequest historyRequest = HttpRequest.newBuilder().uri(urlToGetHistory).header("Accept",
                "application/json").GET().build();
        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasksFromJson = gson.fromJson(historyResponse.body(), taskType);

        assertEquals(newTask, tasksFromJson.get(0));
    }

    @Test
    public void handleGetPriority() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task/");
        Task taskToBeSecond = new Task("Test createTask", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,30, 10,0),90);

        String json = gson.toJson(taskToBeSecond);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskToBeFirst = new Task("Test createTask2", "Test createTask description", Status.NEW,
                LocalDateTime.of(2023, 6,29, 15,0),90);

        String taskToBeFirstJson = gson.toJson(taskToBeFirst);
        final HttpRequest.BodyPublisher taskToBeFirstBody = HttpRequest.BodyPublishers.ofString(taskToBeFirstJson);
        HttpRequest taskToBeFirstRequest = HttpRequest.newBuilder().uri(url).header("Accept", "application/json")
                .POST(taskToBeFirstBody).build();
        HttpResponse<String> taskToBeFirstResponse = client.send(taskToBeFirstRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, taskToBeFirstResponse.statusCode());

        URI urlToGetPriority = URI.create("http://localhost:8080/tasks/");
        HttpRequest priorityRequest = HttpRequest.newBuilder().uri(urlToGetPriority).header("Accept",
                "application/json").GET().build();
        HttpResponse<String> priorityResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        taskToBeFirst.setId(2);
        taskToBeSecond.setId(1);

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasksFromJson = gson.fromJson(priorityResponse.body(), taskType);

        assertEquals(taskToBeFirst, tasksFromJson.get(0));
        assertEquals(taskToBeSecond, tasksFromJson.get(1));
    }
}
