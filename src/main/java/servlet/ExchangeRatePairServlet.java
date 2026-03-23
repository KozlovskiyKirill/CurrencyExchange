package servlet;
import dto.DtoMapper;
import dto.ExchangeRateResponseDto;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateNotFoundException;
import model.ExchangeRate;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.google.gson.Gson;
import service.ExchangeRatesService;


@WebServlet("/exchangeRate/*")
public class ExchangeRatePairServlet extends HttpServlet {
    private ExchangeRatesService _service = new ExchangeRatesService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Нет кода валюты");
        }
        else{
            try {
                String pair = pathInfo.substring(1);
                String baseCurrencyCode = pair.substring(0, 3);
                String targetCurrencyCode = pair.substring(3, 6);
                ExchangeRate rate = _service.findExchangeRatePairByCode(baseCurrencyCode,targetCurrencyCode);
                ExchangeRateResponseDto rateDto = DtoMapper.toExchangeRateDto(rate);
                resp.getWriter().write(gson.toJson(rateDto));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
            }
            catch (CurrencyNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Валюта не найдена");
            }
            catch (ExchangeRateNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Обменный курс не найден");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String methodOverride = request.getHeader("X-HTTP-Method-Override");
        if ("PATCH".equalsIgnoreCase(methodOverride)) {
            doPatch(request, response);
        }
    }
    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Нет кода валюты");
        }
        else{
            try {
                String pair = pathInfo.substring(1);
                String baseCurrencyCode = pair.substring(0, 3);
                String targetCurrencyCode = pair.substring(3, 6);
                String SRate = req.getParameter("rate");
                if (SRate.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("нет обменного курса");
                }
                BigDecimal newRate = new BigDecimal(SRate);
                ExchangeRate rate = _service.UpdateExchangeRate(baseCurrencyCode,targetCurrencyCode, newRate);
                ExchangeRateResponseDto rateDto = DtoMapper.toExchangeRateDto(rate);
                resp.getWriter().write(gson.toJson(rateDto));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
            }
            catch (CurrencyNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Валюта не найдена");
            }
            catch (ExchangeRateNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Обменный курс не найден");
            }
        }
    }
}
