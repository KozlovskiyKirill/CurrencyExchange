package servlet;


import com.google.gson.Gson;
import model.ExchangeCurrency;
import service.ExchangeRatesService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeCurrencyServlet extends HttpServlet {
    private ExchangeRatesService _service = new ExchangeRatesService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try{
            String baseCode = req.getParameter("from");
            String targetCode = req.getParameter("to");
            String sAmount = req.getParameter("amount");
            if(baseCode.trim().isEmpty() || targetCode.trim().isEmpty() || sAmount.trim().isEmpty()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("\"message\": \"Валюта не найдена\"");
            }
            double amount = Double.parseDouble(sAmount);
            ExchangeCurrency exchange = _service.ExchangeCurrency(baseCode, targetCode, amount);
            resp.getWriter().write(gson.toJson(exchange));
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"DB error\"}");
        }

    }
}
