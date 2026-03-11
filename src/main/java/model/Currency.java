package model;

public class Currency {
    private final int _id;
    private final String _code;
    private final String _fullName;
    private final String _sign;



    public Currency (int id, String fullName, String code, String sign){//модель
        // прокинуть проверки, пока на доверии
        _id = id;
        _code = code;
        _fullName = fullName;
        _sign = sign;
    }

    public int get_id(){
        return _id;
    }

    public String get_code(){
        return _code;
    }

    public String get_fullName(){
        return _fullName;
    }
    public String get_sign(){
        return _sign;
    }
}
