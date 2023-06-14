package TaskTracker.server;

import TaskTracker.exception.TaskValidationException;
import TaskTracker.model.Epic;
import TaskTracker.model.Subtask;
import TaskTracker.model.Task;
import TaskTracker.service.Managers;
import TaskTracker.service.TaskManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private HttpServer httpServer;
    private Gson gson;

    private TaskManager manager;


    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        gson = Managers.getGson();
        manager = Managers.getDefaultFileBackedTaskManager();
        httpServer.createContext("/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestedMethod = exchange.getRequestMethod();
            String rawQuery = exchange.getRequestURI().getRawQuery();

            switch (requestedMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task/", path) && rawQuery != null) {
                        String taskId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(taskId);
                        handleGetTask(exchange, id);
                        break;
                    } else if (Pattern.matches("^/tasks/epic/", path) && rawQuery != null) {
                        String epicId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(epicId);
                        handleGetEpic(exchange, id);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/", path) && rawQuery != null) {
                        String subtaskId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(subtaskId);
                        handleGetSubtask(exchange, id);
                        break;

                    } else if (Pattern.matches("^/tasks/task/$", path)) {
                        handleGetTasks(exchange);
                        break;
                    }
                    else if (Pattern.matches("^/tasks/epic/$", path)) {
                        handleGetEpics(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        handleGetSubtasks(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/epic/", path) && rawQuery != null) {
                        String epicId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(epicId);
                        handleGetSubtasksToEpic(exchange, id);
                        break;
                    } else if (Pattern.matches("^/tasks/history$", path)) {
                        handleGetHistory(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/$", path)) {
                        handleGetPriority(exchange);
                        break;
                    } else {
                        System.out.println("Такого эндпойнта с методом GET не существует");
                        exchange.sendResponseHeaders(400, 0);
                        break;
                    }
                case "POST":
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        handlePostTask(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/epic/$", path)) {
                        handlePostEpic(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        handlePostSubtask(exchange);
                        break;
                    } else {
                        System.out.println("Такого эндпойнта с методом POST не существует");
                        exchange.sendResponseHeaders(400, 0);
                        break;
                    }
                case "DELETE":
                    if (Pattern.matches("^/tasks/task/$", path) && rawQuery != null) {
                        String taskId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(taskId);
                        handleDeleteTask(exchange, id);
                        break;

                    } else if (Pattern.matches("^/tasks/epic/$", path) && rawQuery != null) {
                        String epicId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(epicId);
                        handleDeleteEpic(exchange, id);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/", path) && rawQuery != null) {
                        String subtaskId = rawQuery.replaceFirst("id=", "");
                        int id = getPathId(subtaskId);
                        handleDeleteSubtask(exchange, id);
                        break;
                    } else if (Pattern.matches("^/tasks/epic/$", path)) {
                        handleDeleteEpics(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        handleDeleteSubtasks(exchange);
                        break;
                    } else if (Pattern.matches("^/tasks/task/$", path)) {
                        handleDeleteTasks(exchange);
                        break;

                    } else {
                        System.out.println("Такого эндпойнта с методом DELETE не существует");
                        exchange.sendResponseHeaders(400, 0);
                        break;
                    }
                default:
                    System.out.println("Такого эндпойнта не существует");
                    exchange.sendResponseHeaders(400, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void handlePostTask (HttpExchange exchange) throws IOException {
        Task task = null;
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes());
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Получен некорректный Json");
            exchange.sendResponseHeaders(400,  0);
        }
        if (task == null) {
            System.out.println("Поля задачи не могут быть пустыми");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        if (manager.getTaskById(task.getId()).isEmpty()) {
            try {
                manager.createTask(task);
                System.out.println("Задача создана");
                sendText(exchange, gson.toJson(task));
            } catch (TaskValidationException e) {
                System.out.println("Некорректное время задачи");
                exchange.sendResponseHeaders(400, 0);
            }

        } else {
            try {
                manager.updateTask(task);
                System.out.println("Задача обновлена");
                sendText(exchange, gson.toJson(task));

            } catch (TaskValidationException e) {
                System.out.println("Некорректное время задачи");
                exchange.sendResponseHeaders(400, 0);
            }

        }
    }

    private void handlePostEpic (HttpExchange exchange) throws IOException {
        Epic epic = null;
        InputStream inputStream = exchange.getRequestBody(); //name and description
        String body = new String(inputStream.readAllBytes());
        try {
            epic = gson.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Получен некорректный Json");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        if (epic == null) {
            System.out.println("Поля задачи не могут быть пустыми");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        if (manager.getEpicById(epic.getId()).isPresent()) {
            manager.updateEpic(epic);
            System.out.println("Эпик обновлен");
            sendText(exchange, gson.toJson(epic));
        } else {
        manager.createEpic(epic);
            System.out.println("Эпик создан");
            sendText(exchange, gson.toJson(epic));
        }
    }

    private void handlePostSubtask (HttpExchange exchange) throws IOException {
        Subtask subtask = null;
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes());
        try {
            subtask= gson.fromJson(body, Subtask.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Получен некорректный Json");
            exchange.sendResponseHeaders(400, 0);
        }

        if (subtask == null) {
            System.out.println("Поля задачи не могут быть пустыми");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        if (manager.getSubtaskById(subtask.getId()).isPresent()) {
            try {
                manager.updateSubtask(subtask);
            } catch (TaskValidationException e) {
                System.out.println("Некорректное время задачи, либо отсутствует эпик");
                exchange.sendResponseHeaders(400, 0);
            }
            System.out.println("Подзадача обновлена");
            sendText(exchange, gson.toJson(subtask));
        } else {
            try {
                manager.createSubtask(subtask);
            } catch (TaskValidationException e) {
                System.out.println("Некорректное время задачи, либо отсутствует эпик");
                exchange.sendResponseHeaders(400, 0);
            }
            System.out.println("Подзадача создана");
            sendText(exchange, gson.toJson(subtask));
        }
    }

    private void handleGetTasks (HttpExchange exchange) throws IOException {
        String tasksStr = gson.toJson(manager.getAllTasks());
        if (tasksStr.isEmpty()) {
            System.out.println("Задач нет");
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendText(exchange, tasksStr);
        }
    }

    private void handleGetEpics (HttpExchange exchange) throws IOException {
        String epicsStr = gson.toJson(manager.getAllEpics());
        if (epicsStr.isEmpty()) {
            System.out.println("Эпиков нет");
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendText(exchange, epicsStr);
        }
    }

    private void handleGetSubtasks (HttpExchange exchange) throws IOException {
        String subtasksStr = gson.toJson(manager.getAllTasks());
        if (subtasksStr.isEmpty()) {
            System.out.println("Подзадач нет");
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendText(exchange, subtasksStr);
        }
    }

    private void handleGetTask(HttpExchange exchange, int taskId) throws IOException {
        if (taskId == -1) {
            System.out.println("Некорректный id задачи: " + taskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        Optional<Task> task = manager.getTaskById(taskId);
        if (task.isEmpty()) {
            System.out.println("Некорректный id задачи: " + taskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        sendText(exchange, gson.toJson(task.get()) );
    }

    private void handleGetEpic(HttpExchange exchange, int epicId) throws IOException {
       if (epicId == -1) {
           System.out.println("Некорректный id эпика: " + epicId + ".");
           exchange.sendResponseHeaders(400, 0);
           return;
       }

        Optional<Epic> epic = manager.getEpicById(epicId);
        if (epic.isEmpty()) {
            System.out.println("Некорректный id эпика: " + epicId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        String epicStr = gson.toJson(epic.get());
        sendText(exchange, epicStr);
    }

    private void handleGetSubtask(HttpExchange exchange, int subtaskId) throws IOException {
        if (subtaskId == -1) {
            System.out.println("Некорректный id подзадачи: " + subtaskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        Optional<Subtask> subtask = manager.getSubtaskById(subtaskId);
        if (subtask.isEmpty()) {
            System.out.println("Некорректный id подзадачи: " + subtaskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        String subtaskStr = gson.toJson(subtask.get());
        sendText(exchange, subtaskStr);
    }

    private void handleDeleteTask(HttpExchange exchange, int taskId) throws IOException {
        if (taskId == -1) {
            System.out.println("Некорректный id задачи: " + taskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        Optional<Task> task = manager.getTaskById(taskId);
        if (task.isEmpty()) {
            System.out.println("Некорректный id задачи: " + taskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        manager.removeTaskById(taskId);
        System.out.println("Задача с id " + taskId + " удалена.");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleDeleteEpic(HttpExchange exchange, int epicId) throws IOException {
        if (epicId == -1) {
            System.out.println("Некорректный id эпика: " + epicId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        Optional<Epic> epic = manager.getEpicById(epicId);
        if (epic.isEmpty()) {
            System.out.println("Некорректный id эпика: " + epicId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        manager.removeEpicById(epicId);
        System.out.println("Эпик с id " + epicId + " удален.");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleDeleteSubtask(HttpExchange exchange, int subtaskId) throws IOException {
        if (subtaskId == -1) {
            System.out.println("Некорректный id подзадачи: " +  subtaskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }

        Optional<Subtask> subtask = manager.getSubtaskById(subtaskId);
        if (subtask.isEmpty()) {
            System.out.println("Некорректный id подзадачи: " +  subtaskId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        manager.removeSubtaskById(subtaskId);
        System.out.println("Подзадача с id " + subtaskId + " удалена.");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        manager.removeAllTasks();
        System.out.println("Все задачи удалены");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        manager.removeAllEpics();
        System.out.println("Все эпики удалены");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        manager.removeAllSubtasks();
        System.out.println("Все подзадачи удалены");
        exchange.sendResponseHeaders(200, 0);
    }

    private void handleGetSubtasksToEpic(HttpExchange exchange, int epicId) throws IOException {
        if (epicId == -1) {
            System.out.println("Некорректный id эпика: " + epicId + ".");
            exchange.sendResponseHeaders(400, 0);
            return;
        }
        String subtasksStr = gson.toJson(manager.getSubtasksToEpic(epicId));
        sendText(exchange, subtasksStr);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException{
       String historyStr = gson.toJson(manager.getHistory());
       if (historyStr.isEmpty()) {
           System.out.println("История просмотров пустая");
           exchange.sendResponseHeaders(200, 0);
       }
       sendText(exchange, historyStr);
    }

    private void handleGetPriority(HttpExchange exchange) throws IOException{
        String prioritizedTasksStr = gson.toJson(manager.getPrioritizedTasks());
        if (prioritizedTasksStr.isEmpty()) {
            System.out.println(" Задач нет");
            exchange.sendResponseHeaders(200, 0);
        }
        sendText(exchange, prioritizedTasksStr);
    }
    private int getPathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили HTTP-сервер  на порту " + PORT);
    }
}


