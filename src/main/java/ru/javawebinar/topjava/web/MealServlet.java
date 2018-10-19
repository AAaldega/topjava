package ru.javawebinar.topjava.web;

import lombok.extern.slf4j.Slf4j;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoInMemory;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@WebServlet("/meals")
public class MealServlet extends HttpServlet {

    private final String DELETE = "delete";
    private final String EDIT = "edit";
    private final String CREATE = "create";
    private final String ALL = "all";

    private MealDao mealDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mealDao = new MealDaoInMemory();
        log.info("init finished");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("GET /meals {}", req.getMethod());

        String action = req.getParameter("action");

        switch (action == null ? ALL : action) {
            case DELETE: deleteMeal(req);
                break;
            case EDIT: editMeal(req, resp);
                break;
            case CREATE: createMeal(req, resp);
                break;
        }

        showAll(req, resp);
    }

    private void showAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<MealWithExceed> filteredWithExceeded =
                MealUtil.getFilteredWithExceeded(
                        mealDao.findAll(),
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
        int id = getIdFromRequest(req);
        Meal meal = mealDao.find(id);
        log.info("edit meal : {}", meal);
        req.setAttribute("meal", meal);
        req.getRequestDispatcher("mealForm.jsp").forward(req, resp);
    }

    private void deleteMeal(HttpServletRequest req) {
        int id = getIdFromRequest(req);
        log.info("delete meal id = {}", id);
        mealDao.deleteMeal(id);
    }

    private int getIdFromRequest(HttpServletRequest req) {
        String strId = req.getParameter("id");
        if (isEmpty(strId)) {
            throw new RuntimeException("id is empty");
        }
        return Integer.parseInt(strId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        log.info("POST /meals {}", req.getMethod());

        req.setCharacterEncoding(UTF_8.displayName());

        String description = req.getParameter("description").trim();
        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
        int calories = Integer.parseInt(req.getParameter("calories"));

        if (isBlank(req.getParameter("id"))) {
            mealDao.addMeal(new Meal(0, dateTime, description, calories));
        } else {
            int id = Integer.parseInt(req.getParameter("id"));
            mealDao.updateMeal(new Meal(id, dateTime, description, calories));
        }

        resp.sendRedirect("meals");
    }
}
