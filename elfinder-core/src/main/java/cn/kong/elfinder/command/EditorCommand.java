package cn.kong.elfinder.command;

import cn.kong.elfinder.service.ElfinderStorage;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class EditorCommand extends AbstractJsonCommand implements ElfinderCommand {

    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        json.put("OnlineConvert", true);
        json.put("ZipArchive", true);
        json.put("ZohoOffice", true);
    }
}
