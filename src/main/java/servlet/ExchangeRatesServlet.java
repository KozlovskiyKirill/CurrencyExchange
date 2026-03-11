package servlet;

import model.ExchangeRate;
import org.apache.commons.lang3.tuple.Pair;
import service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;



import com.google.gson.Gson;
import service.ExchangeRatesService;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet{
    private ExchangeRatesService _service = new ExchangeRatesService();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        resp.setContentType("application/json;charset=UTF-8");
        try{
            List<ExchangeRate> rates = _service.getAllExchangeRates();
            resp.getWriter().write(gson.toJson(rates));

        }
        catch (Exception e){
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"DB error\"}");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException{
        resp.setContentType("application/json;charset=UTF-8");
    }
}
