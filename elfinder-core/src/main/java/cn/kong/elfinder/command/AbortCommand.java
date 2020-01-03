package cn.kong.elfinder.command;

import cn.kong.elfinder.service.ElfinderStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbortCommand extends AbstractCommand implements ElfinderCommand {
    @Override
    public void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.sendError(204, "No contents！");
    }
}
