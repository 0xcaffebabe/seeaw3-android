package wang.ismy.seeaw3.dto;


import java.util.HashMap;
import java.util.Map;

public class Result {

    private String msg;

    private Map<String,Object> result = new HashMap<>();

    public Result msg(String msg){
        this.msg = msg;
        return this;
    }

    public Result result(String key,Object value){
        result.put(key,value);
        return this;
    }

    public Object result(String key){
        return result.get(key);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
