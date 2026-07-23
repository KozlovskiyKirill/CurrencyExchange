package servlet;
import dto.DtoMapper;
import dto.ExchangeRateResponseDto;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateNotFoundException;
import model.ExchangeRate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Коды валют пары отсутствуют в адресе\"}");
        }
        else{
            try {
                String pair = pathInfo.substring(1);
                if (pair.length() < 6) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"message\":\"Коды валют пары отсутствуют в адресе\"}");
                    return;
                }
                String baseCurrencyCode = pair.substring(0, 3);
                String targetCurrencyCode = pair.substring(3, 6);
                ExchangeRate rate = _service.findExchangeRatePairByCode(baseCurrencyCode,targetCurrencyCode);
                ExchangeRateResponseDto rateDto = DtoMapper.toExchangeRateDto(rate);
                resp.getWriter().write(gson.toJson(rateDto));
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"message\":\"база данных недоступна\"}");
            }
            catch (CurrencyNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Валюта не найдена\"}");
            }
            catch (ExchangeRateNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Обменный курс не найден\"}");
            }
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"Коды валют пары отсутствуют в адресе\"}");
        }
        else{
            try {
                String pair = pathInfo.substring(1);
                if (pair.length() < 6) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"message\":\"Коды валют пары отсутствуют в адресе\"}");
                    return;
                }
                String baseCurrencyCode = pair.substring(0, 3);
                String targetCurrencyCode = pair.substring(3, 6);
                String SRate = null;
                String contentType = req.getContentType();
                if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
                    BufferedReader reader = req.getReader();
                    String body = reader.lines().reduce("", String::concat);
                    for (String param : body.split("&")) {
                        String[] kv = param.split("=");
                        if (kv.length == 2 && "rate".equals(kv[0])) {
                            SRate = kv[1];
                        }
                    }
                }
                if (SRate == null || SRate.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"message\":\"Отсутствует нужное поле формы\"}");
                    return;
                }
                BigDecimal newRate = new BigDecimal(SRate);
                ExchangeRate rate = _service.UpdateExchangeRate(baseCurrencyCode,targetCurrencyCode, newRate);
                ExchangeRateResponseDto rateDto = DtoMapper.toExchangeRateDto(rate);
                resp.getWriter().write(gson.toJson(rateDto));
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"message\":\"база данных недоступна\"}");
            }
            catch (CurrencyNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Валюта не найдена\"}");
            }
            catch (ExchangeRateNotFoundException e){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"message\":\"Обменный курс не найден\"}");
            }
        }
    }
}
