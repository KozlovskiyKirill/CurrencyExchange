package servlet;
import dto.CurrencyResponseDto;
import dto.DtoMapper;
import exceptions.BadRequestException;
import exceptions.CurrencyAlreadyExistsException;
import model.Currency;
import service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet{
    private final CurrencyService _service = new CurrencyService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("In servlet");
        try {
            System.out.print("Зашли в сервлет, идем в сервис");
            List<Currency> _currencies = _service.getAllCurrencies();
            List<CurrencyResponseDto> currenciesDto = _currencies.stream()
                    .map(DtoMapper::toCurrencyDto)
                    .collect(Collectors.toList());
            resp.getWriter().write(gson.toJson(currenciesDto));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException{
        try{
            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");
            if (name == null || name.trim().isEmpty() ||
                    code == null || code.trim().isEmpty() ||
                    sign == null || sign.trim().isEmpty()) {
                throw new BadRequestException("Отсутствует нужное поле формы");
            }
            Currency currency = _service.addCurrency(name,code,sign);
            CurrencyResponseDto currencyDto = DtoMapper.toCurrencyDto(currency);
            resp.getWriter().write(gson.toJson(currencyDto));

        }
        catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"Error\":"+e.getMessage()+"\"}");
        }
        catch (CurrencyAlreadyExistsException e){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{\"Message\":\"Валюта с таким кодом уже существует\"}");
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"Error\":\"база данных недоступна\"}");
        }
    }


}
