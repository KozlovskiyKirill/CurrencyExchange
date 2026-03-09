package servlet;
import model.Currency;
import service.CurrencyService;

import javax.servlet.ServletException;//что это?
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;// что это?
import java.io.PrintWriter;// то это?
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet{//контроллер
    private CurrencyService _service = new CurrencyService();
    private List<Currency> _currencies;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        resp.setContentType("application/json;charset=UTF-8");
        System.out.println("In servlet");
        try {
            System.out.print("Зашли в сервлет, идем в сервис");
            _currencies = _service.getAllCurrencies();
            resp.getWriter().write(gson.toJson(_currencies));

        } catch (Exception e) {
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"DB error\"}");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException,IOException{
        resp.setContentType("application/json;charset=UTF-8");
        try{
            //сделать проверку параметров
            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");
            if (name.isEmpty()  || code.isEmpty() || sign.isEmpty()){
                throw new BadRequestException("Отсутствует нужное поле формы");
            }
            Currency currency = _service.addCurrency(name,code,sign);
            resp.getWriter().write(gson.toJson(currency));

        }
        catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"Error\":"+e.getMessage()+"\"}");
        }
        catch (CurrencyService.CurrencyAlreadyExistsException e){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{\"Message\":\"Валюта с таким кодом уже существует\"}");
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
        }
    }


    private static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
