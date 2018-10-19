package ru.javawebinar.topjava.web;

import lombok.extern.slf4j.Slf4j;
import ru.javawebinar.topjava.dao.MealRepository;
import ru.javawebinar.topjava.dao.MealRepositoryInMemory;
import ru.javawebinar.topjava.dto.MealWithExceed;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@WebServlet("/meals")
public class MealServlet extends HttpServlet {

    private MealRepository mealRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mealRepository = new MealRepositoryInMemory();
        log.info("init finished");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("GET /meals {}", req.getMethod());

        String action = req.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete": deleteMeal(req);
                break;
            case "edit": editMeal(req, resp);
                break;
            case "create": createMeal(req, resp);
                break;
        }

        showAll(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(UTF_8.displayName());

        String id = req.getParameter("id");

        Meal meal = new Meal(
                isBlank(id) ? 0 : parseInt(id),
                LocalDateTime.parse(req.getParameter("dateTime")),
                req.getParameter("description"),
                parseInt(req.getParameter("calories"))
        );

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        mealRepository.save(meal);

        resp.sendRedirect("meals");
    }

    private void showAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<MealWithExceed> filteredWithExceeded =
                MealUtil.getFilteredWithExceeded(
                        mealRepository.getAll(),
                        LocalTime.MIN,
                        LocalTime.MAX,
                        2000);
        req.setAttribute("meals", filteredWithExceeded);
        req.getRequestDispatcher("meals.jsp").forward(req, resp);
    }

    private void createMeal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("create new Meal");
        req.getRequestDispatcher("mealForm.jsp").forward(req, resp);
    }

    private void editMeal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = getId(req);
        Meal meal = mealRepository.get(id);
        log.info("edit meal : {}", meal);
        req.setAttribute("meal", meal);
        req.getRequestDispatcher("mealForm.jsp").forward(req, resp);
    }

    private void deleteMeal(HttpServletRequest req) {
        int id = getId(req);
        log.info("delete meal id = {}", id);
        mealRepository.delete(id);
    }

    private int getId(HttpServletRequest req) {
        String strId = Objects.requireNonNull(req.getParameter("id"), "id is null");
        return parseInt(strId);
    }

}
