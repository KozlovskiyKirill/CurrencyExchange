package servlet;


import com.google.gson.Gson;
import dto.DtoMapper;
import dto.ExchangeCurrencyResponseDto;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateNotFoundException;
import model.ExchangeCurrency;
import service.ExchangeRatesService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeCurrencyServlet extends HttpServlet {
    private ExchangeRatesService _service = new ExchangeRatesService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            String baseCode = req.getParameter("from");
            String targetCode = req.getParameter("to");
            String sAmount = req.getParameter("amount");
            if(baseCode == null || baseCode.trim().isEmpty() ||
               targetCode == null || targetCode.trim().isEmpty() ||
               sAmount == null || sAmount.trim().isEmpty()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"message\":\"Отсутствует нужное поле формы\"}");
                return;
            }
            BigDecimal amount = new BigDecimal(sAmount);
            ExchangeCurrency exchange = _service.ExchangeCurrency(baseCode, targetCode, amount);
            ExchangeCurrencyResponseDto exchangeDto = DtoMapper.toExchangeCurrencyDto(exchange);
            resp.getWriter().write(gson.toJson(exchangeDto));
        }
        catch (CurrencyNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"message\":\"Валюта не найдена\"}");
        }
        catch (ExchangeRateNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"message\":\"Обменный курс не найден\"}");
        }
        catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\":\"база данных недоступна\"}");
        }

    }
}
