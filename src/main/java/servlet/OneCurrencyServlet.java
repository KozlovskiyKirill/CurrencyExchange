package servlet;

import com.google.protobuf.Message;
import service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import model.Currency;
import java.util.List;
import com.google.gson.Gson;
import service.CurrencyService.CurrencyNotFoundException;

@WebServlet("/currencies/*")
public class OneCurrencyServlet extends HttpServlet {
    private final CurrencyService _service = new CurrencyService();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        if (pathInfo == null || pathInfo.equals("/")) {
            // Если просто /currency без кода валюты
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Нет кода валюты");
        }
        else{
            try {
                String currencyCode = pathInfo.substring(1);
                Currency currency = _service.findCurrency(currencyCode);
                resp.getWriter().write(gson.toJson(currency));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
            }
            catch (CurrencyNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Валюта не найдена");
            }
        }
    }

}


