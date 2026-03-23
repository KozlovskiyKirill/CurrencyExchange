package servlet;

import dto.DtoMapper;
import dto.ExchangeRateResponseDto;
import exceptions.BadRequestException;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateAlreadyExistsException;
import model.ExchangeRate;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import service.ExchangeRatesService;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet{
    private ExchangeRatesService _service = new ExchangeRatesService();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        try{
            List<ExchangeRate> rates = _service.getAllExchangeRates();
            List<ExchangeRateResponseDto> ratesDto = rates.stream()
                    .map(DtoMapper::toExchangeRateDto)
                    .collect(Collectors.toList());
            resp.getWriter().write(gson.toJson(ratesDto));

        }
        catch (Exception e){
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"DB error\"}");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException{
        try{
            String baseCurrency = req.getParameter("baseCurrencyCode");
            String targetCurrency = req.getParameter("targetCurrencyCode");
            String s_rate = req.getParameter("rate");
            if(baseCurrency.trim().isEmpty() || targetCurrency.trim().isEmpty() || s_rate.trim().isEmpty()){
                throw new BadRequestException("Отсутствует нужное поле формы");
            }
            BigDecimal rate = new BigDecimal(s_rate);
            ExchangeRate newRate = _service.addNewExchangeRate(baseCurrency,targetCurrency,rate);
            ExchangeRateResponseDto newRateDto = DtoMapper.toExchangeRateDto(newRate);
            resp.getWriter().write(gson.toJson(newRateDto));

        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("{\"Error\":"+e.getMessage()+"\"}"));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("{\"Error\":"+e.getMessage()+"\"}"));
        } catch (ExchangeRateAlreadyExistsException e){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(gson.toJson("{\"Error\":Валютная пара с таким кодом уже существует\"}"));
        } catch(CurrencyNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson("{\"Error\":"+e.getMessage()+"\"}"));
        }
    }
}
